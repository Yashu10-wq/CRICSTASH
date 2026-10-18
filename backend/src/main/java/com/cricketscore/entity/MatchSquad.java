package com.cricketscore.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "match_squads", uniqueConstraints = {
    @UniqueConstraint(name = "uk_match_player", columnNames = {"match_id", "player_id"})
})
public class MatchSquad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "is_playing_xi", nullable = false)
    private boolean playingXi = true;

    @Column(name = "is_captain", nullable = false)
    private boolean captain = false;

    @Column(name = "is_wicket_keeper", nullable = false)
    private boolean wicketKeeper = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public MatchSquad() {
    }

    public MatchSquad(Match match, Team team, Player player, boolean playingXi, boolean captain, boolean wicketKeeper) {
        this.match = match;
        this.team = team;
        this.player = player;
        this.playingXi = playingXi;
        this.captain = captain;
        this.wicketKeeper = wicketKeeper;
        this.createdAt = Instant.now();
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPlayingXi() {
        return playingXi;
    }

    public void setPlayingXi(boolean playingXi) {
        this.playingXi = playingXi;
    }

    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(boolean captain) {
        this.captain = captain;
    }

    public boolean isWicketKeeper() {
        return wicketKeeper;
    }

    public void setWicketKeeper(boolean wicketKeeper) {
        this.wicketKeeper = wicketKeeper;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchSquad that = (MatchSquad) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
