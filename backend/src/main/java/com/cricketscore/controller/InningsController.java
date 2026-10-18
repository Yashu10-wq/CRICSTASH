package com.cricketscore.controller;

import com.cricketscore.dto.request.DeliveryCorrectionRequest;
import com.cricketscore.dto.request.DeliveryRequest;
import com.cricketscore.dto.response.ApiResponse;
import com.cricketscore.dto.response.BallEventResponse;
import com.cricketscore.dto.response.InningsResponse;
import com.cricketscore.dto.response.ScorecardResponse;
import com.cricketscore.service.InningsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/innings")
@Tag(name = "Scoring & Innings", description = "Real-time ball-by-ball scoring engine, deliveries, undo, and innings scorecards")
public class InningsController {

    private final InningsService inningsService;

    public InningsController(InningsService inningsService) {
        this.inningsService = inningsService;
    }

    @PostMapping("/{inningsId}/deliveries")
    @Operation(summary = "Record delivery", description = "Scores a single ball delivery with runs, extras, wickets, and strike rotation (Scorer/Admin)")
    public ResponseEntity<ApiResponse<ScorecardResponse>> recordDelivery(
            @PathVariable Long inningsId,
            @Valid @RequestBody DeliveryRequest request) {
        ScorecardResponse response = inningsService.recordDelivery(inningsId, request);
        return ResponseEntity.ok(ApiResponse.ok("Delivery recorded successfully", response));
    }

    @GetMapping("/{inningsId}")
    @Operation(summary = "Get innings summary", description = "Retrieves current totals, overs, run rates, and active batters/bowler")
    public ResponseEntity<ApiResponse<InningsResponse>> getInningsById(@PathVariable Long inningsId) {
        InningsResponse response = inningsService.getInningsById(inningsId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{inningsId}/scorecard")
    @Operation(summary = "Get innings scorecard", description = "Retrieves authoritative batting, bowling figures, and recent deliveries")
    public ResponseEntity<ApiResponse<ScorecardResponse>> getInningsScorecard(@PathVariable Long inningsId) {
        ScorecardResponse response = inningsService.getInningsScorecard(inningsId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{inningsId}/deliveries")
    @Operation(summary = "Get deliveries history", description = "Retrieves paginated ball-by-ball delivery event audit log")
    public ResponseEntity<ApiResponse<Page<BallEventResponse>>> getInningsDeliveries(
            @PathVariable Long inningsId,
            @PageableDefault(size = 30) Pageable pageable) {
        Page<BallEventResponse> page = inningsService.getInningsDeliveries(inningsId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @PostMapping("/{inningsId}/undo-last-delivery")
    @Operation(summary = "Undo last delivery", description = "Safely rollbacks the most recent delivery and recalculates scorecard totals (Scorer/Admin)")
    public ResponseEntity<ApiResponse<ScorecardResponse>> undoLastDelivery(@PathVariable Long inningsId) {
        ScorecardResponse response = inningsService.undoLastDelivery(inningsId);
        return ResponseEntity.ok(ApiResponse.ok("Last delivery undone successfully", response));
    }

    @PostMapping("/{inningsId}/corrections")
    @Operation(summary = "Correct delivery", description = "Applies an auditable correction to an earlier delivery sequence (Scorer/Admin)")
    public ResponseEntity<ApiResponse<ScorecardResponse>> correctDelivery(
            @PathVariable Long inningsId,
            @Valid @RequestBody DeliveryCorrectionRequest request) {
        ScorecardResponse response = inningsService.correctDelivery(inningsId, request);
        return ResponseEntity.ok(ApiResponse.ok("Delivery corrected successfully", response));
    }
}
