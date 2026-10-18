package com.cricketscore.controller;

import com.cricketscore.dto.response.ApiResponse;
import com.cricketscore.dto.response.MatchSummaryResponse;
import com.cricketscore.dto.response.PlayerStatisticsResponse;
import com.cricketscore.dto.response.TeamStatisticsResponse;
import com.cricketscore.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Statistics & Summaries", description = "Career player analytics, team performance records, and match summaries")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/players/{id}/statistics")
    @Operation(summary = "Get player career statistics", description = "Calculates batting average, strike rate, runs, wickets, and bowling economy")
    public ResponseEntity<ApiResponse<PlayerStatisticsResponse>> getPlayerStatistics(@PathVariable Long id) {
        PlayerStatisticsResponse response = statisticsService.getPlayerStatistics(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/teams/{id}/statistics")
    @Operation(summary = "Get team win/loss statistics", description = "Calculates matches played, won, lost, tied, and win percentage")
    public ResponseEntity<ApiResponse<TeamStatisticsResponse>> getTeamStatistics(@PathVariable Long id) {
        TeamStatisticsResponse response = statisticsService.getTeamStatistics(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/matches/{id}/summary")
    @Operation(summary = "Get match summary", description = "Retrieves complete match summary, final result, and all innings scorecards")
    public ResponseEntity<ApiResponse<MatchSummaryResponse>> getMatchSummary(@PathVariable Long id) {
        MatchSummaryResponse response = statisticsService.getMatchSummary(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
