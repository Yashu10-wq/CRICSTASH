package com.cricketscore.mapper;

import com.cricketscore.dto.response.MatchResponse;
import com.cricketscore.entity.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    private final TeamMapper teamMapper;
    private final PlayerMapper playerMapper;

    public MatchMapper(TeamMapper teamMapper, PlayerMapper playerMapper) {
        this.teamMapper = teamMapper;
        this.playerMapper = playerMapper;
    }

    public MatchResponse toResponse(Match match) {
        if (match == null) return null;
        MatchResponse response = new MatchResponse();
        response.setId(match.getId());
        response.setTitle(match.getTitle());
        response.setFormat(match.getFormat());
        response.setMaxOversPerInnings(match.getMaxOversPerInnings());
        response.setCompetition(match.getCompetition());
        response.setVenue(match.getVenue());
        response.setStartTime(match.getStartTime());
        response.setHomeTeam(teamMapper.toResponse(match.getHomeTeam()));
        response.setAwayTeam(teamMapper.toResponse(match.getAwayTeam()));
        response.setTossWinnerTeam(teamMapper.toResponse(match.getTossWinnerTeam()));
        response.setTossDecision(match.getTossDecision());
        response.setStatus(match.getStatus());
        response.setCurrentInningsNumber(match.getCurrentInningsNumber());
        response.setWinnerTeam(teamMapper.toResponse(match.getWinnerTeam()));
        response.setResultType(match.getResultType());
        response.setResultSummary(match.getResultSummary());
        response.setPlayerOfTheMatch(playerMapper.toResponse(match.getPlayerOfTheMatch()));
        response.setCreatedAt(match.getCreatedAt());
        return response;
    }
}
