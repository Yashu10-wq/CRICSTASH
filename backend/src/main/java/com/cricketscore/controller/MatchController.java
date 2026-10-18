package com.cricketscore.controller;

import com.cricketscore.dto.request.*;
import com.cricketscore.dto.response.*;
import com.cricketscore.entity.enums.MatchStatus;
import com.cricketscore.service.InningsService;
import com.cricketscore.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches", description = "Match scheduling, lifecycle, squads, toss, and scorecards")
public class MatchController {

    private final MatchService matchService;
    private final InningsService inningsService;

    public MatchController(MatchService matchService, InningsService inningsService) {
        this.matchService = matchService;
        this.inningsService = inningsService;
    }

    @PostMapping
    @Operation(summary = "Schedule match", description = "Schedules a new cricket match (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> createMatch(@Valid @RequestBody MatchCreateRequest request) {
        MatchResponse response = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Match scheduled successfully", response));
    }

    @GetMapping
    @Operation(summary = "List matches", description = "Lists matches filtered by status or team, with pagination")
    public ResponseEntity<ApiResponse<Page<MatchResponse>>> getAllMatches(
            @RequestParam(required = false) MatchStatus status,
            @RequestParam(required = false) Long teamId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MatchResponse> page = matchService.getAllMatches(status, teamId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get match by id", description = "Retrieves full match details")
    public ResponseEntity<ApiResponse<MatchResponse>> getMatchById(@PathVariable Long id) {
        MatchResponse response = matchService.getMatchById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update match", description = "Updates match details (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> updateMatch(
            @PathVariable Long id,
            @Valid @RequestBody MatchCreateRequest request) {
        MatchResponse response = matchService.updateMatch(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Match updated successfully", response));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel match", description = "Cancels a scheduled or in-progress match (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> cancelMatch(@PathVariable Long id) {
        MatchResponse response = matchService.cancelMatch(id);
        return ResponseEntity.ok(ApiResponse.ok("Match cancelled successfully", response));
    }

    @PostMapping("/{id}/squads")
    @Operation(summary = "Configure match squads", description = "Selects participating squad, playing XI, captain and wicketkeeper (Admin only)")
    public ResponseEntity<ApiResponse<List<MatchSquadResponse>>> setupMatchSquad(
            @PathVariable Long id,
            @Valid @RequestBody MatchSquadSetupRequest request) {
        List<MatchSquadResponse> response = matchService.setupMatchSquad(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Match squad configured successfully", response));
    }

    @GetMapping("/{id}/squads")
    @Operation(summary = "Get match squads", description = "Retrieves selected squad and playing XI for a match")
    public ResponseEntity<ApiResponse<List<MatchSquadResponse>>> getMatchSquad(
            @PathVariable Long id,
            @RequestParam(required = false) Long teamId) {
        List<MatchSquadResponse> response = matchService.getMatchSquad(id, teamId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{id}/toss")
    @Operation(summary = "Record toss", description = "Records the toss winner and their decision to BAT or BOWL (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> recordToss(
            @PathVariable Long id,
            @Valid @RequestBody TossRequest request) {
        MatchResponse response = matchService.recordToss(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Toss recorded successfully", response));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start match", description = "Starts the match and initiates Innings 1 with opening batters and bowler (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> startMatch(
            @PathVariable Long id,
            @Valid @RequestBody StartMatchRequest request) {
        MatchResponse response = matchService.startMatch(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Match started successfully", response));
    }

    @PostMapping("/{id}/innings")
    @Operation(summary = "Create next innings", description = "Initiates subsequent innings (e.g. Innings 2 chase) (Scorer/Admin)")
    public ResponseEntity<ApiResponse<InningsResponse>> createInnings(
            @PathVariable Long id,
            @Valid @RequestBody InningsCreateRequest request) {
        InningsResponse response = inningsService.createInnings(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Innings initiated successfully", response));
    }

    @GetMapping("/{id}/scorecard")
    @Operation(summary = "Get match scorecard", description = "Retrieves complete scorecard of all innings in the match")
    public ResponseEntity<ApiResponse<List<ScorecardResponse>>> getMatchScorecard(@PathVariable Long id) {
        List<ScorecardResponse> response = inningsService.getMatchScorecards(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
