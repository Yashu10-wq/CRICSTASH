package com.cricketscore.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "bowling_figures", uniqueConstraints = {
    @UniqueConstraint(name = "uk_innings_bowler", columnNames = {"innings_id", "player_id"})
})
public class BowlingFigure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "innings_id", nullable = false)
    private Innings innings;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "legal_balls_bowled", nullable = false)
    private Integer legalBallsBowled = 0;

    @Column(name = "runs_conceded", nullable = false)
    private Integer runsConceded = 0;

    @Column(nullable = false)
    private Integer wickets = 0;

    @Column(nullable = false)
    private Integer maidens = 0;

    @Column(nullable = false)
    private Integer wides = 0;

    @Column(name = "no_balls", nullable = false)
    private Integer noBalls = 0;

    @Column(nullable = false)
    private Integer dots = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public BowlingFigure() {
    }

    public BowlingFigure(Innings innings, Player player) {
        this.innings = innings;
        this.player = player;
        this.legalBallsBowled = 0;
        this.runsConceded = 0;
        this.wickets = 0;
        this.maidens = 0;
        this.wides = 0;
        this.noBalls = 0;
        this.dots = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public String getOversFormatted() {
        int balls = legalBallsBowled != null ? legalBallsBowled : 0;
        return (balls / 6) + "." + (balls % 6);
    }

    public double getEconomyRate() {
        int balls = legalBallsBowled != null ? legalBallsBowled : 0;
        int runs = runsConceded != null ? runsConceded : 0;
        if (balls == 0) return 0.0;
        return Math.round(((runs * 6.0) / balls) * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Innings getInnings() {
        return innings;
    }

    public void setInnings(Innings innings) {
        this.innings = innings;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getLegalBallsBowled() {
        return legalBallsBowled;
    }

    public void setLegalBallsBowled(Integer legalBallsBowled) {
        this.legalBallsBowled = legalBallsBowled;
    }

    public Integer getRunsConceded() {
        return runsConceded;
    }

    public void setRunsConceded(Integer runsConceded) {
        this.runsConceded = runsConceded;
    }

    public Integer getWickets() {
        return wickets;
    }

    public void setWickets(Integer wickets) {
        this.wickets = wickets;
    }

    public Integer getMaidens() {
        return maidens;
    }

    public void setMaidens(Integer maidens) {
        this.maidens = maidens;
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

    public Integer getDots() {
        return dots;
    }

    public void setDots(Integer dots) {
        this.dots = dots;
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
        BowlingFigure that = (BowlingFigure) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
