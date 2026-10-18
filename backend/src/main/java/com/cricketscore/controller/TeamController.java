package com.cricketscore.controller;

import com.cricketscore.dto.request.SquadMembershipRequest;
import com.cricketscore.dto.request.TeamCreateRequest;
import com.cricketscore.dto.response.ApiResponse;
import com.cricketscore.dto.response.SquadMembershipResponse;
import com.cricketscore.dto.response.TeamResponse;
import com.cricketscore.service.TeamService;
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
@RequestMapping("/api/v1/teams")
@Tag(name = "Teams", description = "Team and squad membership management")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @Operation(summary = "Create team", description = "Creates a new cricket team (Admin only)")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        TeamResponse response = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Team created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List teams", description = "Lists all teams with optional search query and pagination")
    public ResponseEntity<ApiResponse<Page<TeamResponse>>> getAllTeams(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TeamResponse> page = teamService.getAllTeams(query, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by id", description = "Retrieves team information by unique ID")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(@PathVariable Long id) {
        TeamResponse response = teamService.getTeamById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team", description = "Updates existing team details (Admin only)")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamCreateRequest request) {
        TeamResponse response = teamService.updateTeam(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Team updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team", description = "Deletes a team (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.ok("Team deleted successfully", null));
    }

    @PostMapping("/{teamId}/players")
    @Operation(summary = "Add player to team squad", description = "Adds a player to a team's registered squad")
    public ResponseEntity<ApiResponse<SquadMembershipResponse>> addPlayerToSquad(
            @PathVariable Long teamId,
            @Valid @RequestBody SquadMembershipRequest request) {
        SquadMembershipResponse response = teamService.addPlayerToSquad(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Player added to squad successfully", response));
    }

    @GetMapping("/{teamId}/players")
    @Operation(summary = "Get team squad", description = "Lists all active players in a team's squad")
    public ResponseEntity<ApiResponse<List<SquadMembershipResponse>>> getTeamSquad(@PathVariable Long teamId) {
        List<SquadMembershipResponse> response = teamService.getTeamSquad(teamId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    @Operation(summary = "Remove player from team squad", description = "Removes a player from a team's squad")
    public ResponseEntity<ApiResponse<Void>> removePlayerFromSquad(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        teamService.removePlayerFromSquad(teamId, playerId);
        return ResponseEntity.ok(ApiResponse.ok("Player removed from squad successfully", null));
    }
}
