package com.cricketscore.entity;

import com.cricketscore.entity.enums.MatchFormat;
import com.cricketscore.entity.enums.MatchResultType;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.entity.enums.TossDecision;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MatchFormat format;

    @Column(name = "max_overs_per_innings")
    private Integer maxOversPerInnings;

    @Column(length = 100)
    private String competition;

    @Column(length = 150)
    private String venue;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toss_winner_team_id")
    private Team tossWinnerTeam;

    @Enumerated(EnumType.STRING)
    @Column(name = "toss_decision", length = 20)
    private TossDecision tossDecision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Column(name = "current_innings_number")
    private Integer currentInningsNumber = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", length = 50)
    private MatchResultType resultType;

    @Column(name = "result_summary", length = 255)
    private String resultSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_of_the_match_id")
    private Player playerOfTheMatch;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Match() {
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
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

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getTossWinnerTeam() {
        return tossWinnerTeam;
    }

    public void setTossWinnerTeam(Team tossWinnerTeam) {
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

    public Team getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeam(Team winnerTeam) {
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

    public Player getPlayerOfTheMatch() {
        return playerOfTheMatch;
    }

    public void setPlayerOfTheMatch(Player playerOfTheMatch) {
        this.playerOfTheMatch = playerOfTheMatch;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
