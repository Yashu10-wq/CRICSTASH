package com.cricketscore.entity;

import com.cricketscore.entity.enums.DismissalType;
import com.cricketscore.entity.enums.ExtraType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "ball_events", uniqueConstraints = {
    @UniqueConstraint(name = "uk_innings_sequence", columnNames = {"innings_id", "event_sequence"})
})
public class BallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "innings_id", nullable = false)
    private Innings innings;

    @Column(name = "over_number", nullable = false)
    private Integer overNumber;

    @Column(name = "delivery_index", nullable = false)
    private Integer deliveryIndex;

    @Column(name = "event_sequence", nullable = false)
    private Long eventSequence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "striker_id", nullable = false)
    private Player striker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "non_striker_id", nullable = false)
    private Player nonStriker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bowler_id", nullable = false)
    private Player bowler;

    @Column(name = "batter_runs", nullable = false)
    private Integer batterRuns = 0;

    @Column(name = "extra_runs", nullable = false)
    private Integer extraRuns = 0;

    @Column(name = "total_runs", nullable = false)
    private Integer totalRuns = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "extra_type", nullable = false, length = 50)
    private ExtraType extraType = ExtraType.NONE;

    @Column(name = "is_legal_delivery", nullable = false)
    private boolean legalDelivery = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "wicket_type", length = 50)
    private DismissalType wicketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissed_player_id")
    private Player dismissedPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fielder_id")
    private Player fielder;

    @Column(length = 500)
    private String commentary;

    @Column(name = "is_undone", nullable = false)
    private boolean undone = false;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    public BallEvent() {
    }

    public BallEvent(Innings innings, Integer overNumber, Integer deliveryIndex, Long eventSequence,
                     Player striker, Player nonStriker, Player bowler,
                     Integer batterRuns, Integer extraRuns, Integer totalRuns,
                     ExtraType extraType, boolean legalDelivery,
                     DismissalType wicketType, Player dismissedPlayer, Player fielder,
                     String commentary) {
        this.innings = innings;
        this.overNumber = overNumber;
        this.deliveryIndex = deliveryIndex;
        this.eventSequence = eventSequence;
        this.striker = striker;
        this.nonStriker = nonStriker;
        this.bowler = bowler;
        this.batterRuns = batterRuns != null ? batterRuns : 0;
        this.extraRuns = extraRuns != null ? extraRuns : 0;
        this.totalRuns = totalRuns != null ? totalRuns : 0;
        this.extraType = extraType != null ? extraType : ExtraType.NONE;
        this.legalDelivery = legalDelivery;
        this.wicketType = wicketType;
        this.dismissedPlayer = dismissedPlayer;
        this.fielder = fielder;
        this.commentary = commentary;
        this.undone = false;
        this.recordedAt = Instant.now();
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

    public Integer getOverNumber() {
        return overNumber;
    }

    public void setOverNumber(Integer overNumber) {
        this.overNumber = overNumber;
    }

    public Integer getDeliveryIndex() {
        return deliveryIndex;
    }

    public void setDeliveryIndex(Integer deliveryIndex) {
        this.deliveryIndex = deliveryIndex;
    }

    public Long getEventSequence() {
        return eventSequence;
    }

    public void setEventSequence(Long eventSequence) {
        this.eventSequence = eventSequence;
    }

    public Player getStriker() {
        return striker;
    }

    public void setStriker(Player striker) {
        this.striker = striker;
    }

    public Player getNonStriker() {
        return nonStriker;
    }

    public void setNonStriker(Player nonStriker) {
        this.nonStriker = nonStriker;
    }

    public Player getBowler() {
        return bowler;
    }

    public void setBowler(Player bowler) {
        this.bowler = bowler;
    }

    public Integer getBatterRuns() {
        return batterRuns;
    }

    public void setBatterRuns(Integer batterRuns) {
        this.batterRuns = batterRuns;
    }

    public Integer getExtraRuns() {
        return extraRuns;
    }

    public void setExtraRuns(Integer extraRuns) {
        this.extraRuns = extraRuns;
    }

    public Integer getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(Integer totalRuns) {
        this.totalRuns = totalRuns;
    }

    public ExtraType getExtraType() {
        return extraType;
    }

    public void setExtraType(ExtraType extraType) {
        this.extraType = extraType;
    }

    public boolean isLegalDelivery() {
        return legalDelivery;
    }

    public void setLegalDelivery(boolean legalDelivery) {
        this.legalDelivery = legalDelivery;
    }

    public DismissalType getWicketType() {
        return wicketType;
    }

    public void setWicketType(DismissalType wicketType) {
        this.wicketType = wicketType;
    }

    public Player getDismissedPlayer() {
        return dismissedPlayer;
    }

    public void setDismissedPlayer(Player dismissedPlayer) {
        this.dismissedPlayer = dismissedPlayer;
    }

    public Player getFielder() {
        return fielder;
    }

    public void setFielder(Player fielder) {
        this.fielder = fielder;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public boolean isUndone() {
        return undone;
    }

    public void setUndone(boolean undone) {
        this.undone = undone;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BallEvent ballEvent = (BallEvent) o;
        return Objects.equals(id, ballEvent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
