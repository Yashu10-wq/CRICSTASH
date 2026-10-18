package com.cricketscore.scoring;

import com.cricketscore.dto.request.*;
import com.cricketscore.dto.response.MatchResponse;
import com.cricketscore.dto.response.ScorecardResponse;
import com.cricketscore.entity.*;
import com.cricketscore.entity.enums.*;
import com.cricketscore.repository.*;
import com.cricketscore.service.InningsService;
import com.cricketscore.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ScoringIntegrationTest {

    @Autowired private MatchService matchService;
    @Autowired private InningsService inningsService;
    @Autowired private TeamRepository teamRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private InningsRepository inningsRepository;
    @Autowired private MatchRepository matchRepository;

    private Team teamInd;
    private Team teamPak;
    private Player batter1;
    private Player batter2;
    private Player bowler1;
    private Long matchId;

    @BeforeEach
    void setUp() {
        teamInd = teamRepository.save(new Team("India National", "IND", "India", null));
        teamPak = teamRepository.save(new Team("Pakistan National", "PAK", "Pakistan", null));

        batter1 = playerRepository.save(new Player("Shubman Gill", null, "India", BattingStyle.RIGHT_HAND, null, PlayerRole.BATTER));
        batter2 = playerRepository.save(new Player("Yashasvi Jaiswal", null, "India", BattingStyle.LEFT_HAND, null, PlayerRole.BATTER));
        bowler1 = playerRepository.save(new Player("Shaheen Afridi", null, "Pakistan", BattingStyle.LEFT_HAND, BowlingStyle.LEFT_ARM_FAST, PlayerRole.BOWLER));

        MatchCreateRequest matchReq = new MatchCreateRequest(
                "IND vs PAK", MatchFormat.T20, 20, "Asia Cup", "Dubai", Instant.now(),
                teamInd.getId(), teamPak.getId()
        );
        MatchResponse matchRes = matchService.createMatch(matchReq);
        matchId = matchRes.getId();

        matchService.recordToss(matchId, new TossRequest(teamInd.getId(), TossDecision.BAT));
        matchService.startMatch(matchId, new StartMatchRequest(batter1.getId(), batter2.getId(), bowler1.getId()));
    }

    @Test
    @DisplayName("End-to-End: Score full over with dot, single, wide, four, and verify scorecard")
    void testEndToEndScoringOver() {
        Innings innings = inningsRepository.findByMatchIdAndInningsNumber(matchId, 1).orElseThrow();
        Long inningsId = innings.getId();

        // Ball 1: Dot ball
        DeliveryRequest ball1 = new DeliveryRequest();
        ball1.setBatterRuns(0);
        ball1.setExtraType(ExtraType.NONE);
        ScorecardResponse sc1 = inningsService.recordDelivery(inningsId, ball1);
        assertThat(sc1.getInnings().getTotalRuns()).isEqualTo(0);
        assertThat(sc1.getInnings().getLegalBallsBowled()).isEqualTo(1);

        // Ball 2: Wide delivery (1 extra run, legal ball remains 1)
        DeliveryRequest ball2 = new DeliveryRequest();
        ball2.setBatterRuns(0);
        ball2.setExtraRuns(1);
        ball2.setExtraType(ExtraType.WIDE);
        ScorecardResponse sc2 = inningsService.recordDelivery(inningsId, ball2);
        assertThat(sc2.getInnings().getTotalRuns()).isEqualTo(1);
        assertThat(sc2.getInnings().getLegalBallsBowled()).isEqualTo(1); // Still 1 legal ball!

        // Ball 3: Single run (Gill to Jaiswal)
        DeliveryRequest ball3 = new DeliveryRequest();
        ball3.setBatterRuns(1);
        ball3.setExtraType(ExtraType.NONE);
        ScorecardResponse sc3 = inningsService.recordDelivery(inningsId, ball3);
        assertThat(sc3.getInnings().getTotalRuns()).isEqualTo(2);
        assertThat(sc3.getInnings().getLegalBallsBowled()).isEqualTo(2);
        // Strike rotated to Jaiswal
        assertThat(sc3.getInnings().getCurrentStriker().getId()).isEqualTo(batter2.getId());

        // Ball 4: Boundary Four by Jaiswal
        DeliveryRequest ball4 = new DeliveryRequest();
        ball4.setBatterRuns(4);
        ball4.setExtraType(ExtraType.NONE);
        ScorecardResponse sc4 = inningsService.recordDelivery(inningsId, ball4);
        assertThat(sc4.getInnings().getTotalRuns()).isEqualTo(6);
        assertThat(sc4.getInnings().getLegalBallsBowled()).isEqualTo(3);

        // Test Undo of Ball 4
        ScorecardResponse undoSc = inningsService.undoLastDelivery(inningsId);
        assertThat(undoSc.getInnings().getTotalRuns()).isEqualTo(2); // Rolled back from 6 to 2
        assertThat(undoSc.getInnings().getLegalBallsBowled()).isEqualTo(2); // Rolled back from 3 to 2
    }
}
