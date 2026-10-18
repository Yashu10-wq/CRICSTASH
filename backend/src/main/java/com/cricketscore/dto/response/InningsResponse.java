package com.cricketscore.dto.response;

import com.cricketscore.entity.enums.InningsStatus;

import java.time.Instant;

public class InningsResponse {
    private Long id;
    private Long matchId;
    private Integer inningsNumber;
    private TeamResponse battingTeam;
    private TeamResponse bowlingTeam;
    private Integer totalRuns;
    private Integer wickets;
    private Integer legalBallsBowled;
    private String overs;
    private Integer byes;
    private Integer legByes;
    private Integer wides;
    private Integer noBalls;
    private Integer penaltyRuns;
    private Integer totalExtras;
    private InningsStatus status;
    private Integer targetRuns;
    private Double currentRunRate;
    private Double requiredRunRate;
    private PlayerResponse currentStriker;
    private PlayerResponse currentNonStriker;
    private PlayerResponse currentBowler;
    private PlayerResponse lastBowler;
    private Long version;
    private Instant createdAt;

    public InningsResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Integer getInningsNumber() {
        return inningsNumber;
    }

    public void setInningsNumber(Integer inningsNumber) {
        this.inningsNumber = inningsNumber;
    }

    public TeamResponse getBattingTeam() {
        return battingTeam;
    }

    public void setBattingTeam(TeamResponse battingTeam) {
        this.battingTeam = battingTeam;
    }

    public TeamResponse getBowlingTeam() {
        return bowlingTeam;
    }

    public void setBowlingTeam(TeamResponse bowlingTeam) {
        this.bowlingTeam = bowlingTeam;
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

    public Integer getLegalBallsBowled() {
        return legalBallsBowled;
    }

    public void setLegalBallsBowled(Integer legalBallsBowled) {
        this.legalBallsBowled = legalBallsBowled;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
    }

    public Integer getByes() {
        return byes;
    }

    public void setByes(Integer byes) {
        this.byes = byes;
    }

    public Integer getLegByes() {
        return legByes;
    }

    public void setLegByes(Integer legByes) {
        this.legByes = legByes;
    }

    public Integer getWides() {
        return wides;
    }

    public void setWides(Integer wides) {
        this.wides = wides;
    }

    public Integer getNoBalls() {
        return noBalls;
    }

    public void setNoBalls(Integer noBalls) {
        this.noBalls = noBalls;
    }

    public Integer getPenaltyRuns() {
        return penaltyRuns;
    }

    public void setPenaltyRuns(Integer penaltyRuns) {
        this.penaltyRuns = penaltyRuns;
    }

    public Integer getTotalExtras() {
        return totalExtras;
    }

    public void setTotalExtras(Integer totalExtras) {
        this.totalExtras = totalExtras;
    }

    public InningsStatus getStatus() {
        return status;
    }

    public void setStatus(InningsStatus status) {
        this.status = status;
    }

    public Integer getTargetRuns() {
        return targetRuns;
    }

    public void setTargetRuns(Integer targetRuns) {
        this.targetRuns = targetRuns;
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

    public PlayerResponse getCurrentStriker() {
        return currentStriker;
    }

    public void setCurrentStriker(PlayerResponse currentStriker) {
        this.currentStriker = currentStriker;
    }

    public PlayerResponse getCurrentNonStriker() {
        return currentNonStriker;
    }

    public void setCurrentNonStriker(PlayerResponse currentNonStriker) {
        this.currentNonStriker = currentNonStriker;
    }

    public PlayerResponse getCurrentBowler() {
        return currentBowler;
    }

    public void setCurrentBowler(PlayerResponse currentBowler) {
        this.currentBowler = currentBowler;
    }

    public PlayerResponse getLastBowler() {
        return lastBowler;
    }

    public void setLastBowler(PlayerResponse lastBowler) {
        this.lastBowler = lastBowler;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
