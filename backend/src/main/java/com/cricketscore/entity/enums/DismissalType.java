package com.cricketscore.entity.enums;

public enum DismissalType {
    NOT_OUT(false),
    BOWLED(true),
    CAUGHT(true),
    LBW(true),
    STUMPED(true),
    HIT_WICKET(true),
    RUN_OUT(false),
    RETIRED_OUT(false),
    OBSTRUCTING_THE_FIELD(false),
    TIMED_OUT(false),
    HIT_BALL_TWICE(false);

    private final boolean creditedToBowler;

    DismissalType(boolean creditedToBowler) {
        this.creditedToBowler = creditedToBowler;
    }

    public boolean isCreditedToBowler() {
        return creditedToBowler;
    }
}
