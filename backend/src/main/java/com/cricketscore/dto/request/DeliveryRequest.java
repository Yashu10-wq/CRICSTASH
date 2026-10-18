package com.cricketscore.dto.request;

import com.cricketscore.entity.enums.DismissalType;
import com.cricketscore.entity.enums.ExtraType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DeliveryRequest {

    @Min(value = 0, message = "Batter runs cannot be negative")
    private Integer batterRuns = 0;

    @Min(value = 0, message = "Extra runs cannot be negative")
    private Integer extraRuns = 0;

    @NotNull(message = "Extra type must be provided (use NONE for normal delivery)")
    private ExtraType extraType = ExtraType.NONE;

    private DismissalType wicketType;

    private Long dismissedPlayerId;

    private Long fielderId;

    private Long newBatterId;

    private Long nextBowlerId;

    @Size(max = 500, message = "Commentary cannot exceed 500 characters")
    private String commentary;

    @Size(max = 100, message = "Idempotency key cannot exceed 100 characters")
    private String idempotencyKey;

    public DeliveryRequest() {
    }

    public DeliveryRequest(Integer batterRuns, Integer extraRuns, ExtraType extraType,
                           DismissalType wicketType, Long dismissedPlayerId, Long fielderId,
                           Long newBatterId, Long nextBowlerId, String commentary) {
        this.batterRuns = batterRuns != null ? batterRuns : 0;
        this.extraRuns = extraRuns != null ? extraRuns : 0;
        this.extraType = extraType != null ? extraType : ExtraType.NONE;
        this.wicketType = wicketType;
        this.dismissedPlayerId = dismissedPlayerId;
        this.fielderId = fielderId;
        this.newBatterId = newBatterId;
        this.nextBowlerId = nextBowlerId;
        this.commentary = commentary;
    }

    public Integer getBatterRuns() {
        return batterRuns != null ? batterRuns : 0;
    }

    public void setBatterRuns(Integer batterRuns) {
        this.batterRuns = batterRuns;
    }

    public Integer getExtraRuns() {
        return extraRuns != null ? extraRuns : 0;
    }

    public void setExtraRuns(Integer extraRuns) {
        this.extraRuns = extraRuns;
    }

    public ExtraType getExtraType() {
        return extraType != null ? extraType : ExtraType.NONE;
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

    public Long getDismissedPlayerId() {
        return dismissedPlayerId;
    }

    public void setDismissedPlayerId(Long dismissedPlayerId) {
        this.dismissedPlayerId = dismissedPlayerId;
    }

    public Long getFielderId() {
        return fielderId;
    }

    public void setFielderId(Long fielderId) {
        this.fielderId = fielderId;
    }

    public Long getNewBatterId() {
        return newBatterId;
    }

    public void setNewBatterId(Long newBatterId) {
        this.newBatterId = newBatterId;
    }

    public Long getNextBowlerId() {
        return nextBowlerId;
    }

    public void setNextBowlerId(Long nextBowlerId) {
        this.nextBowlerId = nextBowlerId;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
