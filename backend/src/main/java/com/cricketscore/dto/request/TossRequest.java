package com.cricketscore.dto.request;

import com.cricketscore.entity.enums.TossDecision;
import jakarta.validation.constraints.NotNull;

public class TossRequest {

    @NotNull(message = "Toss winner team ID is required")
    private Long tossWinnerTeamId;

    @NotNull(message = "Toss decision is required (BAT or BOWL)")
    private TossDecision tossDecision;

    public TossRequest() {
    }

    public TossRequest(Long tossWinnerTeamId, TossDecision tossDecision) {
        this.tossWinnerTeamId = tossWinnerTeamId;
        this.tossDecision = tossDecision;
    }

    public Long getTossWinnerTeamId() {
        return tossWinnerTeamId;
    }

    public void setTossWinnerTeamId(Long tossWinnerTeamId) {
        this.tossWinnerTeamId = tossWinnerTeamId;
    }

    public TossDecision getTossDecision() {
        return tossDecision;
    }

    public void setTossDecision(TossDecision tossDecision) {
        this.tossDecision = tossDecision;
    }
}
