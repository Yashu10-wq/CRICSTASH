package com.cricketscore.scoring;

import com.cricketscore.dto.request.DeliveryCorrectionRequest;
import com.cricketscore.dto.request.DeliveryRequest;
import com.cricketscore.entity.Innings;

public interface CricketScoringEngine {

    /**
     * Records and processes a delivery according to realistic cricket rules.
     * Updates Innings totals, Batting figures, Bowling figures, strike rotation, and event audit logs.
     */
    DeliveryOutcome processDelivery(Long inningsId, DeliveryRequest request);

    /**
     * Undoes the last recorded delivery in the innings, restoring exact previous state.
     */
    Innings undoLastDelivery(Long inningsId);

    /**
     * Auditable delivery correction for historical event sequence.
     */
    Innings correctDelivery(Long inningsId, DeliveryCorrectionRequest request);
}
