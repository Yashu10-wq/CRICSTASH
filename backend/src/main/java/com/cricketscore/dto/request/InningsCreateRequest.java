package com.cricketscore.dto.request;

import jakarta.validation.constraints.NotNull;

public class InningsCreateRequest {

    @NotNull(message = "Innings number is required")
    private Integer inningsNumber;

    @NotNull(message = "Opening striker player ID is required")
    private Long openingStrikerId;

    @NotNull(message = "Opening non-striker player ID is required")
    private Long openingNonStrikerId;

    @NotNull(message = "Opening bowler player ID is required")
    private Long openingBowlerId;

    public InningsCreateRequest() {
    }

    public InningsCreateRequest(Integer inningsNumber, Long openingStrikerId, Long openingNonStrikerId, Long openingBowlerId) {
        this.inningsNumber = inningsNumber;
        this.openingStrikerId = openingStrikerId;
        this.openingNonStrikerId = openingNonStrikerId;
        this.openingBowlerId = openingBowlerId;
    }

    public Integer getInningsNumber() {
        return inningsNumber;
    }

    public void setInningsNumber(Integer inningsNumber) {
        this.inningsNumber = inningsNumber;
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
