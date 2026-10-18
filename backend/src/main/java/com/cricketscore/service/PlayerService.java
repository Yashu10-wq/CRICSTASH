package com.cricketscore.service;

import com.cricketscore.dto.request.PlayerCreateRequest;
import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.entity.enums.PlayerRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlayerService {
    PlayerResponse createPlayer(PlayerCreateRequest request);
    Page<PlayerResponse> getAllPlayers(String query, PlayerRole role, Pageable pageable);
    PlayerResponse getPlayerById(Long id);
    PlayerResponse updatePlayer(Long id, PlayerCreateRequest request);
    void deletePlayer(Long id);
}
