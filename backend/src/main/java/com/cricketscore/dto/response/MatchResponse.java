package com.cricketscore.dto.response;

import com.cricketscore.entity.enums.MatchFormat;
import com.cricketscore.entity.enums.MatchResultType;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.entity.enums.TossDecision;

import java.time.Instant;

public class MatchResponse {
    private Long id;
    private String title;
    private MatchFormat format;
    private Integer maxOversPerInnings;
    private String competition;
    private String venue;
    private Instant startTime;
    private TeamResponse homeTeam;
    private TeamResponse awayTeam;
    private TeamResponse tossWinnerTeam;
    private TossDecision tossDecision;
    private MatchStatus status;
    private Integer currentInningsNumber;
    private TeamResponse winnerTeam;
    private MatchResultType resultType;
    private String resultSummary;
    private PlayerResponse playerOfTheMatch;
    private Instant createdAt;

    public MatchResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MatchFormat getFormat() {
        return format;
    }

    public void setFormat(MatchFormat format) {
        this.format = format;
    }

    public Integer getMaxOversPerInnings() {
        return maxOversPerInnings;
    }

    public void setMaxOversPerInnings(Integer maxOversPerInnings) {
        this.maxOversPerInnings = maxOversPerInnings;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public TeamResponse getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamResponse homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamResponse getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamResponse awayTeam) {
        this.awayTeam = awayTeam;
    }

    public TeamResponse getTossWinnerTeam() {
        return tossWinnerTeam;
    }

    public void setTossWinnerTeam(TeamResponse tossWinnerTeam) {
        this.tossWinnerTeam = tossWinnerTeam;
    }

    public TossDecision getTossDecision() {
        return tossDecision;
    }

    public void setTossDecision(TossDecision tossDecision) {
        this.tossDecision = tossDecision;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Integer getCurrentInningsNumber() {
        return currentInningsNumber;
    }

    public void setCurrentInningsNumber(Integer currentInningsNumber) {
        this.currentInningsNumber = currentInningsNumber;
    }

    public TeamResponse getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeam(TeamResponse winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    public MatchResultType getResultType() {
        return resultType;
    }

    public void setResultType(MatchResultType resultType) {
        this.resultType = resultType;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public PlayerResponse getPlayerOfTheMatch() {
        return playerOfTheMatch;
    }

    public void setPlayerOfTheMatch(PlayerResponse playerOfTheMatch) {
        this.playerOfTheMatch = playerOfTheMatch;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
