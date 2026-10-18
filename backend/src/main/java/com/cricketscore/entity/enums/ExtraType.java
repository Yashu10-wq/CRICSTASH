package com.cricketscore.entity.enums;

public enum ExtraType {
    NONE(true, false),
    WIDE(false, true),
    NO_BALL(false, true),
    BYE(true, false),
    LEG_BYE(true, false),
    PENALTY(false, false);

    private final boolean legalDelivery;
    private final boolean bowlerConceded;

    ExtraType(boolean legalDelivery, boolean bowlerConceded) {
        this.legalDelivery = legalDelivery;
        this.bowlerConceded = bowlerConceded;
    }

    public boolean isLegalDelivery() {
        return legalDelivery;
    }

    public boolean isBowlerConceded() {
        return bowlerConceded;
    }
}
