package com.cricketscore.entity;

import com.cricketscore.entity.enums.DismissalType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "batting_scores", uniqueConstraints = {
    @UniqueConstraint(name = "uk_innings_batter", columnNames = {"innings_id", "player_id"})
})
public class BattingScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "innings_id", nullable = false)
    private Innings innings;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Integer runs = 0;

    @Column(name = "balls_faced", nullable = false)
    private Integer ballsFaced = 0;

    @Column(nullable = false)
    private Integer fours = 0;

    @Column(nullable = false)
    private Integer sixes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "dismissal_type", length = 50)
    private DismissalType dismissalType = DismissalType.NOT_OUT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissed_by_bowler_id")
    private Player dismissedByBowler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assisted_by_player_id")
    private Player assistedByPlayer;

    @Column(name = "batting_position", nullable = false)
    private Integer battingPosition;

    @Column(name = "is_on_strike", nullable = false)
    private boolean onStrike = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public BattingScore() {
    }

    public BattingScore(Innings innings, Player player, Integer battingPosition) {
        this.innings = innings;
        this.player = player;
        this.battingPosition = battingPosition;
        this.runs = 0;
        this.ballsFaced = 0;
        this.fours = 0;
        this.sixes = 0;
        this.dismissalType = DismissalType.NOT_OUT;
        this.onStrike = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public double getStrikeRate() {
        if (ballsFaced == null || ballsFaced == 0) return 0.0;
        int r = runs != null ? runs : 0;
        return Math.round(((r * 100.0) / ballsFaced) * 100.0) / 100.0;
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

    public Player getDismissedByBowler() {
        return dismissedByBowler;
    }

    public void setDismissedByBowler(Player dismissedByBowler) {
        this.dismissedByBowler = dismissedByBowler;
    }

    public Player getAssistedByPlayer() {
        return assistedByPlayer;
    }

    public void setAssistedByPlayer(Player assistedByPlayer) {
        this.assistedByPlayer = assistedByPlayer;
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
        BattingScore that = (BattingScore) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
