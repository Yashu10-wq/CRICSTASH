package com.cricketscore.dto.request;

import com.cricketscore.entity.enums.DismissalType;
import com.cricketscore.entity.enums.ExtraType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryCorrectionRequest {

    @NotNull(message = "Event sequence number to correct is required")
    private Long eventSequence;

    @Min(value = 0, message = "Batter runs cannot be negative")
    private Integer batterRuns = 0;

    @Min(value = 0, message = "Extra runs cannot be negative")
    private Integer extraRuns = 0;

    @NotNull(message = "Extra type is required")
    private ExtraType extraType = ExtraType.NONE;

    private DismissalType wicketType;

    @NotBlank(message = "Correction reason must be documented for audit trail")
    private String reason;

    public DeliveryCorrectionRequest() {
    }

    public DeliveryCorrectionRequest(Long eventSequence, Integer batterRuns, Integer extraRuns,
                                     ExtraType extraType, DismissalType wicketType, String reason) {
        this.eventSequence = eventSequence;
        this.batterRuns = batterRuns;
        this.extraRuns = extraRuns;
        this.extraType = extraType;
        this.wicketType = wicketType;
        this.reason = reason;
    }

    public Long getEventSequence() {
        return eventSequence;
    }

    public void setEventSequence(Long eventSequence) {
        this.eventSequence = eventSequence;
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

    public ExtraType getExtraType() {
        return extraType;
    }

    public void setExtraType(ExtraType extraType) {
        this.extraType = extraType;
    }

    public DismissalType getWicketType() {
        return wicketType;
    }

    public void setWicketType(DismissalType wicketType) {
        this.wicketType = wicketType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
