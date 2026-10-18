package com.cricketscore.dto.response;

import com.cricketscore.entity.enums.DismissalType;

public class BattingScoreResponse {
    private Long id;
    private Long playerId;
    private String playerName;
    private Integer runs;
    private Integer ballsFaced;
    private Integer fours;
    private Integer sixes;
    private DismissalType dismissalType;
    private String dismissedByBowlerName;
    private String assistedByPlayerName;
    private Integer battingPosition;
    private boolean onStrike;
    private double strikeRate;

    public BattingScoreResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
    }

    public Integer getBallsFaced() {
        return ballsFaced;
    }

    public void setBallsFaced(Integer ballsFaced) {
        this.ballsFaced = ballsFaced;
    }

    public Integer getFours() {
        return fours;
    }

    public void setFours(Integer fours) {
        this.fours = fours;
    }

    public Integer getSixes() {
        return sixes;
    }

    public void setSixes(Integer sixes) {
        this.sixes = sixes;
    }

    public DismissalType getDismissalType() {
        return dismissalType;
    }

    public void setDismissalType(DismissalType dismissalType) {
        this.dismissalType = dismissalType;
    }

    public String getDismissedByBowlerName() {
        return dismissedByBowlerName;
    }

    public void setDismissedByBowlerName(String dismissedByBowlerName) {
        this.dismissedByBowlerName = dismissedByBowlerName;
    }

    public String getAssistedByPlayerName() {
        return assistedByPlayerName;
    }

    public void setAssistedByPlayerName(String assistedByPlayerName) {
        this.assistedByPlayerName = assistedByPlayerName;
    }

    public Integer getBattingPosition() {
        return battingPosition;
    }

    public void setBattingPosition(Integer battingPosition) {
        this.battingPosition = battingPosition;
    }

    public boolean isOnStrike() {
        return onStrike;
    }

    public void setOnStrike(boolean onStrike) {
        this.onStrike = onStrike;
    }

    public double getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(double strikeRate) {
        this.strikeRate = strikeRate;
    }
}
