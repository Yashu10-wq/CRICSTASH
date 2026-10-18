package com.cricketscore.websocket;

import java.time.Instant;

public class ScoreUpdateMessage {
    private WebSocketEventType eventType;
    private Long matchId;
    private Long inningsId;
    private Integer inningsNumber;
    private Long eventSequence;
    private Integer totalRuns;
    private Integer wickets;
    private String overs;
    private Double currentRunRate;
    private Double requiredRunRate;
    private String lastDeliveryDescription;
    private Instant timestamp;

    public ScoreUpdateMessage() {
        this.timestamp = Instant.now();
    }

    public WebSocketEventType getEventType() {
        return eventType;
    }

    public void setEventType(WebSocketEventType eventType) {
        this.eventType = eventType;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getInningsId() {
        return inningsId;
    }

    public void setInningsId(Long inningsId) {
        this.inningsId = inningsId;
    }

    public Integer getInningsNumber() {
        return inningsNumber;
    }

    public void setInningsNumber(Integer inningsNumber) {
        this.inningsNumber = inningsNumber;
    }

    public Long getEventSequence() {
        return eventSequence;
    }

    public void setEventSequence(Long eventSequence) {
        this.eventSequence = eventSequence;
    }

    public Integer getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(Integer totalRuns) {
        this.totalRuns = totalRuns;
    }

    public Integer getWickets() {
        return wickets;
    }

    public void setWickets(Integer wickets) {
        this.wickets = wickets;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
    }

    public Double getCurrentRunRate() {
        return currentRunRate;
    }

    public void setCurrentRunRate(Double currentRunRate) {
        this.currentRunRate = currentRunRate;
    }

    public Double getRequiredRunRate() {
        return requiredRunRate;
    }

    public void setRequiredRunRate(Double requiredRunRate) {
        this.requiredRunRate = requiredRunRate;
    }

    public String getLastDeliveryDescription() {
        return lastDeliveryDescription;
    }

    public void setLastDeliveryDescription(String lastDeliveryDescription) {
        this.lastDeliveryDescription = lastDeliveryDescription;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
