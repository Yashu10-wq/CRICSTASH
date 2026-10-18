package com.cricketscore.service;

import com.cricketscore.dto.request.MatchCreateRequest;
import com.cricketscore.dto.request.StartMatchRequest;
import com.cricketscore.dto.request.TossRequest;
import com.cricketscore.dto.response.MatchResponse;
import com.cricketscore.entity.*;
import com.cricketscore.entity.enums.*;
import com.cricketscore.exception.InvalidMatchStateException;
import com.cricketscore.mapper.MatchMapper;
import com.cricketscore.mapper.PlayerMapper;
import com.cricketscore.mapper.TeamMapper;
import com.cricketscore.repository.*;
import com.cricketscore.service.impl.MatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MatchServiceTest {

    @Mock private MatchRepository matchRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private MatchSquadRepository matchSquadRepository;
    @Mock private InningsRepository inningsRepository;
    @Mock private BattingScoreRepository battingScoreRepository;
    @Mock private BowlingFigureRepository bowlingFigureRepository;

    private TeamMapper teamMapper = new TeamMapper();
    private PlayerMapper playerMapper = new PlayerMapper();
    private MatchMapper matchMapper;

    private MatchServiceImpl matchService;

    private Team ind;
    private Team aus;
    private Player rohit;
    private Player kohli;
    private Player starc;

    @BeforeEach
    void setUp() {
        matchMapper = new MatchMapper(teamMapper, playerMapper);
        matchService = new MatchServiceImpl(
                matchRepository, teamRepository, playerRepository,
                matchSquadRepository, inningsRepository, battingScoreRepository,
                bowlingFigureRepository, matchMapper, playerMapper
        );

        ind = new Team("India", "IND", "India", null);
        ind.setId(1L);

        aus = new Team("Australia", "AUS", "Australia", null);
        aus.setId(2L);

        rohit = new Player("Rohit Sharma", null, "India", BattingStyle.RIGHT_HAND, null, PlayerRole.BATTER);
        rohit.setId(10L);

        kohli = new Player("Virat Kohli", null, "India", BattingStyle.RIGHT_HAND, null, PlayerRole.BATTER);
        kohli.setId(11L);

        starc = new Player("Mitchell Starc", null, "Australia", BattingStyle.LEFT_HAND, BowlingStyle.LEFT_ARM_FAST, PlayerRole.BOWLER);
        starc.setId(20L);
    }

    @Test
    @DisplayName("Should create match with default format overs")
    void testCreateMatch() {
        MatchCreateRequest req = new MatchCreateRequest("IND vs AUS", MatchFormat.T20, null, "World Cup", "MCG", Instant.now(), 1L, 2L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(ind));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(aus));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId(100L);
            return m;
        });

        MatchResponse res = matchService.createMatch(req);

        assertThat(res.getId()).isEqualTo(100L);
        assertThat(res.getFormat()).isEqualTo(MatchFormat.T20);
        assertThat(res.getMaxOversPerInnings()).isEqualTo(20);
        assertThat(res.getStatus()).isEqualTo(MatchStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Should reject match with same home and away team")
    void testRejectSameTeams() {
        MatchCreateRequest req = new MatchCreateRequest("IND vs IND", MatchFormat.T20, 20, null, null, Instant.now(), 1L, 1L);

        assertThatThrownBy(() -> matchService.createMatch(req))
                .isInstanceOf(InvalidMatchStateException.class)
                .hasMessageContaining("Home team and away team must be different");
    }

    @Test
    @DisplayName("Should record toss successfully")
    void testRecordToss() {
        Match match = new Match();
        match.setId(100L);
        match.setHomeTeam(ind);
        match.setAwayTeam(aus);
        match.setStatus(MatchStatus.SCHEDULED);

        when(matchRepository.findById(100L)).thenReturn(Optional.of(match));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(ind));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        TossRequest tossReq = new TossRequest(1L, TossDecision.BAT);
        MatchResponse res = matchService.recordToss(100L, tossReq);

        assertThat(res.getTossWinnerTeam().getId()).isEqualTo(1L);
        assertThat(res.getTossDecision()).isEqualTo(TossDecision.BAT);
    }

    @Test
    @DisplayName("Should start match and initialize innings 1")
    void testStartMatch() {
        Match match = new Match();
        match.setId(100L);
        match.setHomeTeam(ind);
        match.setAwayTeam(aus);
        match.setStatus(MatchStatus.SCHEDULED);
        match.setTossWinnerTeam(ind);
        match.setTossDecision(TossDecision.BAT);

        when(matchRepository.findById(100L)).thenReturn(Optional.of(match));
        when(playerRepository.findById(10L)).thenReturn(Optional.of(rohit));
        when(playerRepository.findById(11L)).thenReturn(Optional.of(kohli));
        when(playerRepository.findById(20L)).thenReturn(Optional.of(starc));
        when(inningsRepository.save(any(Innings.class))).thenAnswer(i -> {
            Innings inn = i.getArgument(0);
            inn.setId(500L);
            return inn;
        });
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        StartMatchRequest startReq = new StartMatchRequest(10L, 11L, 20L);
        MatchResponse res = matchService.startMatch(100L, startReq);

        assertThat(res.getStatus()).isEqualTo(MatchStatus.LIVE);
        assertThat(res.getCurrentInningsNumber()).isEqualTo(1);
    }
}
