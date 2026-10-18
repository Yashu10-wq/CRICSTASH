package com.cricketscore.scoring;

import com.cricketscore.dto.request.DeliveryCorrectionRequest;
import com.cricketscore.dto.request.DeliveryRequest;
import com.cricketscore.entity.*;
import com.cricketscore.entity.enums.*;
import com.cricketscore.exception.InvalidMatchStateException;
import com.cricketscore.exception.InvalidScoringOperationException;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.repository.*;
import com.cricketscore.websocket.ScoreUpdateMessage;
import com.cricketscore.websocket.WebSocketEventPublisher;
import com.cricketscore.websocket.WebSocketEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CricketScoringEngineImpl implements CricketScoringEngine {

    private static final Logger log = LoggerFactory.getLogger(CricketScoringEngineImpl.class);

    private final InningsRepository inningsRepository;
    private final BattingScoreRepository battingScoreRepository;
    private final BowlingFigureRepository bowlingFigureRepository;
    private final BallEventRepository ballEventRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final MatchSquadRepository matchSquadRepository;
    private final WebSocketEventPublisher eventPublisher;

    public CricketScoringEngineImpl(InningsRepository inningsRepository,
                                    BattingScoreRepository battingScoreRepository,
                                    BowlingFigureRepository bowlingFigureRepository,
                                    BallEventRepository ballEventRepository,
                                    MatchRepository matchRepository,
                                    PlayerRepository playerRepository,
                                    MatchSquadRepository matchSquadRepository,
                                    WebSocketEventPublisher eventPublisher) {
        this.inningsRepository = inningsRepository;
        this.battingScoreRepository = battingScoreRepository;
        this.bowlingFigureRepository = bowlingFigureRepository;
        this.ballEventRepository = ballEventRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.matchSquadRepository = matchSquadRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public DeliveryOutcome processDelivery(Long inningsId, DeliveryRequest request) {
        Innings innings = inningsRepository.findByIdForUpdate(inningsId)
                .orElseThrow(() -> new ResourceNotFoundException("Innings not found with id: " + inningsId));

        Match match = innings.getMatch();

        if (innings.getStatus() == InningsStatus.COMPLETED || innings.getStatus() == InningsStatus.DECLARED) {
            throw new InvalidMatchStateException("Cannot record delivery for an innings that is already completed or declared.");
        }
        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.CANCELLED) {
            throw new InvalidMatchStateException("Cannot record delivery for a match in status: " + match.getStatus());
        }

        if (innings.getStatus() == InningsStatus.NOT_STARTED) {
            innings.setStatus(InningsStatus.IN_PROGRESS);
            match.setStatus(MatchStatus.LIVE);
        }

        Player striker = innings.getCurrentStriker();
        Player nonStriker = innings.getCurrentNonStriker();
        Player bowler = innings.getCurrentBowler();

        if (striker == null || nonStriker == null || bowler == null) {
            throw new InvalidScoringOperationException("Innings must have active striker, non-striker, and bowler before deliveries can be recorded.");
        }

        if (striker.getId().equals(nonStriker.getId())) {
            throw new InvalidScoringOperationException("Striker and non-striker cannot be the same player.");
        }
        if (bowler.getId().equals(striker.getId()) || bowler.getId().equals(nonStriker.getId())) {
            throw new InvalidScoringOperationException("Bowler cannot be currently batting as striker or non-striker.");
        }

        ExtraType extraType = request.getExtraType() != null ? request.getExtraType() : ExtraType.NONE;
        boolean isLegalDelivery = extraType.isLegalDelivery();

        int batterRuns = request.getBatterRuns() != null ? request.getBatterRuns() : 0;
        int extraRuns = request.getExtraRuns() != null ? request.getExtraRuns() : 0;

        // Auto-apply 1 run penalty for Wide / No-ball if not explicitly specified
        if (extraType == ExtraType.WIDE && extraRuns == 0) {
            extraRuns = 1;
        } else if (extraType == ExtraType.NO_BALL && extraRuns == 0) {
            extraRuns = 1;
        }

        int totalRuns = batterRuns + extraRuns;

        DismissalType wicketType = request.getWicketType();
        boolean isWicket = (wicketType != null && wicketType != DismissalType.NOT_OUT);

        // Enforce cricket dismissal legality rules
        if (isWicket) {
            validateDismissalLegality(extraType, wicketType);
        }

        // Over and delivery sequence calculations
        int currentLegalBalls = innings.getLegalBallsBowled();
        int overNumber = currentLegalBalls / 6;
        int deliveryIndex = (currentLegalBalls % 6) + 1;
        long eventSequence = ballEventRepository.findMaxEventSequence(innings.getId()) + 1;

        Player dismissedPlayer = null;
        Player fielder = null;

        if (isWicket) {
            if (request.getDismissedPlayerId() != null) {
                dismissedPlayer = playerRepository.findById(request.getDismissedPlayerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dismissed player not found with id: " + request.getDismissedPlayerId()));
            } else {
                dismissedPlayer = striker; // default dismissed is striker unless non-striker was run out
            }

            if (request.getFielderId() != null) {
                fielder = playerRepository.findById(request.getFielderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Fielder not found with id: " + request.getFielderId()));
            }
        }

        // 1. Create and persist the BallEvent audit record
        BallEvent ballEvent = new BallEvent(
                innings, overNumber, deliveryIndex, eventSequence,
                striker, nonStriker, bowler,
                batterRuns, extraRuns, totalRuns,
                extraType, isLegalDelivery,
                isWicket ? wicketType : null, dismissedPlayer, fielder,
                request.getCommentary()
        );
        ballEvent = ballEventRepository.save(ballEvent);

        final Player currentStriker = striker;
        final Player currentNonStriker = nonStriker;
        final Player currentBowler = bowler;

        // 2. Update Batting Scores
        BattingScore strikerScore = battingScoreRepository.findByInningsIdAndPlayerId(innings.getId(), currentStriker.getId())
                .orElse(null);
        if (strikerScore == null) {
            strikerScore = createBattingScore(innings, currentStriker);
        }

        BattingScore nonStrikerScore = battingScoreRepository.findByInningsIdAndPlayerId(innings.getId(), currentNonStriker.getId())
                .orElse(null);
        if (nonStrikerScore == null) {
            nonStrikerScore = createBattingScore(innings, currentNonStriker);
        }

        // Wide deliveries do not count as a ball faced by the batter
        if (extraType != ExtraType.WIDE) {
            strikerScore.setBallsFaced(strikerScore.getBallsFaced() + 1);
        }
        strikerScore.setRuns(strikerScore.getRuns() + batterRuns);
        if (batterRuns == 4) {
            strikerScore.setFours(strikerScore.getFours() + 1);
        } else if (batterRuns == 6) {
            strikerScore.setSixes(strikerScore.getSixes() + 1);
        }

        // 3. Update Bowling Figures
        BowlingFigure bowlerFigure = bowlingFigureRepository.findByInningsIdAndPlayerId(innings.getId(), currentBowler.getId())
                .orElse(null);
        if (bowlerFigure == null) {
            bowlerFigure = bowlingFigureRepository.save(new BowlingFigure(innings, currentBowler));
        }

        if (isLegalDelivery) {
            bowlerFigure.setLegalBallsBowled(bowlerFigure.getLegalBallsBowled() + 1);
        }

        // Runs charged to bowler (batter runs + bowler-conceded extras such as wides/no-balls)
        int runsChargedToBowler = batterRuns + (extraType.isBowlerConceded() ? extraRuns : 0);
        bowlerFigure.setRunsConceded(bowlerFigure.getRunsConceded() + runsChargedToBowler);

        if (extraType == ExtraType.WIDE) {
            bowlerFigure.setWides(bowlerFigure.getWides() + 1);
        } else if (extraType == ExtraType.NO_BALL) {
            bowlerFigure.setNoBalls(bowlerFigure.getNoBalls() + 1);
        }

        if (totalRuns == 0) {
            bowlerFigure.setDots(bowlerFigure.getDots() + 1);
        }

        // 4. Handle Wickets
        if (isWicket) {
            innings.setWickets(innings.getWickets() + 1);

            if (wicketType.isCreditedToBowler()) {
                bowlerFigure.setWickets(bowlerFigure.getWickets() + 1);
            }

            BattingScore dismissedScore = dismissedPlayer.getId().equals(striker.getId()) ? strikerScore : nonStrikerScore;
            dismissedScore.setDismissalType(wicketType);
            dismissedScore.setDismissedByBowler(wicketType.isCreditedToBowler() ? bowler : null);
            dismissedScore.setAssistedByPlayer(fielder);
            dismissedScore.setOnStrike(false);

            // Handle incoming batter if not all out
            if (innings.getWickets() < 10 && request.getNewBatterId() != null) {
                Player newBatter = playerRepository.findById(request.getNewBatterId())
                        .orElseThrow(() -> new ResourceNotFoundException("New batter not found with id: " + request.getNewBatterId()));

                createBattingScore(innings, newBatter);

                if (dismissedPlayer.getId().equals(striker.getId())) {
                    innings.setCurrentStriker(newBatter);
                    striker = newBatter;
                } else {
                    innings.setCurrentNonStriker(newBatter);
                    nonStriker = newBatter;
                }
            }
        }

        // 5. Update Innings totals
        innings.setTotalRuns(innings.getTotalRuns() + totalRuns);
        if (isLegalDelivery) {
            innings.setLegalBallsBowled(innings.getLegalBallsBowled() + 1);
        }

        switch (extraType) {
            case WIDE -> innings.setWides(innings.getWides() + extraRuns);
            case NO_BALL -> innings.setNoBalls(innings.getNoBalls() + extraRuns);
            case BYE -> innings.setByes(innings.getByes() + extraRuns);
            case LEG_BYE -> innings.setLegByes(innings.getLegByes() + extraRuns);
            case PENALTY -> innings.setPenaltyRuns(innings.getPenaltyRuns() + extraRuns);
            default -> {}
        }

        // 6. Strike rotation from runs completed
        int runningRuns = batterRuns + (extraType == ExtraType.BYE || extraType == ExtraType.LEG_BYE ? extraRuns : 0);
        if (runningRuns % 2 != 0) {
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;
            innings.setCurrentStriker(striker);
            innings.setCurrentNonStriker(nonStriker);
        }

        // 7. Over Completion Check
        boolean overCompleted = false;
        if (isLegalDelivery && (innings.getLegalBallsBowled() % 6 == 0)) {
            overCompleted = true;

            // Maiden check for completed over
            checkAndAwardMaidenOver(innings, bowlerFigure, overNumber);

            // End of over strike rotation
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;
            innings.setCurrentStriker(striker);
            innings.setCurrentNonStriker(nonStriker);

            innings.setLastBowler(bowler);

            // Check if next bowler is assigned
            if (request.getNextBowlerId() != null) {
                if (request.getNextBowlerId().equals(bowler.getId())) {
                    throw new InvalidScoringOperationException("Bowler cannot bowl two consecutive overs.");
                }
                Player nextBowler = playerRepository.findById(request.getNextBowlerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Next bowler not found with id: " + request.getNextBowlerId()));
                innings.setCurrentBowler(nextBowler);
            }
        }

        // 8. Innings and Match Completion evaluation
        boolean inningsCompleted = false;
        boolean matchCompleted = false;

        Integer targetRuns = innings.getTargetRuns();
        Integer maxOvers = match.getMaxOversPerInnings();
        int maxLegalBalls = (maxOvers != null && maxOvers > 0) ? maxOvers * 6 : Integer.MAX_VALUE;

        // Condition A: Target successfully chased
        if (targetRuns != null && innings.getTotalRuns() >= targetRuns) {
            inningsCompleted = true;
            matchCompleted = true;
            innings.setStatus(InningsStatus.COMPLETED);
            match.setStatus(MatchStatus.COMPLETED);
            match.setWinnerTeam(innings.getBattingTeam());
            match.setResultType(MatchResultType.WIN_BY_WICKETS);
            int wicketsRemaining = 10 - innings.getWickets();
            match.setResultSummary(innings.getBattingTeam().getName() + " won by " + wicketsRemaining + " wickets");
        }
        // Condition B: All out or max overs reached
        else if (innings.getWickets() >= 10 || innings.getLegalBallsBowled() >= maxLegalBalls) {
            inningsCompleted = true;
            innings.setStatus(InningsStatus.COMPLETED);

            // If this was the 2nd innings of a limited overs match, match completes
            if (innings.getInningsNumber() == 2 && targetRuns != null) {
                matchCompleted = true;
                match.setStatus(MatchStatus.COMPLETED);

                if (Objects.equals(innings.getTotalRuns(), targetRuns - 1)) {
                    match.setResultType(MatchResultType.TIE);
                    match.setResultSummary("Match tied");
                } else if (innings.getTotalRuns() < targetRuns - 1) {
                    match.setWinnerTeam(innings.getBowlingTeam());
                    match.setResultType(MatchResultType.WIN_BY_RUNS);
                    int runDiff = (targetRuns - 1) - innings.getTotalRuns();
                    match.setResultSummary(innings.getBowlingTeam().getName() + " won by " + runDiff + " runs");
                }
            } else if (innings.getInningsNumber() == 1) {
                match.setStatus(MatchStatus.INNINGS_BREAK);
            }
        }

        // Update on-strike markers
        strikerScore.setOnStrike(true);
        nonStrikerScore.setOnStrike(false);

        battingScoreRepository.save(strikerScore);
        battingScoreRepository.save(nonStrikerScore);
        bowlingFigureRepository.save(bowlerFigure);
        matchRepository.save(match);
        innings = inningsRepository.save(innings);

        // 9. Send live WebSocket event notification (synchronizes after transaction commit)
        broadcastDeliveryUpdate(innings, ballEvent, isWicket, overCompleted, inningsCompleted, matchCompleted);

        return new DeliveryOutcome(innings, ballEvent, strikerScore, nonStrikerScore, bowlerFigure,
                overCompleted, inningsCompleted, matchCompleted);
    }

    private void validateDismissalLegality(ExtraType extraType, DismissalType wicketType) {
        if (extraType == ExtraType.NO_BALL) {
            if (wicketType == DismissalType.BOWLED || wicketType == DismissalType.CAUGHT ||
                wicketType == DismissalType.LBW || wicketType == DismissalType.STUMPED ||
                wicketType == DismissalType.HIT_WICKET) {
                throw new InvalidScoringOperationException("Batter cannot be dismissed " + wicketType + " off a No-ball.");
            }
        } else if (extraType == ExtraType.WIDE) {
            if (wicketType == DismissalType.BOWLED || wicketType == DismissalType.CAUGHT ||
                wicketType == DismissalType.LBW) {
                throw new InvalidScoringOperationException("Batter cannot be dismissed " + wicketType + " off a Wide.");
            }
        }
    }

    private void checkAndAwardMaidenOver(Innings innings, BowlingFigure bowlerFigure, int overNumber) {
        // Query deliveries from this specific completed over
        List<BallEvent> overDeliveries = ballEventRepository.findByInningsIdAndUndoneFalseOrderByEventSequenceAsc(innings.getId())
                .stream()
                .filter(b -> b.getOverNumber() == overNumber)
                .toList();

        int runsConcededInOver = overDeliveries.stream()
                .mapToInt(b -> b.getBatterRuns() + (b.getExtraType().isBowlerConceded() ? b.getExtraRuns() : 0))
                .sum();

        if (runsConcededInOver == 0 && overDeliveries.stream().filter(BallEvent::isLegalDelivery).count() == 6) {
            bowlerFigure.setMaidens(bowlerFigure.getMaidens() + 1);
        }
    }

    private BattingScore createBattingScore(Innings innings, Player player) {
        Integer maxPos = battingScoreRepository.findMaxBattingPosition(innings.getId());
        int nextPos = (maxPos != null ? maxPos : 0) + 1;
        BattingScore score = new BattingScore(innings, player, nextPos);
        return battingScoreRepository.save(score);
    }

    @Override
    @Transactional
    public Innings undoLastDelivery(Long inningsId) {
        Innings innings = inningsRepository.findByIdForUpdate(inningsId)
                .orElseThrow(() -> new ResourceNotFoundException("Innings not found with id: " + inningsId));

        BallEvent lastBall = ballEventRepository.findTopByInningsIdAndUndoneFalseOrderByEventSequenceDesc(inningsId)
                .orElseThrow(() -> new InvalidScoringOperationException("No recorded deliveries to undo for innings id: " + inningsId));

        lastBall.setUndone(true);
        ballEventRepository.save(lastBall);

        // Recalculate complete scorecard from remaining active deliveries to guarantee absolute consistency
        recalculateInningsTotals(innings);

        // Publish WebSocket undo event
        ScoreUpdateMessage message = new ScoreUpdateMessage();
        message.setEventType(WebSocketEventType.DELIVERY_UNDONE);
        message.setMatchId(innings.getMatch().getId());
        message.setInningsId(innings.getId());
        message.setInningsNumber(innings.getInningsNumber());
        message.setEventSequence(lastBall.getEventSequence());
        message.setTotalRuns(innings.getTotalRuns());
        message.setWickets(innings.getWickets());
        message.setOvers(innings.getOversFormatted());
        message.setCurrentRunRate(innings.getCurrentRunRate());
        message.setLastDeliveryDescription("Undone delivery sequence " + lastBall.getEventSequence());
        eventPublisher.publishScoreUpdate(message);

        return innings;
    }

    @Override
    @Transactional
    public Innings correctDelivery(Long inningsId, DeliveryCorrectionRequest request) {
        Innings innings = inningsRepository.findByIdForUpdate(inningsId)
                .orElseThrow(() -> new ResourceNotFoundException("Innings not found with id: " + inningsId));

        List<BallEvent> events = ballEventRepository.findByInningsIdAndUndoneFalseOrderByEventSequenceAsc(inningsId);
        BallEvent targetEvent = events.stream()
                .filter(e -> Objects.equals(e.getEventSequence(), request.getEventSequence()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ball event sequence " + request.getEventSequence() + " not found."));

        int batterRuns = request.getBatterRuns() != null ? request.getBatterRuns() : 0;
        int extraRuns = request.getExtraRuns() != null ? request.getExtraRuns() : 0;
        ExtraType extraType = request.getExtraType() != null ? request.getExtraType() : ExtraType.NONE;

        targetEvent.setBatterRuns(batterRuns);
        targetEvent.setExtraRuns(extraRuns);
        targetEvent.setTotalRuns(batterRuns + extraRuns);
        targetEvent.setExtraType(extraType);
        targetEvent.setLegalDelivery(extraType.isLegalDelivery());
        targetEvent.setWicketType(request.getWicketType());
        targetEvent.setCommentary(targetEvent.getCommentary() + " [Correction: " + request.getReason() + "]");
        ballEventRepository.save(targetEvent);

        // Recalculate state cleanly from event history
        recalculateInningsTotals(innings);

        ScoreUpdateMessage message = new ScoreUpdateMessage();
        message.setEventType(WebSocketEventType.DELIVERY_CORRECTED);
        message.setMatchId(innings.getMatch().getId());
        message.setInningsId(innings.getId());
        message.setInningsNumber(innings.getInningsNumber());
        message.setEventSequence(targetEvent.getEventSequence());
        message.setTotalRuns(innings.getTotalRuns());
        message.setWickets(innings.getWickets());
        message.setOvers(innings.getOversFormatted());
        message.setCurrentRunRate(innings.getCurrentRunRate());
        message.setLastDeliveryDescription("Corrected delivery sequence " + targetEvent.getEventSequence());
        eventPublisher.publishScoreUpdate(message);

        return innings;
    }

    private void recalculateInningsTotals(Innings innings) {
        List<BallEvent> activeDeliveries = ballEventRepository.findByInningsIdAndUndoneFalseOrderByEventSequenceAsc(innings.getId());

        int totalRuns = 0;
        int wickets = 0;
        int legalBalls = 0;
        int byes = 0;
        int legByes = 0;
        int wides = 0;
        int noBalls = 0;
        int penaltyRuns = 0;

        for (BallEvent be : activeDeliveries) {
            totalRuns += be.getTotalRuns();
            if (be.isLegalDelivery()) {
                legalBalls++;
            }
            if (be.getWicketType() != null && be.getWicketType() != DismissalType.NOT_OUT) {
                wickets++;
            }
            switch (be.getExtraType()) {
                case WIDE -> wides += be.getExtraRuns();
                case NO_BALL -> noBalls += be.getExtraRuns();
                case BYE -> byes += be.getExtraRuns();
                case LEG_BYE -> legByes += be.getExtraRuns();
                case PENALTY -> penaltyRuns += be.getExtraRuns();
                default -> {}
            }
        }

        innings.setTotalRuns(totalRuns);
        innings.setWickets(wickets);
        innings.setLegalBallsBowled(legalBalls);
        innings.setWides(wides);
        innings.setNoBalls(noBalls);
        innings.setByes(byes);
        innings.setLegByes(legByes);
        innings.setPenaltyRuns(penaltyRuns);

        if (innings.getStatus() == InningsStatus.COMPLETED) {
            Integer targetRuns = innings.getTargetRuns();
            Integer maxOvers = innings.getMatch().getMaxOversPerInnings();
            int maxBalls = (maxOvers != null && maxOvers > 0) ? maxOvers * 6 : Integer.MAX_VALUE;

            if ((targetRuns == null || totalRuns < targetRuns) && wickets < 10 && legalBalls < maxBalls) {
                innings.setStatus(InningsStatus.IN_PROGRESS);
                innings.getMatch().setStatus(MatchStatus.LIVE);
                matchRepository.save(innings.getMatch());
            }
        }

        inningsRepository.save(innings);
    }

    private void broadcastDeliveryUpdate(Innings innings, BallEvent ballEvent,
                                         boolean isWicket, boolean overCompleted,
                                         boolean inningsCompleted, boolean matchCompleted) {
        ScoreUpdateMessage message = new ScoreUpdateMessage();
        message.setMatchId(innings.getMatch().getId());
        message.setInningsId(innings.getId());
        message.setInningsNumber(innings.getInningsNumber());
        message.setEventSequence(ballEvent.getEventSequence());
        message.setTotalRuns(innings.getTotalRuns());
        message.setWickets(innings.getWickets());
        message.setOvers(innings.getOversFormatted());
        message.setCurrentRunRate(innings.getCurrentRunRate());

        Integer maxOvers = innings.getMatch().getMaxOversPerInnings();
        message.setRequiredRunRate(innings.getRequiredRunRate(maxOvers));

        if (matchCompleted) {
            message.setEventType(WebSocketEventType.MATCH_COMPLETED);
        } else if (inningsCompleted) {
            message.setEventType(WebSocketEventType.INNINGS_COMPLETED);
        } else if (overCompleted) {
            message.setEventType(WebSocketEventType.OVER_COMPLETED);
        } else if (isWicket) {
            message.setEventType(WebSocketEventType.WICKET_FALL);
        } else {
            message.setEventType(WebSocketEventType.BALL_SCORED);
        }

        message.setLastDeliveryDescription(String.format("Over %d.%d: %s to %s, %d run(s)%s",
                ballEvent.getOverNumber(), ballEvent.getDeliveryIndex(),
                ballEvent.getBowler().getFullName(), ballEvent.getStriker().getFullName(),
                ballEvent.getTotalRuns(),
                isWicket ? " [WICKET: " + ballEvent.getWicketType() + "]" : ""));

        eventPublisher.publishScoreUpdate(message);
    }
}
