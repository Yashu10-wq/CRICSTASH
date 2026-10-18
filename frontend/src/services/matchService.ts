import { apiClient } from './apiClient';
import { Match, MatchFormat, MatchStatus, PageResponse, TossDecision } from '../types';
import { cricApiService, cricMatchToMatch, CricApiMatch } from './cricApiService';

export interface CreateMatchPayload {
  title: string;
  format: MatchFormat;
  maxOversPerInnings: number;
  homeTeamId: number;
  awayTeamId: number;
  startTime: string;
  venue?: string;
  competition?: string;
}

export interface TossPayload {
  tossWinnerTeamId: number;
  tossDecision: TossDecision;
}

export interface MatchSquadPayload {
  teamId: number;
  playingPlayerIds: number[];
  substitutePlayerIds?: number[];
  captainPlayerId?: number;
  wicketkeeperPlayerId?: number;
}

export const matchService = {
  async getAllMatches(params?: { status?: MatchStatus; page?: number; size?: number }): Promise<PageResponse<Match>> {
    // 1. Backend database matches
    let dbMatches: Match[] = [];
    try {
      const res = await apiClient.get<PageResponse<Match>>('/matches', params);
      dbMatches = res.content || [];
    } catch (e) {
      console.warn('Backend match fetch failed:', e);
    }

    // 2. CricAPI current / recent matches
    let currentCric: Match[] = [];
    try {
      const rawCurrent = await cricApiService.getCurrentMatches();
      currentCric = rawCurrent.map(cricMatchToMatch);
    } catch (e) {
      console.warn('CricAPI current match fetch failed:', e);
    }

    // 3. CricAPI upcoming matches
    let upcomingCric: Match[] = [];
    try {
      const rawUpcoming = await cricApiService.getUpcomingMatches();
      upcomingCric = rawUpcoming.map(cricMatchToMatch);
    } catch (e) {
      console.warn('CricAPI upcoming match fetch failed:', e);
    }

    // Deduplicate by ID
    const seen = new Set<string>();
    const combined: Match[] = [];

    // Backend matches first
    for (const m of dbMatches) {
      const key = String(m.id);
      if (!seen.has(key)) {
        seen.add(key);
        combined.push(m);
      }
    }
    // Then current & recent matches
    for (const m of currentCric) {
      const key = String(m.id);
      if (!seen.has(key)) {
        seen.add(key);
        combined.push(m);
      }
    }
    // Then upcoming matches
    for (const m of upcomingCric) {
      const key = String(m.id);
      if (!seen.has(key)) {
        seen.add(key);
        combined.push(m);
      }
    }

    let result = combined;
    if (params?.status) {
      result = result.filter((m) => m.status === params.status);
    }

    // Sort: LIVE first, then COMPLETED, then SCHEDULED
    const order: Record<string, number> = {
      LIVE: 1,
      INNINGS_BREAK: 2,
      COMPLETED: 3,
      SCHEDULED: 4,
      CANCELLED: 5,
      ABANDONED: 6
    };
    result.sort((a, b) => (order[a.status] || 99) - (order[b.status] || 99));

    return {
      content: result,
      totalElements: result.length,
      totalPages: Math.ceil(result.length / (params?.size || 25)) || 1
    };
  },

  async getMatchById(id: number | string): Promise<Match> {
    const isNumeric = typeof id === 'number' || /^\d+$/.test(String(id));
    if (isNumeric) {
      return apiClient.get<Match>(`/matches/${id}`);
    }

    // Check CricAPI current matches
    const currentList = await cricApiService.getCurrentMatches();
    const foundCurrent = currentList.find((m) => m.id === String(id));
    if (foundCurrent) {
      return cricMatchToMatch(foundCurrent);
    }

    // Check CricAPI upcoming matches
    const upcomingList = await cricApiService.getUpcomingMatches();
    const foundUpcoming = upcomingList.find((m) => m.id === String(id));
    if (foundUpcoming) {
      return cricMatchToMatch(foundUpcoming);
    }

    // Check Scorecard endpoint
    const card = await cricApiService.getMatchScorecard(String(id));
    if (card) {
      const teams = (card.name || '').split(' vs ');
      const t1 = teams[0] || 'Team 1';
      const t2 = teams[1]?.split(',')[0] || 'Team 2';
      return {
        id: card.id,
        title: card.name,
        format: (card.matchType?.toUpperCase() as MatchFormat) || 'T20',
        venue: card.venue || 'International Ground',
        competition: 'Live Cricket',
        startTime: card.date || new Date().toISOString(),
        status: card.status?.toLowerCase().includes('won') ? 'COMPLETED' : 'LIVE',
        resultSummary: card.status,
        homeTeam: { id: 9001, name: t1, shortName: t1.substring(0, 4).toUpperCase() },
        awayTeam: { id: 9002, name: t2, shortName: t2.substring(0, 4).toUpperCase() }
      };
    }

    throw new Error(`Match not found for id: ${id}`);
  },

  async createMatch(payload: CreateMatchPayload): Promise<Match> {
    return apiClient.post<Match>('/matches', payload);
  },

  async updateMatch(id: number, payload: Partial<CreateMatchPayload>): Promise<Match> {
    return apiClient.put<Match>(`/matches/${id}`, payload);
  },

  async cancelMatch(id: number): Promise<Match> {
    return apiClient.post<Match>(`/matches/${id}/cancel`);
  },

  async setSquads(id: number, payload: MatchSquadPayload): Promise<void> {
    return apiClient.post<void>(`/matches/${id}/squads`, payload);
  },

  async recordToss(id: number, payload: TossPayload): Promise<Match> {
    return apiClient.post<Match>(`/matches/${id}/toss`, payload);
  },

  async startMatch(id: number, payload?: { openingStrikerId?: number; openingNonStrikerId?: number; openingBowlerId?: number }): Promise<Match> {
    return apiClient.post<Match>(`/matches/${id}/start`, payload || {});
  }
};
