package com.cricketscore.dto.request;

import jakarta.validation.constraints.NotNull;

public class StartMatchRequest {

    @NotNull(message = "Opening striker player ID is required")
    private Long openingStrikerId;

    @NotNull(message = "Opening non-striker player ID is required")
    private Long openingNonStrikerId;

    @NotNull(message = "Opening bowler player ID is required")
    private Long openingBowlerId;

    public StartMatchRequest() {
    }

    public StartMatchRequest(Long openingStrikerId, Long openingNonStrikerId, Long openingBowlerId) {
        this.openingStrikerId = openingStrikerId;
        this.openingNonStrikerId = openingNonStrikerId;
        this.openingBowlerId = openingBowlerId;
    }

    public Long getOpeningStrikerId() {
        return openingStrikerId;
    }

    public void setOpeningStrikerId(Long openingStrikerId) {
        this.openingStrikerId = openingStrikerId;
    }

    public Long getOpeningNonStrikerId() {
        return openingNonStrikerId;
    }

    public void setOpeningNonStrikerId(Long openingNonStrikerId) {
        this.openingNonStrikerId = openingNonStrikerId;
    }

    public Long getOpeningBowlerId() {
        return openingBowlerId;
    }

    public void setOpeningBowlerId(Long openingBowlerId) {
        this.openingBowlerId = openingBowlerId;
    }
}
