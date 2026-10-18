import { apiClient } from './apiClient';
import { BattingStyle, BowlingStyle, PageResponse, Player, PlayerRole } from '../types';

export interface CreatePlayerPayload {
  fullName: string;
  dateOfBirth?: string;
  nationality?: string;
  battingStyle?: BattingStyle;
  bowlingStyle?: BowlingStyle;
  primaryRole: PlayerRole;
}

export const playerService = {
  async getPlayers(params?: { query?: string; role?: PlayerRole; page?: number; size?: number }): Promise<PageResponse<Player>> {
    return apiClient.get<PageResponse<Player>>('/players', params);
  },

  async getPlayerById(id: number): Promise<Player> {
    return apiClient.get<Player>(`/players/${id}`);
  },

  async createPlayer(payload: CreatePlayerPayload): Promise<Player> {
    return apiClient.post<Player>('/players', payload);
  },

  async updatePlayer(id: number, payload: Partial<CreatePlayerPayload>): Promise<Player> {
    return apiClient.put<Player>(`/players/${id}`, payload);
  },

  async deletePlayer(id: number): Promise<void> {
    return apiClient.delete<void>(`/players/${id}`);
  }
};
