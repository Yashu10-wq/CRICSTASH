package com.cricketscore.mapper;

import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public PlayerResponse toResponse(Player player) {
        if (player == null) return null;
        return new PlayerResponse(
                player.getId(),
                player.getFullName(),
                player.getDateOfBirth(),
                player.getNationality(),
                player.getBattingStyle(),
                player.getBowlingStyle(),
                player.getPrimaryRole(),
                player.getCreatedAt()
        );
    }
}
