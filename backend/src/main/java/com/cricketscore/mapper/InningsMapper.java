package com.cricketscore.mapper;

import com.cricketscore.dto.response.InningsResponse;
import com.cricketscore.entity.Innings;
import org.springframework.stereotype.Component;

@Component
public class InningsMapper {

    private final TeamMapper teamMapper;
    private final PlayerMapper playerMapper;

    public InningsMapper(TeamMapper teamMapper, PlayerMapper playerMapper) {
        this.teamMapper = teamMapper;
        this.playerMapper = playerMapper;
    }

    public InningsResponse toResponse(Innings innings) {
        if (innings == null) return null;
        InningsResponse response = new InningsResponse();
        response.setId(innings.getId());
        response.setMatchId(innings.getMatch() != null ? innings.getMatch().getId() : null);
        response.setInningsNumber(innings.getInningsNumber());
        response.setBattingTeam(teamMapper.toResponse(innings.getBattingTeam()));
        response.setBowlingTeam(teamMapper.toResponse(innings.getBowlingTeam()));
        response.setTotalRuns(innings.getTotalRuns());
        response.setWickets(innings.getWickets());
        response.setLegalBallsBowled(innings.getLegalBallsBowled());
        response.setOvers(innings.getOversFormatted());
        response.setByes(innings.getByes());
        response.setLegByes(innings.getLegByes());
        response.setWides(innings.getWides());
        response.setNoBalls(innings.getNoBalls());
        response.setPenaltyRuns(innings.getPenaltyRuns());
        response.setTotalExtras(innings.getTotalExtras());
        response.setStatus(innings.getStatus());
        response.setTargetRuns(innings.getTargetRuns());
        response.setCurrentRunRate(innings.getCurrentRunRate());

        Integer maxOvers = (innings.getMatch() != null) ? innings.getMatch().getMaxOversPerInnings() : null;
        response.setRequiredRunRate(innings.getRequiredRunRate(maxOvers));

        response.setCurrentStriker(playerMapper.toResponse(innings.getCurrentStriker()));
        response.setCurrentNonStriker(playerMapper.toResponse(innings.getCurrentNonStriker()));
        response.setCurrentBowler(playerMapper.toResponse(innings.getCurrentBowler()));
        response.setLastBowler(playerMapper.toResponse(innings.getLastBowler()));
        response.setVersion(innings.getVersion());
        response.setCreatedAt(innings.getCreatedAt());
        return response;
    }
}
