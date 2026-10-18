package com.cricketscore.service;

import com.cricketscore.dto.response.MatchSummaryResponse;
import com.cricketscore.dto.response.PlayerStatisticsResponse;
import com.cricketscore.dto.response.TeamStatisticsResponse;

public interface StatisticsService {
    PlayerStatisticsResponse getPlayerStatistics(Long playerId);
    TeamStatisticsResponse getTeamStatistics(Long teamId);
    MatchSummaryResponse getMatchSummary(Long matchId);
}
