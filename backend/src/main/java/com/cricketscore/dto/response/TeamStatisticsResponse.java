package com.cricketscore.dto.response;

public class TeamStatisticsResponse {
    private Long teamId;
    private String teamName;
    private long matchesPlayed;
    private long matchesWon;
    private long matchesLost;
    private long matchesTied;
    private long matchesNoResult;
    private double winPercentage;

    public TeamStatisticsResponse() {
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(long matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public long getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(long matchesWon) {
        this.matchesWon = matchesWon;
    }

    public long getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(long matchesLost) {
        this.matchesLost = matchesLost;
    }

    public long getMatchesTied() {
        return matchesTied;
    }

    public void setMatchesTied(long matchesTied) {
        this.matchesTied = matchesTied;
    }

    public long getMatchesNoResult() {
        return matchesNoResult;
    }

    public void setMatchesNoResult(long matchesNoResult) {
        this.matchesNoResult = matchesNoResult;
    }

    public double getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(double winPercentage) {
        this.winPercentage = winPercentage;
    }
}
