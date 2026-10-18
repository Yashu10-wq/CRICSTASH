package com.cricketscore.entity;

import com.cricketscore.entity.enums.InningsStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "innings", uniqueConstraints = {
    @UniqueConstraint(name = "uk_match_innings_number", columnNames = {"match_id", "innings_number"})
})
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "innings_number", nullable = false)
    private Integer inningsNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batting_team_id", nullable = false)
    private Team battingTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bowling_team_id", nullable = false)
    private Team bowlingTeam;

    @Column(name = "total_runs", nullable = false)
    private Integer totalRuns = 0;

    @Column(nullable = false)
    private Integer wickets = 0;

    @Column(name = "legal_balls_bowled", nullable = false)
    private Integer legalBallsBowled = 0;

    @Column(nullable = false)
    private Integer byes = 0;

    @Column(name = "leg_byes", nullable = false)
    private Integer legByes = 0;

    @Column(nullable = false)
    private Integer wides = 0;

    @Column(name = "no_balls", nullable = false)
    private Integer noBalls = 0;

    @Column(name = "penalty_runs", nullable = false)
    private Integer penaltyRuns = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private InningsStatus status = InningsStatus.NOT_STARTED;

    @Column(name = "target_runs")
    private Integer targetRuns;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_striker_id")
    private Player currentStriker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_non_striker_id")
    private Player currentNonStriker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_bowler_id")
    private Player currentBowler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_bowler_id")
    private Player lastBowler;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Innings() {
    }

    public Innings(Match match, Integer inningsNumber, Team battingTeam, Team bowlingTeam, Integer targetRuns) {
        this.match = match;
        this.inningsNumber = inningsNumber;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.targetRuns = targetRuns;
        this.status = InningsStatus.NOT_STARTED;
        this.totalRuns = 0;
        this.wickets = 0;
        this.legalBallsBowled = 0;
        this.byes = 0;
        this.legByes = 0;
        this.wides = 0;
        this.noBalls = 0;
        this.penaltyRuns = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public int getTotalExtras() {
        return (byes != null ? byes : 0) +
               (legByes != null ? legByes : 0) +
               (wides != null ? wides : 0) +
               (noBalls != null ? noBalls : 0) +
               (penaltyRuns != null ? penaltyRuns : 0);
    }

    public String getOversFormatted() {
        int balls = legalBallsBowled != null ? legalBallsBowled : 0;
        return (balls / 6) + "." + (balls % 6);
    }

    public double getCurrentRunRate() {
        int balls = legalBallsBowled != null ? legalBallsBowled : 0;
        int runs = totalRuns != null ? totalRuns : 0;
        if (balls == 0) return 0.0;
        return Math.round(((runs * 6.0) / balls) * 100.0) / 100.0;
    }

    public Double getRequiredRunRate(Integer maxOvers) {
        if (targetRuns == null || maxOvers == null) return null;
        int totalMaxBalls = maxOvers * 6;
        int ballsBowled = legalBallsBowled != null ? legalBallsBowled : 0;
        int ballsRemaining = totalMaxBalls - ballsBowled;
        int runsRemaining = targetRuns - (totalRuns != null ? totalRuns : 0);

        if (runsRemaining <= 0) return 0.0;
        if (ballsRemaining <= 0) return null; // Unachievable mathematically in regulation balls

        return Math.round(((runsRemaining * 6.0) / ballsRemaining) * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Integer getInningsNumber() {
        return inningsNumber;
    }

    public void setInningsNumber(Integer inningsNumber) {
        this.inningsNumber = inningsNumber;
    }

    public Team getBattingTeam() {
        return battingTeam;
    }

    public void setBattingTeam(Team battingTeam) {
        this.battingTeam = battingTeam;
    }

    public Team getBowlingTeam() {
        return bowlingTeam;
    }

    public void setBowlingTeam(Team bowlingTeam) {
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

    public Player getCurrentStriker() {
        return currentStriker;
    }

    public void setCurrentStriker(Player currentStriker) {
        this.currentStriker = currentStriker;
    }

    public Player getCurrentNonStriker() {
        return currentNonStriker;
    }

    public void setCurrentNonStriker(Player currentNonStriker) {
        this.currentNonStriker = currentNonStriker;
    }

    public Player getCurrentBowler() {
        return currentBowler;
    }

    public void setCurrentBowler(Player currentBowler) {
        this.currentBowler = currentBowler;
    }

    public Player getLastBowler() {
        return lastBowler;
    }

    public void setLastBowler(Player lastBowler) {
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
        Innings innings = (Innings) o;
        return Objects.equals(id, innings.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
