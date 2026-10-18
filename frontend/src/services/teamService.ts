import { apiClient } from './apiClient';
import { PageResponse, Player, Team } from '../types';

export interface CreateTeamPayload {
  name: string;
  shortName: string;
  country?: string;
  logoUrl?: string;
}

export const teamService = {
  async getTeams(params?: { search?: string; page?: number; size?: number }): Promise<PageResponse<Team>> {
    return apiClient.get<PageResponse<Team>>('/teams', params);
  },

  async getTeamById(id: number): Promise<Team> {
    return apiClient.get<Team>(`/teams/${id}`);
  },

  async createTeam(payload: CreateTeamPayload): Promise<Team> {
    return apiClient.post<Team>('/teams', payload);
  },

  async updateTeam(id: number, payload: Partial<CreateTeamPayload>): Promise<Team> {
    return apiClient.put<Team>(`/teams/${id}`, payload);
  },

  async deleteTeam(id: number): Promise<void> {
    return apiClient.delete<void>(`/teams/${id}`);
  },

  async getTeamPlayers(teamId: number): Promise<Player[]> {
    const raw = await apiClient.get<any[]>(`/teams/${teamId}/players`);
    return (raw || []).map((item) => ({
      id: item.playerId || item.id,
      fullName: item.playerName || item.fullName || `Player #${item.playerId || item.id}`,
      primaryRole: item.primaryRole || 'BATTER',
      battingStyle: item.battingStyle || 'RIGHT_HAND',
      nationality: item.nationality || item.teamName,
      jerseyNumber: item.jerseyNumber
    }));
  },

  async addPlayerToTeam(teamId: number, playerId: number, roleInTeam?: string): Promise<void> {
    return apiClient.post<void>(`/teams/${teamId}/players`, { playerId, roleInTeam });
  },

  async removePlayerFromTeam(teamId: number, playerId: number): Promise<void> {
    return apiClient.delete<void>(`/teams/${teamId}/players/${playerId}`);
  }
};
