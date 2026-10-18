import { apiClient } from './apiClient';
import { BallEvent, Innings, Scorecard } from '../types';

export interface StartInningsPayload {
  inningsNumber: number;
  battingTeamId: number;
  bowlingTeamId: number;
  strikerPlayerId: number;
  nonStrikerPlayerId: number;
  openingBowlerPlayerId: number;
  targetRuns?: number;
}

export interface RecordDeliveryPayload {
  strikerId: number;
  nonStrikerId: number;
  bowlerId: number;
  batterRuns: number;
  extraType?: 'WIDE' | 'NO_BALL' | 'BYE' | 'LEG_BYE' | 'PENALTY';
  extraRuns?: number;
  isWicket?: boolean;
  wicketType?: 'BOWLED' | 'CAUGHT' | 'LBW' | 'STUMPED' | 'RUN_OUT' | 'HIT_WICKET' | 'RETIRED_OUT' | 'OBSTRUCTING_FIELD';
  dismissedPlayerId?: number;
  assistedByPlayerId?: number;
  incomingBatterId?: number;
  commentary?: string;
  nextBowlerId?: number;
}

export interface DeliveryCorrectionPayload {
  batterRuns?: number;
  extraType?: 'WIDE' | 'NO_BALL' | 'BYE' | 'LEG_BYE' | 'PENALTY';
  extraRuns?: number;
  isWicket?: boolean;
  wicketType?: string;
  dismissedPlayerId?: number;
  commentary?: string;
  reason: string;
}

export const inningsService = {
  async startInnings(matchId: number, payload: StartInningsPayload): Promise<Innings> {
    return apiClient.post<Innings>(`/matches/${matchId}/innings`, payload);
  },

  async recordDelivery(inningsId: number, payload: RecordDeliveryPayload): Promise<Scorecard> {
    return apiClient.post<Scorecard>(`/innings/${inningsId}/deliveries`, payload);
  },

  async getInnings(inningsId: number): Promise<Innings> {
    return apiClient.get<Innings>(`/innings/${inningsId}`);
  },

  async getDeliveries(inningsId: number): Promise<BallEvent[]> {
    return apiClient.get<BallEvent[]>(`/innings/${inningsId}/deliveries`);
  },

  async getScorecard(inningsId: number): Promise<Scorecard> {
    return apiClient.get<Scorecard>(`/innings/${inningsId}/scorecard`);
  },

  async getMatchScorecards(matchId: number): Promise<Scorecard[]> {
    return apiClient.get<Scorecard[]>(`/matches/${matchId}/scorecard`);
  },

  async undoLastDelivery(inningsId: number): Promise<Scorecard> {
    return apiClient.post<Scorecard>(`/innings/${inningsId}/undo-last-delivery`);
  },

  async correctDelivery(inningsId: number, deliverySequence: number, payload: DeliveryCorrectionPayload): Promise<Scorecard> {
    return apiClient.put<Scorecard>(`/innings/${inningsId}/deliveries/${deliverySequence}/correct`, payload);
  }
};
