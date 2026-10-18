package com.cricketscore.service.impl;

import com.cricketscore.dto.request.PlayerCreateRequest;
import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.entity.Player;
import com.cricketscore.entity.enums.PlayerRole;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.PlayerMapper;
import com.cricketscore.repository.PlayerRepository;
import com.cricketscore.service.PlayerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    @Override
    @Transactional
    public PlayerResponse createPlayer(PlayerCreateRequest request) {
        Player player = new Player(
                request.getFullName(),
                request.getDateOfBirth(),
                request.getNationality(),
                request.getBattingStyle(),
                request.getBowlingStyle(),
                request.getPrimaryRole()
        );
        player = playerRepository.save(player);
        return playerMapper.toResponse(player);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlayerResponse> getAllPlayers(String query, PlayerRole role, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return playerRepository.findByFullNameContainingIgnoreCase(query, pageable).map(playerMapper::toResponse);
        } else if (role != null) {
            return playerRepository.findByPrimaryRole(role, pageable).map(playerMapper::toResponse);
        }
        return playerRepository.findAll(pageable).map(playerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));
        return playerMapper.toResponse(player);
    }

    @Override
    @Transactional
    public PlayerResponse updatePlayer(Long id, PlayerCreateRequest request) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        player.setFullName(request.getFullName());
        player.setDateOfBirth(request.getDateOfBirth());
        player.setNationality(request.getNationality());
        player.setBattingStyle(request.getBattingStyle());
        player.setBowlingStyle(request.getBowlingStyle());
        player.setPrimaryRole(request.getPrimaryRole());

        player = playerRepository.save(player);
        return playerMapper.toResponse(player);
    }

    @Override
    @Transactional
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }
}
