import { apiClient } from './apiClient';
import { MatchSummary, PlayerStatistics, TeamStatistics } from '../types';

export const statisticsService = {
  async getPlayerStatistics(playerId: number): Promise<PlayerStatistics> {
    return apiClient.get<PlayerStatistics>(`/players/${playerId}/statistics`);
  },

  async getTeamStatistics(teamId: number): Promise<TeamStatistics> {
    return apiClient.get<TeamStatistics>(`/teams/${teamId}/statistics`);
  },

  async getMatchSummary(matchId: number): Promise<MatchSummary> {
    return apiClient.get<MatchSummary>(`/matches/${matchId}/summary`);
  }
};
