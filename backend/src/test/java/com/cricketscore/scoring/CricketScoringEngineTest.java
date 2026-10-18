package com.cricketscore.scoring;

import com.cricketscore.dto.request.DeliveryRequest;
import com.cricketscore.entity.*;
import com.cricketscore.entity.enums.*;
import com.cricketscore.exception.InvalidScoringOperationException;
import com.cricketscore.repository.*;
import com.cricketscore.websocket.ScoreUpdateMessage;
import com.cricketscore.websocket.WebSocketEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CricketScoringEngineTest {

    @Mock private InningsRepository inningsRepository;
    @Mock private BattingScoreRepository battingScoreRepository;
    @Mock private BowlingFigureRepository bowlingFigureRepository;
    @Mock private BallEventRepository ballEventRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private MatchSquadRepository matchSquadRepository;
    private WebSocketEventPublisher eventPublisher = new WebSocketEventPublisher(null) {
        @Override
        public void publishScoreUpdate(ScoreUpdateMessage message) {
            // No-op for unit tests
        }
    };

    private CricketScoringEngineImpl scoringEngine;

    private Team teamIndia;
    private Team teamAustralia;
    private Match testMatch;
    private Innings testInnings;
    private Player rohit;
    private Player kohli;
    private Player starc;
    private BattingScore rohitBatting;
    private BattingScore kohliBatting;
    private BowlingFigure starcBowling;

    @BeforeEach
    void setUp() {
        teamIndia = new Team("India", "IND", "India", null);
        teamIndia.setId(1L);

        teamAustralia = new Team("Australia", "AUS", "Australia", null);
        teamAustralia.setId(2L);

        testMatch = new Match();
        testMatch.setId(100L);
        testMatch.setTitle("IND vs AUS T20");
        testMatch.setFormat(MatchFormat.T20);
        testMatch.setMaxOversPerInnings(20);
        testMatch.setHomeTeam(teamIndia);
        testMatch.setAwayTeam(teamAustralia);
        testMatch.setStatus(MatchStatus.LIVE);

        rohit = new Player("Rohit Sharma", null, "India", BattingStyle.RIGHT_HAND, null, PlayerRole.BATTER);
        rohit.setId(10L);

        kohli = new Player("Virat Kohli", null, "India", BattingStyle.RIGHT_HAND, null, PlayerRole.BATTER);
        kohli.setId(11L);

        starc = new Player("Mitchell Starc", null, "Australia", BattingStyle.LEFT_HAND, BowlingStyle.LEFT_ARM_FAST, PlayerRole.BOWLER);
        starc.setId(20L);

        testInnings = new Innings(testMatch, 1, teamIndia, teamAustralia, null);
        testInnings.setId(500L);
        testInnings.setStatus(InningsStatus.IN_PROGRESS);
        testInnings.setCurrentStriker(rohit);
        testInnings.setCurrentNonStriker(kohli);
        testInnings.setCurrentBowler(starc);

        rohitBatting = new BattingScore(testInnings, rohit, 1);
        rohitBatting.setId(101L);
        rohitBatting.setOnStrike(true);

        kohliBatting = new BattingScore(testInnings, kohli, 2);
        kohliBatting.setId(102L);
        kohliBatting.setOnStrike(false);

        starcBowling = new BowlingFigure(testInnings, starc);
        starcBowling.setId(201L);

        when(inningsRepository.findByIdForUpdate(500L)).thenReturn(Optional.of(testInnings));
        when(inningsRepository.save(any(Innings.class))).thenAnswer(i -> i.getArgument(0));
        when(battingScoreRepository.findByInningsIdAndPlayerId(500L, 10L)).thenReturn(Optional.of(rohitBatting));
        when(battingScoreRepository.findByInningsIdAndPlayerId(500L, 11L)).thenReturn(Optional.of(kohliBatting));
        when(bowlingFigureRepository.findByInningsIdAndPlayerId(500L, 20L)).thenReturn(Optional.of(starcBowling));
        when(ballEventRepository.save(any(BallEvent.class))).thenAnswer(i -> {
            BallEvent b = i.getArgument(0);
            b.setId(999L);
            return b;
        });
        when(ballEventRepository.findMaxEventSequence(500L)).thenReturn(0L);

        scoringEngine = new CricketScoringEngineImpl(
                inningsRepository, battingScoreRepository, bowlingFigureRepository,
                ballEventRepository, matchRepository, playerRepository,
                matchSquadRepository, eventPublisher
        );
    }

    @Test
    @DisplayName("Should score a Dot Ball correctly without rotating strike")
    void testDotBall() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(0);
        request.setExtraType(ExtraType.NONE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(0);
        assertThat(outcome.getInnings().getLegalBallsBowled()).isEqualTo(1);
        assertThat(rohitBatting.getBallsFaced()).isEqualTo(1);
        assertThat(rohitBatting.getRuns()).isEqualTo(0);
        assertThat(starcBowling.getLegalBallsBowled()).isEqualTo(1);
        assertThat(starcBowling.getRunsConceded()).isEqualTo(0);
        assertThat(starcBowling.getDots()).isEqualTo(1);
        // Strike unchanged
        assertThat(outcome.getInnings().getCurrentStriker().getId()).isEqualTo(rohit.getId());
    }

    @Test
    @DisplayName("Should score a Single and rotate strike to non-striker")
    void testSingleRunsAndStrikeRotation() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(1);
        request.setExtraType(ExtraType.NONE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(1);
        assertThat(rohitBatting.getRuns()).isEqualTo(1);
        assertThat(rohitBatting.getBallsFaced()).isEqualTo(1);
        assertThat(starcBowling.getRunsConceded()).isEqualTo(1);
        // Odd runs -> Kohli should now be striker
        assertThat(outcome.getInnings().getCurrentStriker().getId()).isEqualTo(kohli.getId());
        assertThat(outcome.getInnings().getCurrentNonStriker().getId()).isEqualTo(rohit.getId());
    }

    @Test
    @DisplayName("Should score a Boundary (Four) without rotating strike")
    void testBoundaryFour() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(4);
        request.setExtraType(ExtraType.NONE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(4);
        assertThat(rohitBatting.getRuns()).isEqualTo(4);
        assertThat(rohitBatting.getFours()).isEqualTo(1);
        assertThat(rohitBatting.getSixes()).isEqualTo(0);
        assertThat(outcome.getInnings().getCurrentStriker().getId()).isEqualTo(rohit.getId());
    }

    @Test
    @DisplayName("Should score a Maximum (Six) without rotating strike")
    void testBoundarySix() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(6);
        request.setExtraType(ExtraType.NONE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(6);
        assertThat(rohitBatting.getRuns()).isEqualTo(6);
        assertThat(rohitBatting.getSixes()).isEqualTo(1);
        assertThat(outcome.getInnings().getCurrentStriker().getId()).isEqualTo(rohit.getId());
    }

    @Test
    @DisplayName("Should handle Wide: extra run awarded, legal ball count not incremented, batter not facing")
    void testWideDelivery() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(0);
        request.setExtraRuns(1);
        request.setExtraType(ExtraType.WIDE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(1);
        assertThat(outcome.getInnings().getWides()).isEqualTo(1);
        assertThat(outcome.getInnings().getLegalBallsBowled()).isEqualTo(0); // Not a legal ball!
        assertThat(rohitBatting.getBallsFaced()).isEqualTo(0); // Batter did not face a legal delivery
        assertThat(starcBowling.getLegalBallsBowled()).isEqualTo(0);
        assertThat(starcBowling.getRunsConceded()).isEqualTo(1); // Conceded to bowler
        assertThat(starcBowling.getWides()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle No-ball: extra run awarded, legal ball not incremented, batter faces delivery")
    void testNoBallDelivery() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(2); // Batter hit 2 runs off no ball
        request.setExtraRuns(1); // 1 penalty
        request.setExtraType(ExtraType.NO_BALL);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(3);
        assertThat(outcome.getInnings().getNoBalls()).isEqualTo(1);
        assertThat(outcome.getInnings().getLegalBallsBowled()).isEqualTo(0);
        assertThat(rohitBatting.getBallsFaced()).isEqualTo(1); // Batter faced the delivery
        assertThat(rohitBatting.getRuns()).isEqualTo(2);
        assertThat(starcBowling.getRunsConceded()).isEqualTo(3);
        assertThat(starcBowling.getNoBalls()).isEqualTo(1);
        // Running 2 runs is even -> striker stays Rohit
        assertThat(outcome.getInnings().getCurrentStriker().getId()).isEqualTo(rohit.getId());
    }

    @Test
    @DisplayName("Should handle Byes: legal ball incremented, but NOT charged to bowler runs conceded")
    void testByesDelivery() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(0);
        request.setExtraRuns(2);
        request.setExtraType(ExtraType.BYE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getTotalRuns()).isEqualTo(2);
        assertThat(outcome.getInnings().getByes()).isEqualTo(2);
        assertThat(outcome.getInnings().getLegalBallsBowled()).isEqualTo(1);
        assertThat(rohitBatting.getBallsFaced()).isEqualTo(1);
        assertThat(rohitBatting.getRuns()).isEqualTo(0);
        // Byes are NOT charged to bowler!
        assertThat(starcBowling.getRunsConceded()).isEqualTo(0);
        assertThat(starcBowling.getLegalBallsBowled()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should credit Bowled wicket to bowler and mark batter dismissed")
    void testBowledWicket() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(0);
        request.setExtraType(ExtraType.NONE);
        request.setWicketType(DismissalType.BOWLED);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getWickets()).isEqualTo(1);
        assertThat(starcBowling.getWickets()).isEqualTo(1); // Credited to bowler
        assertThat(rohitBatting.getDismissalType()).isEqualTo(DismissalType.BOWLED);
        assertThat(rohitBatting.getDismissedByBowler().getId()).isEqualTo(starc.getId());
    }

    @Test
    @DisplayName("Should NOT credit Run Out wicket to bowler")
    void testRunOutWicketNotCreditedToBowler() {
        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(0);
        request.setExtraType(ExtraType.NONE);
        request.setWicketType(DismissalType.RUN_OUT);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.getInnings().getWickets()).isEqualTo(1);
        assertThat(starcBowling.getWickets()).isEqualTo(0); // Run out is NOT credited to bowler!
        assertThat(rohitBatting.getDismissalType()).isEqualTo(DismissalType.RUN_OUT);
    }

    @Test
    @DisplayName("Should reject illegal dismissal off a No-ball (e.g. Bowled off No-ball)")
    void testRejectBowledOffNoBall() {
        DeliveryRequest request = new DeliveryRequest();
        request.setExtraType(ExtraType.NO_BALL);
        request.setWicketType(DismissalType.BOWLED);

        assertThatThrownBy(() -> scoringEngine.processDelivery(500L, request))
                .isInstanceOf(InvalidScoringOperationException.class)
                .hasMessageContaining("Batter cannot be dismissed BOWLED off a No-ball");
    }

    @Test
    @DisplayName("Should handle Over Completion and rotate strike at end of 6th legal ball")
    void testOverCompletionStrikeRotation() {
        testInnings.setLegalBallsBowled(5); // 5 balls already bowled in over

        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(0); // Dot ball on ball 6
        request.setExtraType(ExtraType.NONE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.isOverCompleted()).isTrue();
        assertThat(outcome.getInnings().getLegalBallsBowled()).isEqualTo(6);
        // Over ended -> strike switches ends (Kohli was non-striker, now striker for next over)
        assertThat(outcome.getInnings().getCurrentStriker().getId()).isEqualTo(kohli.getId());
        assertThat(outcome.getInnings().getLastBowler().getId()).isEqualTo(starc.getId());
    }

    @Test
    @DisplayName("Should complete innings and match when chasing team reaches target")
    void testSuccessfulChase() {
        testInnings.setInningsNumber(2);
        testInnings.setTargetRuns(150);
        testInnings.setTotalRuns(148);

        DeliveryRequest request = new DeliveryRequest();
        request.setBatterRuns(4); // Hits a four -> reaches 152 >= 150
        request.setExtraType(ExtraType.NONE);

        DeliveryOutcome outcome = scoringEngine.processDelivery(500L, request);

        assertThat(outcome.isInningsCompleted()).isTrue();
        assertThat(outcome.isMatchCompleted()).isTrue();
        assertThat(outcome.getInnings().getStatus()).isEqualTo(InningsStatus.COMPLETED);
        assertThat(testMatch.getStatus()).isEqualTo(MatchStatus.COMPLETED);
        assertThat(testMatch.getWinnerTeam().getId()).isEqualTo(teamIndia.getId());
        assertThat(testMatch.getResultType()).isEqualTo(MatchResultType.WIN_BY_WICKETS);
    }
}
