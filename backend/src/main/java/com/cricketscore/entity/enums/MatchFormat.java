package com.cricketscore.entity.enums;

public enum MatchFormat {
    T20(20),
    ODI(50),
    TEST(null),
    CUSTOM_LIMITED_OVERS(null);

    private final Integer defaultOvers;

    MatchFormat(Integer defaultOvers) {
        this.defaultOvers = defaultOvers;
    }

    public Integer getDefaultOvers() {
        return defaultOvers;
    }
}
