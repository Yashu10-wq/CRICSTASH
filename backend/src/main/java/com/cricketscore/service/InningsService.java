package com.cricketscore.service;

import com.cricketscore.dto.request.DeliveryCorrectionRequest;
import com.cricketscore.dto.request.DeliveryRequest;
import com.cricketscore.dto.request.InningsCreateRequest;
import com.cricketscore.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InningsService {
    InningsResponse createInnings(Long matchId, InningsCreateRequest request);
    InningsResponse getInningsById(Long inningsId);
    Page<BallEventResponse> getInningsDeliveries(Long inningsId, Pageable pageable);
    List<BallEventResponse> getAllInningsDeliveries(Long inningsId);
    ScorecardResponse getInningsScorecard(Long inningsId);
    List<ScorecardResponse> getMatchScorecards(Long matchId);

    ScorecardResponse recordDelivery(Long inningsId, DeliveryRequest request);
    ScorecardResponse undoLastDelivery(Long inningsId);
    ScorecardResponse correctDelivery(Long inningsId, DeliveryCorrectionRequest request);
}
