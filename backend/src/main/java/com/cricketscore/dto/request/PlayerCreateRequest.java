package com.cricketscore.dto.request;

import com.cricketscore.entity.enums.BattingStyle;
import com.cricketscore.entity.enums.BowlingStyle;
import com.cricketscore.entity.enums.PlayerRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PlayerCreateRequest {

    @NotBlank(message = "Player full name cannot be blank")
    @Size(max = 100, message = "Player full name cannot exceed 100 characters")
    private String fullName;

    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Nationality cannot exceed 100 characters")
    private String nationality;

    private BattingStyle battingStyle;

    private BowlingStyle bowlingStyle;

    @NotNull(message = "Primary player role is required")
    private PlayerRole primaryRole;

    public PlayerCreateRequest() {
    }

    public PlayerCreateRequest(String fullName, LocalDate dateOfBirth, String nationality,
                               BattingStyle battingStyle, BowlingStyle bowlingStyle, PlayerRole primaryRole) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
        this.primaryRole = primaryRole;
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
}
