package com.cricketscore.dto.request;

import com.cricketscore.entity.enums.MatchFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class MatchCreateRequest {

    @NotBlank(message = "Match title is required")
    @Size(max = 200, message = "Match title cannot exceed 200 characters")
    private String title;

    @NotNull(message = "Match format is required")
    private MatchFormat format;

    private Integer maxOversPerInnings;

    @Size(max = 100, message = "Competition name cannot exceed 100 characters")
    private String competition;

    @Size(max = 150, message = "Venue cannot exceed 150 characters")
    private String venue;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "Home team ID is required")
    private Long homeTeamId;

    @NotNull(message = "Away team ID is required")
    private Long awayTeamId;

    public MatchCreateRequest() {
    }

    public MatchCreateRequest(String title, MatchFormat format, Integer maxOversPerInnings,
                              String competition, String venue, Instant startTime,
                              Long homeTeamId, Long awayTeamId) {
        this.title = title;
        this.format = format;
        this.maxOversPerInnings = maxOversPerInnings;
        this.competition = competition;
        this.venue = venue;
        this.startTime = startTime;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
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

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(Long awayTeamId) {
        this.awayTeamId = awayTeamId;
    }
}
