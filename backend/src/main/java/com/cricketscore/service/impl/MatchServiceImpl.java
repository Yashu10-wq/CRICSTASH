package com.cricketscore.service.impl;

import com.cricketscore.dto.request.MatchCreateRequest;
import com.cricketscore.dto.request.MatchSquadSetupRequest;
import com.cricketscore.dto.request.StartMatchRequest;
import com.cricketscore.dto.request.TossRequest;
import com.cricketscore.dto.response.MatchResponse;
import com.cricketscore.dto.response.MatchSquadResponse;
import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.entity.*;
import com.cricketscore.entity.enums.InningsStatus;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.entity.enums.TossDecision;
import com.cricketscore.exception.InvalidMatchStateException;
import com.cricketscore.exception.InvalidScoringOperationException;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.MatchMapper;
import com.cricketscore.mapper.PlayerMapper;
import com.cricketscore.repository.*;
import com.cricketscore.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchSquadRepository matchSquadRepository;
    private final InningsRepository inningsRepository;
    private final BattingScoreRepository battingScoreRepository;
    private final BowlingFigureRepository bowlingFigureRepository;
    private final MatchMapper matchMapper;
    private final PlayerMapper playerMapper;

    public MatchServiceImpl(MatchRepository matchRepository,
                            TeamRepository teamRepository,
                            PlayerRepository playerRepository,
                            MatchSquadRepository matchSquadRepository,
                            InningsRepository inningsRepository,
                            BattingScoreRepository battingScoreRepository,
                            BowlingFigureRepository bowlingFigureRepository,
                            MatchMapper matchMapper,
                            PlayerMapper playerMapper) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchSquadRepository = matchSquadRepository;
        this.inningsRepository = inningsRepository;
        this.battingScoreRepository = battingScoreRepository;
        this.bowlingFigureRepository = bowlingFigureRepository;
        this.matchMapper = matchMapper;
        this.playerMapper = playerMapper;
    }

    @Override
    @Transactional
    public MatchResponse createMatch(MatchCreateRequest request) {
        if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
            throw new InvalidMatchStateException("Home team and away team must be different teams.");
        }

        Team homeTeam = teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Home team not found with id: " + request.getHomeTeamId()));

        Team awayTeam = teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Away team not found with id: " + request.getAwayTeamId()));

        Integer maxOvers = request.getMaxOversPerInnings();
        if (maxOvers == null && request.getFormat() != null) {
            maxOvers = request.getFormat().getDefaultOvers();
        }

        Match match = new Match();
        match.setTitle(request.getTitle());
        match.setFormat(request.getFormat());
        match.setMaxOversPerInnings(maxOvers);
        match.setCompetition(request.getCompetition());
        match.setVenue(request.getVenue());
        match.setStartTime(request.getStartTime());
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setStatus(MatchStatus.SCHEDULED);
        match.setCurrentInningsNumber(1);

        match = matchRepository.save(match);
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchResponse> getAllMatches(MatchStatus status, Long teamId, Pageable pageable) {
        if (status != null) {
            return matchRepository.findByStatus(status, pageable).map(matchMapper::toResponse);
        } else if (teamId != null) {
            return matchRepository.findByTeamId(teamId, pageable).map(matchMapper::toResponse);
        }
        return matchRepository.findAll(pageable).map(matchMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id) {
        Match match = matchRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional
    public MatchResponse updateMatch(Long id, MatchCreateRequest request) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));

        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.CANCELLED) {
            throw new InvalidMatchStateException("Cannot edit details of a completed or cancelled match.");
        }

        match.setTitle(request.getTitle());
        match.setFormat(request.getFormat());
        match.setMaxOversPerInnings(request.getMaxOversPerInnings() != null ?
                request.getMaxOversPerInnings() : request.getFormat().getDefaultOvers());
        match.setCompetition(request.getCompetition());
        match.setVenue(request.getVenue());
        match.setStartTime(request.getStartTime());

        match = matchRepository.save(match);
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional
    public MatchResponse cancelMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new InvalidMatchStateException("Cannot cancel an already completed match.");
        }

        match.setStatus(MatchStatus.CANCELLED);
        match.setResultSummary("Match cancelled");
        match = matchRepository.save(match);
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional
    public List<MatchSquadResponse> setupMatchSquad(Long matchId, MatchSquadSetupRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.CANCELLED) {
            throw new InvalidMatchStateException("Cannot modify squad of a completed or cancelled match.");
        }

        if (!Objects.equals(match.getHomeTeam().getId(), request.getTeamId()) &&
            !Objects.equals(match.getAwayTeam().getId(), request.getTeamId())) {
            throw new InvalidMatchStateException("Team id " + request.getTeamId() + " is not participating in this match.");
        }

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTeamId()));

        List<MatchSquadResponse> result = new ArrayList<>();
        for (MatchSquadSetupRequest.SquadPlayerItem item : request.getPlayers()) {
            Player player = playerRepository.findById(item.getPlayerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + item.getPlayerId()));

            MatchSquad squad = matchSquadRepository.findByMatchIdAndPlayerId(matchId, item.getPlayerId())
                    .orElseGet(() -> new MatchSquad(match, team, player, item.isPlayingXi(), item.isCaptain(), item.isWicketKeeper()));

            squad.setPlayingXi(item.isPlayingXi());
            squad.setCaptain(item.isCaptain());
            squad.setWicketKeeper(item.isWicketKeeper());
            squad = matchSquadRepository.save(squad);

            result.add(new MatchSquadResponse(
                    squad.getId(), matchId, team.getId(), team.getName(),
                    playerMapper.toResponse(player),
                    squad.isPlayingXi(), squad.isCaptain(), squad.isWicketKeeper()
            ));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchSquadResponse> getMatchSquad(Long matchId, Long teamId) {
        if (!matchRepository.existsById(matchId)) {
            throw new ResourceNotFoundException("Match not found with id: " + matchId);
        }

        List<MatchSquad> squads = (teamId != null) ?
                matchSquadRepository.findByMatchIdAndTeamId(matchId, teamId) :
                matchSquadRepository.findByMatchId(matchId);

        return squads.stream()
                .map(s -> new MatchSquadResponse(
                        s.getId(), matchId, s.getTeam().getId(), s.getTeam().getName(),
                        playerMapper.toResponse(s.getPlayer()),
                        s.isPlayingXi(), s.isCaptain(), s.isWicketKeeper()
                ))
                .toList();
    }

    @Override
    @Transactional
    public MatchResponse recordToss(Long matchId, TossRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new InvalidMatchStateException("Toss can only be recorded when match is in SCHEDULED status.");
        }

        if (!Objects.equals(match.getHomeTeam().getId(), request.getTossWinnerTeamId()) &&
            !Objects.equals(match.getAwayTeam().getId(), request.getTossWinnerTeamId())) {
            throw new InvalidMatchStateException("Toss winner must be one of the participating match teams.");
        }

        Team tossWinner = teamRepository.findById(request.getTossWinnerTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTossWinnerTeamId()));

        match.setTossWinnerTeam(tossWinner);
        match.setTossDecision(request.getTossDecision());
        match = matchRepository.save(match);
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional
    public MatchResponse startMatch(Long matchId, StartMatchRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new InvalidMatchStateException("Match has already started or is in status: " + match.getStatus());
        }

        if (match.getTossWinnerTeam() == null || match.getTossDecision() == null) {
            throw new InvalidMatchStateException("Cannot start match: Toss winner and toss decision must be recorded first.");
        }

        // Determine batting team and bowling team from toss
        Team tossWinner = match.getTossWinnerTeam();
        Team opponent = Objects.equals(tossWinner.getId(), match.getHomeTeam().getId()) ? match.getAwayTeam() : match.getHomeTeam();

        Team battingTeam = (match.getTossDecision() == TossDecision.BAT) ? tossWinner : opponent;
        Team bowlingTeam = (match.getTossDecision() == TossDecision.BAT) ? opponent : tossWinner;

        Player openingStriker = playerRepository.findById(request.getOpeningStrikerId())
                .orElseThrow(() -> new ResourceNotFoundException("Opening striker not found with id: " + request.getOpeningStrikerId()));

        Player openingNonStriker = playerRepository.findById(request.getOpeningNonStrikerId())
                .orElseThrow(() -> new ResourceNotFoundException("Opening non-striker not found with id: " + request.getOpeningNonStrikerId()));

        Player openingBowler = playerRepository.findById(request.getOpeningBowlerId())
                .orElseThrow(() -> new ResourceNotFoundException("Opening bowler not found with id: " + request.getOpeningBowlerId()));

        if (openingStriker.getId().equals(openingNonStriker.getId())) {
            throw new InvalidScoringOperationException("Opening striker and opening non-striker cannot be the same player.");
        }
        if (openingBowler.getId().equals(openingStriker.getId()) || openingBowler.getId().equals(openingNonStriker.getId())) {
            throw new InvalidScoringOperationException("Opening bowler cannot be one of the opening batters.");
        }

        // Create Innings 1
        Innings innings = new Innings(match, 1, battingTeam, bowlingTeam, null);
        innings.setStatus(InningsStatus.IN_PROGRESS);
        innings.setCurrentStriker(openingStriker);
        innings.setCurrentNonStriker(openingNonStriker);
        innings.setCurrentBowler(openingBowler);
        innings = inningsRepository.save(innings);

        // Initialize opening BattingScores
        BattingScore strikerScore = new BattingScore(innings, openingStriker, 1);
        strikerScore.setOnStrike(true);
        battingScoreRepository.save(strikerScore);

        BattingScore nonStrikerScore = new BattingScore(innings, openingNonStriker, 2);
        nonStrikerScore.setOnStrike(false);
        battingScoreRepository.save(nonStrikerScore);

        // Initialize opening BowlingFigure
        BowlingFigure bowlingFigure = new BowlingFigure(innings, openingBowler);
        bowlingFigureRepository.save(bowlingFigure);

        match.setStatus(MatchStatus.LIVE);
        match.setCurrentInningsNumber(1);
        match = matchRepository.save(match);

        return matchMapper.toResponse(match);
    }
}
