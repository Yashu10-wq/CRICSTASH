package com.cricketscore.service.impl;

import com.cricketscore.dto.request.DeliveryCorrectionRequest;
import com.cricketscore.dto.request.DeliveryRequest;
import com.cricketscore.dto.request.InningsCreateRequest;
import com.cricketscore.dto.response.*;
import com.cricketscore.entity.*;
import com.cricketscore.entity.enums.InningsStatus;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.exception.InvalidMatchStateException;
import com.cricketscore.exception.InvalidScoringOperationException;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.InningsMapper;
import com.cricketscore.mapper.ScorecardMapper;
import com.cricketscore.repository.*;
import com.cricketscore.scoring.CricketScoringEngine;
import com.cricketscore.scoring.DeliveryOutcome;
import com.cricketscore.service.InningsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InningsServiceImpl implements InningsService {

    private final InningsRepository inningsRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final BattingScoreRepository battingScoreRepository;
    private final BowlingFigureRepository bowlingFigureRepository;
    private final BallEventRepository ballEventRepository;
    private final CricketScoringEngine scoringEngine;
    private final InningsMapper inningsMapper;
    private final ScorecardMapper scorecardMapper;

    public InningsServiceImpl(InningsRepository inningsRepository,
                              MatchRepository matchRepository,
                              PlayerRepository playerRepository,
                              BattingScoreRepository battingScoreRepository,
                              BowlingFigureRepository bowlingFigureRepository,
                              BallEventRepository ballEventRepository,
                              CricketScoringEngine scoringEngine,
                              InningsMapper inningsMapper,
                              ScorecardMapper scorecardMapper) {
        this.inningsRepository = inningsRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.battingScoreRepository = battingScoreRepository;
        this.bowlingFigureRepository = bowlingFigureRepository;
        this.ballEventRepository = ballEventRepository;
        this.scoringEngine = scoringEngine;
        this.inningsMapper = inningsMapper;
        this.scorecardMapper = scorecardMapper;
    }

    @Override
    @Transactional
    public InningsResponse createInnings(Long matchId, InningsCreateRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.CANCELLED) {
            throw new InvalidMatchStateException("Cannot start innings for a completed or cancelled match.");
        }

        // Verify previous innings
        if (request.getInningsNumber() > 1) {
            Innings prevInnings = inningsRepository.findByMatchIdAndInningsNumber(matchId, request.getInningsNumber() - 1)
                    .orElseThrow(() -> new InvalidMatchStateException("Previous innings " + (request.getInningsNumber() - 1) + " must exist first."));

            if (prevInnings.getStatus() != InningsStatus.COMPLETED && prevInnings.getStatus() != InningsStatus.DECLARED) {
                throw new InvalidMatchStateException("Previous innings is not completed yet.");
            }
        }

        // Determine batting and bowling team (swap from innings 1)
        Innings firstInnings = inningsRepository.findByMatchIdAndInningsNumber(matchId, 1)
                .orElseThrow(() -> new InvalidMatchStateException("First innings must exist before subsequent innings."));

        Team battingTeam = (request.getInningsNumber() % 2 == 0) ? firstInnings.getBowlingTeam() : firstInnings.getBattingTeam();
        Team bowlingTeam = (request.getInningsNumber() % 2 == 0) ? firstInnings.getBattingTeam() : firstInnings.getBowlingTeam();

        Integer targetRuns = null;
        if (request.getInningsNumber() == 2) {
            targetRuns = firstInnings.getTotalRuns() + 1;
        }

        Player openingStriker = playerRepository.findById(request.getOpeningStrikerId())
                .orElseThrow(() -> new ResourceNotFoundException("Opening striker not found with id: " + request.getOpeningStrikerId()));

        Player openingNonStriker = playerRepository.findById(request.getOpeningNonStrikerId())
                .orElseThrow(() -> new ResourceNotFoundException("Opening non-striker not found with id: " + request.getOpeningNonStrikerId()));

        Player openingBowler = playerRepository.findById(request.getOpeningBowlerId())
                .orElseThrow(() -> new ResourceNotFoundException("Opening bowler not found with id: " + request.getOpeningBowlerId()));

        Innings innings = new Innings(match, request.getInningsNumber(), battingTeam, bowlingTeam, targetRuns);
        innings.setStatus(InningsStatus.IN_PROGRESS);
        innings.setCurrentStriker(openingStriker);
        innings.setCurrentNonStriker(openingNonStriker);
        innings.setCurrentBowler(openingBowler);
        innings = inningsRepository.save(innings);

        // Initialize BattingScores
        BattingScore strikerScore = new BattingScore(innings, openingStriker, 1);
        strikerScore.setOnStrike(true);
        battingScoreRepository.save(strikerScore);

        BattingScore nonStrikerScore = new BattingScore(innings, openingNonStriker, 2);
        nonStrikerScore.setOnStrike(false);
        battingScoreRepository.save(nonStrikerScore);

        // Initialize BowlingFigure
        BowlingFigure bowlingFigure = new BowlingFigure(innings, openingBowler);
        bowlingFigureRepository.save(bowlingFigure);

        match.setStatus(MatchStatus.LIVE);
        match.setCurrentInningsNumber(request.getInningsNumber());
        matchRepository.save(match);

        return inningsMapper.toResponse(innings);
    }

    @Override
    @Transactional(readOnly = true)
    public InningsResponse getInningsById(Long inningsId) {
        Innings innings = inningsRepository.findByIdWithDetails(inningsId)
                .orElseThrow(() -> new ResourceNotFoundException("Innings not found with id: " + inningsId));
        return inningsMapper.toResponse(innings);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BallEventResponse> getInningsDeliveries(Long inningsId, Pageable pageable) {
        if (!inningsRepository.existsById(inningsId)) {
            throw new ResourceNotFoundException("Innings not found with id: " + inningsId);
        }
        return ballEventRepository.findByInningsIdAndUndoneFalseOrderByEventSequenceDesc(inningsId, pageable)
                .map(scorecardMapper::toBallEventResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BallEventResponse> getAllInningsDeliveries(Long inningsId) {
        if (!inningsRepository.existsById(inningsId)) {
            throw new ResourceNotFoundException("Innings not found with id: " + inningsId);
        }
        return ballEventRepository.findActiveDeliveriesWithPlayers(inningsId).stream()
                .map(scorecardMapper::toBallEventResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ScorecardResponse getInningsScorecard(Long inningsId) {
        Innings innings = inningsRepository.findByIdWithDetails(inningsId)
                .orElseThrow(() -> new ResourceNotFoundException("Innings not found with id: " + inningsId));

        List<BattingScoreResponse> batting = battingScoreRepository.findByInningsIdWithPlayerDetails(inningsId).stream()
                .map(scorecardMapper::toBattingResponse)
                .toList();

        List<BowlingFigureResponse> bowling = bowlingFigureRepository.findByInningsIdWithPlayerDetails(inningsId).stream()
                .map(scorecardMapper::toBowlingResponse)
                .toList();

        List<BallEventResponse> recentDeliveries = ballEventRepository.findByInningsIdAndUndoneFalseOrderByEventSequenceDesc(
                inningsId, PageRequest.of(0, 12)).getContent().stream()
                .map(scorecardMapper::toBallEventResponse)
                .toList();

        return new ScorecardResponse(inningsMapper.toResponse(innings), batting, bowling, recentDeliveries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScorecardResponse> getMatchScorecards(Long matchId) {
        if (!matchRepository.existsById(matchId)) {
            throw new ResourceNotFoundException("Match not found with id: " + matchId);
        }
        List<Innings> inningsList = inningsRepository.findByMatchIdOrderByInningsNumberAsc(matchId);
        return inningsList.stream()
                .map(inn -> getInningsScorecard(inn.getId()))
                .toList();
    }

    @Override
    @Transactional
    public ScorecardResponse recordDelivery(Long inningsId, DeliveryRequest request) {
        DeliveryOutcome outcome = scoringEngine.processDelivery(inningsId, request);
        return getInningsScorecard(outcome.getInnings().getId());
    }

    @Override
    @Transactional
    public ScorecardResponse undoLastDelivery(Long inningsId) {
        Innings innings = scoringEngine.undoLastDelivery(inningsId);
        return getInningsScorecard(innings.getId());
    }

    @Override
    @Transactional
    public ScorecardResponse correctDelivery(Long inningsId, DeliveryCorrectionRequest request) {
        Innings innings = scoringEngine.correctDelivery(inningsId, request);
        return getInningsScorecard(innings.getId());
    }
}
