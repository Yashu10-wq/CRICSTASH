package com.cricketscore.service;

import com.cricketscore.dto.request.PlayerCreateRequest;
import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.entity.Player;
import com.cricketscore.entity.enums.BattingStyle;
import com.cricketscore.entity.enums.BowlingStyle;
import com.cricketscore.entity.enums.PlayerRole;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.PlayerMapper;
import com.cricketscore.repository.PlayerRepository;
import com.cricketscore.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerMapper playerMapper;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private Player player;
    private PlayerCreateRequest createRequest;
    private PlayerResponse playerResponse;

    @BeforeEach
    void setUp() {
        player = new Player(
                "Virat Kohli",
                LocalDate.of(1988, 11, 5),
                "India",
                BattingStyle.RIGHT_HAND,
                BowlingStyle.RIGHT_ARM_MEDIUM,
                PlayerRole.BATTER
        );
        player.setId(1L);

        createRequest = new PlayerCreateRequest(
                "Virat Kohli",
                LocalDate.of(1988, 11, 5),
                "India",
                BattingStyle.RIGHT_HAND,
                BowlingStyle.RIGHT_ARM_MEDIUM,
                PlayerRole.BATTER
        );

        playerResponse = new PlayerResponse(
                1L,
                "Virat Kohli",
                LocalDate.of(1988, 11, 5),
                "India",
                BattingStyle.RIGHT_HAND,
                BowlingStyle.RIGHT_ARM_MEDIUM,
                PlayerRole.BATTER,
                Instant.now()
        );
    }

    @Test
    @DisplayName("Should create player successfully")
    void testCreatePlayer() {
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(playerMapper.toResponse(player)).thenReturn(playerResponse);

        PlayerResponse created = playerService.createPlayer(createRequest);

        assertThat(created).isNotNull();
        assertThat(created.getFullName()).isEqualTo("Virat Kohli");
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    @DisplayName("Should retrieve player by ID")
    void testGetPlayerById_Success() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerMapper.toResponse(player)).thenReturn(playerResponse);

        PlayerResponse found = playerService.getPlayerById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when player not found")
    void testGetPlayerById_NotFound() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayerById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Player not found");
    }

    @Test
    @DisplayName("Should list players with pagination")
    void testGetAllPlayers() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(playerRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(player)));
        when(playerMapper.toResponse(player)).thenReturn(playerResponse);

        Page<PlayerResponse> result = playerService.getAllPlayers(null, null, pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Virat Kohli");
    }
}
