// Common API Response wrapper matching com.cricketscore.dto.response.ApiResponse
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

// Spring Data Page structure (VIA_DTO mode)
export interface PageResponse<T> {
  content: T[];
  page?: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
  pageable?: {
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements?: number;
  totalPages?: number;
  size?: number;
  number?: number;
  first?: boolean;
  last?: boolean;
  numberOfElements?: number;
  empty?: boolean;
}

export type MatchFormat = 'T20' | 'ODI' | 'TEST' | 'CUSTOM';
export type MatchStatus = 'SCHEDULED' | 'LIVE' | 'INNINGS_BREAK' | 'COMPLETED' | 'ABANDONED' | 'CANCELLED';
export type TossDecision = 'BAT' | 'BOWL';
export type MatchResultType = 'WIN_BY_RUNS' | 'WIN_BY_WICKETS' | 'TIE' | 'NO_RESULT' | 'DRAW';
export type PlayerRole = 'BATTER' | 'BOWLER' | 'ALL_ROUNDER' | 'WICKETKEEPER';
export type BattingStyle = 'RIGHT_HAND' | 'LEFT_HAND';
export type BowlingStyle =
  | 'RIGHT_ARM_FAST'
  | 'RIGHT_ARM_MEDIUM'
  | 'RIGHT_ARM_SPIN'
  | 'LEFT_ARM_FAST'
  | 'LEFT_ARM_MEDIUM'
  | 'LEFT_ARM_SPIN'
  | 'OTHER';

export interface Team {
  id: number;
  name: string;
  shortName: string;
  country?: string;
  logoUrl?: string;
  createdAt?: string;
}

export interface Player {
  id: number;
  fullName: string;
  dateOfBirth?: string;
  nationality?: string;
  battingStyle?: BattingStyle;
  bowlingStyle?: BowlingStyle;
  primaryRole: PlayerRole;
  createdAt?: string;
}

export interface Match {
  id: number | string;
  title: string;
  format: MatchFormat;
  maxOversPerInnings?: number;
  competition?: string;
  venue?: string;
  startTime: string;
  homeTeam: Team;
  awayTeam: Team;
  tossWinnerTeam?: Team;
  tossDecision?: TossDecision;
  status: MatchStatus;
  currentInningsNumber?: number;
  winnerTeam?: Team;
  resultType?: MatchResultType;
  resultSummary?: string;
  playerOfTheMatch?: Player;
  createdAt?: string;
}

export interface Innings {
  id: number;
  matchId: number;
  inningsNumber: number;
  battingTeam: Team;
  bowlingTeam: Team;
  totalRuns: number;
  wickets: number;
  legalBalls: number;
  overs: string;
  targetRuns?: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'DECLARED';
  currentRunRate?: number;
  requiredRunRate?: number;
  ballsRemaining?: number;
  runsRemaining?: number;
}

export interface BattingScore {
  id: number;
  playerId: number;
  playerName: string;
  battingPosition: number;
  runs: number;
  ballsFaced: number;
  fours: number;
  sixes: number;
  strikeRate: number;
  isOut: boolean;
  dismissalType: string;
  bowlerName?: string;
  fielderName?: string;
  onStrike: boolean;
  atCrease: boolean;
}

export interface BowlingFigure {
  id: number;
  playerId: number;
  playerName: string;
  legalBalls: number;
  overs: string;
  maidens: number;
  runsConceded: number;
  wickets: number;
  economy: number;
  wides: number;
  noBalls: number;
  dotBalls: number;
}

export interface BallEvent {
  id: number;
  inningsId: number;
  overNumber: number;
  deliveryInOver: number;
  deliverySequence: number;
  strikerName: string;
  nonStrikerName: string;
  bowlerName: string;
  batterRuns: number;
  totalExtras: number;
  totalRuns: number;
  extraType?: 'WIDE' | 'NO_BALL' | 'BYE' | 'LEG_BYE' | 'PENALTY';
  isLegal: boolean;
  isWicket: boolean;
  wicketType?: string;
  dismissedPlayerName?: string;
  description?: string;
  recordedAt: string;
}

export interface Scorecard {
  innings: Innings;
  battingScores: BattingScore[];
  bowlingFigures: BowlingFigure[];
  recentDeliveries?: BallEvent[];
}

export interface MatchSummary {
  match: Match;
  inningsScorecards: Scorecard[];
  matchResultSummary: string;
}

export interface PlayerStatistics {
  playerId: number;
  playerName: string;
  matchesPlayed: number;
  inningsBatted: number;
  totalRuns: number;
  ballsFaced: number;
  highestScore: number;
  battingAverage: number;
  battingStrikeRate: number;
  fours: number;
  sixes: number;
  inningsBowled: number;
  legalBallsBowled: number;
  oversBowled: string;
  runsConceded: number;
  wicketsTaken: number;
  maidens: number;
  bowlingEconomy: number;
  bowlingAverage: number;
  bowlingStrikeRate: number;
}

export interface TeamStatistics {
  teamId: number;
  teamName: string;
  matchesPlayed: number;
  matchesWon: number;
  matchesLost: number;
  matchesTied: number;
  matchesNoResult: number;
  winPercentage: number;
}

export interface ScoreUpdateMessage {
  eventType: 'DELIVERY_SCORED' | 'WICKET_FALL' | 'OVER_COMPLETED' | 'INNINGS_COMPLETED' | 'MATCH_COMPLETED' | 'DELIVERY_CORRECTED';
  matchId: number;
  inningsId: number;
  inningsNumber: number;
  totalRuns: number;
  wickets: number;
  overs: string;
  legalBalls: number;
  currentRunRate?: number;
  targetRuns?: number;
  requiredRunRate?: number;
  strikerName?: string;
  nonStrikerName?: string;
  bowlerName?: string;
  lastBallDescription?: string;
  matchStatus: MatchStatus;
  timestamp: string;
}

export interface AuthUser {
  username: string;
  role: 'ROLE_ADMIN' | 'ROLE_SCORER' | 'ROLE_VIEWER';
  token: string;
}
