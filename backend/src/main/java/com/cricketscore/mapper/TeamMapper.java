package com.cricketscore.mapper;

import com.cricketscore.dto.response.TeamResponse;
import com.cricketscore.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team team) {
        if (team == null) return null;
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getShortName(),
                team.getCountry(),
                team.getLogoUrl(),
                team.getCreatedAt()
        );
    }
}
