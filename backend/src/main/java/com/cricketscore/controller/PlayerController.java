package com.cricketscore.controller;

import com.cricketscore.dto.request.PlayerCreateRequest;
import com.cricketscore.dto.response.ApiResponse;
import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.entity.enums.PlayerRole;
import com.cricketscore.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players", description = "Player registry and profile management")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    @Operation(summary = "Create player", description = "Registers a new player (Admin only)")
    public ResponseEntity<ApiResponse<PlayerResponse>> createPlayer(@Valid @RequestBody PlayerCreateRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Player created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List players", description = "Lists players with optional search query, role filter, and pagination")
    public ResponseEntity<ApiResponse<Page<PlayerResponse>>> getAllPlayers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) PlayerRole role,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PlayerResponse> page = playerService.getAllPlayers(query, role, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get player by id", description = "Retrieves player details by ID")
    public ResponseEntity<ApiResponse<PlayerResponse>> getPlayerById(@PathVariable Long id) {
        PlayerResponse response = playerService.getPlayerById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update player", description = "Updates player profile (Admin only)")
    public ResponseEntity<ApiResponse<PlayerResponse>> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerCreateRequest request) {
        PlayerResponse response = playerService.updatePlayer(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Player updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete player", description = "Deletes a player (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok(ApiResponse.ok("Player deleted successfully", null));
    }
}
