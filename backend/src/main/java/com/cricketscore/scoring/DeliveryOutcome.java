package com.cricketscore.scoring;

import com.cricketscore.entity.BallEvent;
import com.cricketscore.entity.BattingScore;
import com.cricketscore.entity.BowlingFigure;
import com.cricketscore.entity.Innings;

import java.util.List;

public class DeliveryOutcome {
    private final Innings innings;
    private final BallEvent ballEvent;
    private final BattingScore strikerScore;
    private final BattingScore nonStrikerScore;
    private final BowlingFigure bowlingFigure;
    private final boolean overCompleted;
    private final boolean inningsCompleted;
    private final boolean matchCompleted;

    public DeliveryOutcome(Innings innings, BallEvent ballEvent,
                           BattingScore strikerScore, BattingScore nonStrikerScore,
                           BowlingFigure bowlingFigure,
                           boolean overCompleted, boolean inningsCompleted, boolean matchCompleted) {
        this.innings = innings;
        this.ballEvent = ballEvent;
        this.strikerScore = strikerScore;
        this.nonStrikerScore = nonStrikerScore;
        this.bowlingFigure = bowlingFigure;
        this.overCompleted = overCompleted;
        this.inningsCompleted = inningsCompleted;
        this.matchCompleted = matchCompleted;
    }

    public Innings getInnings() {
        return innings;
    }

    public BallEvent getBallEvent() {
        return ballEvent;
    }

    public BattingScore getStrikerScore() {
        return strikerScore;
    }

    public BattingScore getNonStrikerScore() {
        return nonStrikerScore;
    }

    public BowlingFigure getBowlingFigure() {
        return bowlingFigure;
    }

    public boolean isOverCompleted() {
        return overCompleted;
    }

    public boolean isInningsCompleted() {
        return inningsCompleted;
    }

    public boolean isMatchCompleted() {
        return matchCompleted;
    }
}
