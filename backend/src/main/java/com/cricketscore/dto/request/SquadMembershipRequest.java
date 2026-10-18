package com.cricketscore.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SquadMembershipRequest {

    @NotNull(message = "Player ID is required")
    private Long playerId;

    private Integer jerseyNumber;

    private LocalDate joinedDate;

    public SquadMembershipRequest() {
    }

    public SquadMembershipRequest(Long playerId, Integer jerseyNumber, LocalDate joinedDate) {
        this.playerId = playerId;
        this.jerseyNumber = jerseyNumber;
        this.joinedDate = joinedDate;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }
}
