package com.cricketscore.service.impl;

import com.cricketscore.dto.response.*;
import com.cricketscore.entity.Match;
import com.cricketscore.entity.Player;
import com.cricketscore.entity.Team;
import com.cricketscore.entity.enums.MatchResultType;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.MatchMapper;
import com.cricketscore.repository.*;
import com.cricketscore.service.InningsService;
import com.cricketscore.service.StatisticsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final BattingScoreRepository battingScoreRepository;
    private final BowlingFigureRepository bowlingFigureRepository;
    private final InningsService inningsService;
    private final MatchMapper matchMapper;

    public StatisticsServiceImpl(PlayerRepository playerRepository,
                                 TeamRepository teamRepository,
                                 MatchRepository matchRepository,
                                 BattingScoreRepository battingScoreRepository,
                                 BowlingFigureRepository bowlingFigureRepository,
                                 InningsService inningsService,
                                 MatchMapper matchMapper) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.battingScoreRepository = battingScoreRepository;
        this.bowlingFigureRepository = bowlingFigureRepository;
        this.inningsService = inningsService;
        this.matchMapper = matchMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerStatisticsResponse getPlayerStatistics(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));

        long inningsBatted = battingScoreRepository.countInningsBattedByPlayerId(playerId);
        long totalRuns = battingScoreRepository.sumRunsByPlayerId(playerId);
        long ballsFaced = battingScoreRepository.sumBallsFacedByPlayerId(playerId);
        int highestScore = battingScoreRepository.findHighestScoreByPlayerId(playerId);
        long dismissals = battingScoreRepository.countDismissalsByPlayerId(playerId);
        long fours = battingScoreRepository.sumFoursByPlayerId(playerId);
        long sixes = battingScoreRepository.sumSixesByPlayerId(playerId);

        double battingAverage = dismissals > 0 ?
                Math.round(((double) totalRuns / dismissals) * 100.0) / 100.0 : totalRuns;

        double battingStrikeRate = ballsFaced > 0 ?
                Math.round(((totalRuns * 100.0) / ballsFaced) * 100.0) / 100.0 : 0.0;

        long inningsBowled = bowlingFigureRepository.countInningsBowledByPlayerId(playerId);
        long legalBallsBowled = bowlingFigureRepository.sumBallsBowledByPlayerId(playerId);
        long runsConceded = bowlingFigureRepository.sumRunsConcededByPlayerId(playerId);
        long wicketsTaken = bowlingFigureRepository.sumWicketsByPlayerId(playerId);
        long maidens = bowlingFigureRepository.sumMaidensByPlayerId(playerId);

        double bowlingEconomy = legalBallsBowled > 0 ?
                Math.round(((runsConceded * 6.0) / legalBallsBowled) * 100.0) / 100.0 : 0.0;

        double bowlingAverage = wicketsTaken > 0 ?
                Math.round(((double) runsConceded / wicketsTaken) * 100.0) / 100.0 : 0.0;

        double bowlingStrikeRate = wicketsTaken > 0 ?
                Math.round(((double) legalBallsBowled / wicketsTaken) * 100.0) / 100.0 : 0.0;

        String oversBowled = (legalBallsBowled / 6) + "." + (legalBallsBowled % 6);

        PlayerStatisticsResponse response = new PlayerStatisticsResponse();
        response.setPlayerId(player.getId());
        response.setPlayerName(player.getFullName());
        response.setInningsBatted(inningsBatted);
        response.setTotalRuns(totalRuns);
        response.setBallsFaced(ballsFaced);
        response.setHighestScore(highestScore);
        response.setBattingAverage(battingAverage);
        response.setBattingStrikeRate(battingStrikeRate);
        response.setFours(fours);
        response.setSixes(sixes);

        response.setInningsBowled(inningsBowled);
        response.setLegalBallsBowled(legalBallsBowled);
        response.setOversBowled(oversBowled);
        response.setRunsConceded(runsConceded);
        response.setWicketsTaken(wicketsTaken);
        response.setMaidens(maidens);
        response.setBowlingEconomy(bowlingEconomy);
        response.setBowlingAverage(bowlingAverage);
        response.setBowlingStrikeRate(bowlingStrikeRate);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TeamStatisticsResponse getTeamStatistics(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        List<Match> matches = matchRepository.findByTeamId(teamId, Pageable.unpaged()).getContent();

        long played = 0;
        long won = 0;
        long lost = 0;
        long tied = 0;
        long noResult = 0;

        for (Match m : matches) {
            if (m.getStatus() == MatchStatus.COMPLETED) {
                played++;
                if (m.getResultType() == MatchResultType.TIE) {
                    tied++;
                } else if (m.getWinnerTeam() != null && Objects.equals(m.getWinnerTeam().getId(), teamId)) {
                    won++;
                } else {
                    lost++;
                }
            } else if (m.getStatus() == MatchStatus.ABANDONED) {
                noResult++;
            }
        }

        double winPct = played > 0 ? Math.round(((won * 100.0) / played) * 100.0) / 100.0 : 0.0;

        TeamStatisticsResponse response = new TeamStatisticsResponse();
        response.setTeamId(team.getId());
        response.setTeamName(team.getName());
        response.setMatchesPlayed(played);
        response.setMatchesWon(won);
        response.setMatchesLost(lost);
        response.setMatchesTied(tied);
        response.setMatchesNoResult(noResult);
        response.setWinPercentage(winPct);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public MatchSummaryResponse getMatchSummary(Long matchId) {
        Match match = matchRepository.findByIdWithDetails(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        List<ScorecardResponse> scorecards = inningsService.getMatchScorecards(matchId);

        return new MatchSummaryResponse(matchMapper.toResponse(match), scorecards, match.getResultSummary());
    }
}
