package com.cricketscore.dto.response;

import com.cricketscore.entity.enums.BattingStyle;
import com.cricketscore.entity.enums.BowlingStyle;
import com.cricketscore.entity.enums.PlayerRole;

import java.time.Instant;
import java.time.LocalDate;

public class PlayerResponse {
    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String nationality;
    private BattingStyle battingStyle;
    private BowlingStyle bowlingStyle;
    private PlayerRole primaryRole;
    private Instant createdAt;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String fullName, LocalDate dateOfBirth, String nationality,
                          BattingStyle battingStyle, BowlingStyle bowlingStyle, PlayerRole primaryRole, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
        this.primaryRole = primaryRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public BattingStyle getBattingStyle() {
        return battingStyle;
    }

    public void setBattingStyle(BattingStyle battingStyle) {
        this.battingStyle = battingStyle;
    }

    public BowlingStyle getBowlingStyle() {
        return bowlingStyle;
    }

    public void setBowlingStyle(BowlingStyle bowlingStyle) {
        this.bowlingStyle = bowlingStyle;
    }

    public PlayerRole getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(PlayerRole primaryRole) {
        this.primaryRole = primaryRole;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
