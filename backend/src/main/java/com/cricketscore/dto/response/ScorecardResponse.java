package com.cricketscore.dto.response;

import java.util.List;

public class ScorecardResponse {
    private InningsResponse innings;
    private List<BattingScoreResponse> battingScores;
    private List<BowlingFigureResponse> bowlingFigures;
    private List<BallEventResponse> recentDeliveries;

    public ScorecardResponse() {
    }

    public ScorecardResponse(InningsResponse innings, List<BattingScoreResponse> battingScores,
                             List<BowlingFigureResponse> bowlingFigures, List<BallEventResponse> recentDeliveries) {
        this.innings = innings;
        this.battingScores = battingScores;
        this.bowlingFigures = bowlingFigures;
        this.recentDeliveries = recentDeliveries;
    }

    public InningsResponse getInnings() {
        return innings;
    }

    public void setInnings(InningsResponse innings) {
        this.innings = innings;
    }

    public List<BattingScoreResponse> getBattingScores() {
        return battingScores;
    }

    public void setBattingScores(List<BattingScoreResponse> battingScores) {
        this.battingScores = battingScores;
    }

    public List<BowlingFigureResponse> getBowlingFigures() {
        return bowlingFigures;
    }

    public void setBowlingFigures(List<BowlingFigureResponse> bowlingFigures) {
        this.bowlingFigures = bowlingFigures;
    }

    public List<BallEventResponse> getRecentDeliveries() {
        return recentDeliveries;
    }

    public void setRecentDeliveries(List<BallEventResponse> recentDeliveries) {
        this.recentDeliveries = recentDeliveries;
    }
}
