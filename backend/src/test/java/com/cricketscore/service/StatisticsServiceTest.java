package com.cricketscore.service;

import com.cricketscore.dto.response.MatchResponse;
import com.cricketscore.dto.response.MatchSummaryResponse;
import com.cricketscore.dto.response.PlayerStatisticsResponse;
import com.cricketscore.entity.Match;
import com.cricketscore.entity.Player;
import com.cricketscore.entity.Team;
import com.cricketscore.entity.enums.MatchFormat;
import com.cricketscore.entity.enums.MatchResultType;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.entity.enums.PlayerRole;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.MatchMapper;
import com.cricketscore.repository.BattingScoreRepository;
import com.cricketscore.repository.BowlingFigureRepository;
import com.cricketscore.repository.MatchRepository;
import com.cricketscore.repository.PlayerRepository;
import com.cricketscore.service.InningsService;
import com.cricketscore.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BattingScoreRepository battingScoreRepository;

    @Mock
    private BowlingFigureRepository bowlingFigureRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private InningsService inningsService;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private Player player;
    private Match match;

    @BeforeEach
    void setUp() {
        player = new Player("Jasprit Bumrah", null, "India", null, null, PlayerRole.BOWLER);
        player.setId(1L);

        Team homeTeam = new Team("India", "IND", "India", null);
        homeTeam.setId(1L);
        Team awayTeam = new Team("Australia", "AUS", "Australia", null);
        awayTeam.setId(2L);

        match = new Match();
        match.setId(100L);
        match.setTitle("IND vs AUS Final");
        match.setFormat(MatchFormat.T20);
        match.setMaxOversPerInnings(20);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setStartTime(Instant.now());
        match.setVenue("Melbourne");
        match.setCompetition("World Cup");
        match.setStatus(MatchStatus.COMPLETED);
        match.setWinnerTeam(homeTeam);
        match.setResultType(MatchResultType.WIN_BY_RUNS);
        match.setResultSummary("India won by 15 runs");
    }

    @Test
    @DisplayName("Should compute correct player batting and bowling statistics")
    void testGetPlayerStatistics() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        when(battingScoreRepository.countInningsBattedByPlayerId(1L)).thenReturn(2L);
        when(battingScoreRepository.sumRunsByPlayerId(1L)).thenReturn(80L);
        when(battingScoreRepository.sumBallsFacedByPlayerId(1L)).thenReturn(40L);
        when(battingScoreRepository.findHighestScoreByPlayerId(1L)).thenReturn(50);
        when(battingScoreRepository.countDismissalsByPlayerId(1L)).thenReturn(1L);
        when(battingScoreRepository.sumFoursByPlayerId(1L)).thenReturn(6L);
        when(battingScoreRepository.sumSixesByPlayerId(1L)).thenReturn(4L);

        when(bowlingFigureRepository.countInningsBowledByPlayerId(1L)).thenReturn(1L);
        when(bowlingFigureRepository.sumBallsBowledByPlayerId(1L)).thenReturn(24L); // 4 overs
        when(bowlingFigureRepository.sumRunsConcededByPlayerId(1L)).thenReturn(20L);
        when(bowlingFigureRepository.sumWicketsByPlayerId(1L)).thenReturn(3L);
        when(bowlingFigureRepository.sumMaidensByPlayerId(1L)).thenReturn(1L);

        PlayerStatisticsResponse stats = statisticsService.getPlayerStatistics(1L);

        assertThat(stats).isNotNull();
        assertThat(stats.getPlayerId()).isEqualTo(1L);
        assertThat(stats.getTotalRuns()).isEqualTo(80);
        assertThat(stats.getBallsFaced()).isEqualTo(40);
        assertThat(stats.getFours()).isEqualTo(6);
        assertThat(stats.getSixes()).isEqualTo(4);
        assertThat(stats.getBattingAverage()).isEqualTo(80.0);
        assertThat(stats.getBattingStrikeRate()).isEqualTo(200.0);

        // Bowling stats
        assertThat(stats.getWicketsTaken()).isEqualTo(3);
        assertThat(stats.getLegalBallsBowled()).isEqualTo(24);
        assertThat(stats.getOversBowled()).isEqualTo("4.0");
        assertThat(stats.getRunsConceded()).isEqualTo(20);
        assertThat(stats.getBowlingEconomy()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should build complete match summary")
    void testGetMatchSummary() {
        when(matchRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(match));
        when(inningsService.getMatchScorecards(100L)).thenReturn(Collections.emptyList());

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setId(100L);
        matchResponse.setTitle("IND vs AUS Final");
        when(matchMapper.toResponse(match)).thenReturn(matchResponse);

        MatchSummaryResponse summary = statisticsService.getMatchSummary(100L);

        assertThat(summary).isNotNull();
        assertThat(summary.getMatch().getId()).isEqualTo(100L);
        assertThat(summary.getMatch().getTitle()).isEqualTo("IND vs AUS Final");
        assertThat(summary.getMatchResultSummary()).isEqualTo("India won by 15 runs");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when match not found for summary")
    void testGetMatchSummary_NotFound() {
        when(matchRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statisticsService.getMatchSummary(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Match not found");
    }
}
