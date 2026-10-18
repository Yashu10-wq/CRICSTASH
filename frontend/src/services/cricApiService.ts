import { Match, MatchFormat, MatchStatus } from '../types';

export interface CricApiMatch {
  id: string;
  name: string;
  matchType: string;
  status: string;
  venue: string;
  date: string;
  dateTimeGMT: string;
  teams: string[];
  teamInfo?: Array<{
    name: string;
    shortname: string;
    img: string;
  }>;
  score?: Array<{
    r: number;
    w: number;
    o: number;
    inning: string;
  }>;
  matchStarted: boolean;
  matchEnded: boolean;
}

export interface CricApiScorecard {
  id: string;
  name: string;
  matchType: string;
  status: string;
  venue: string;
  date: string;
  scorecard: Array<{
    inning: string;
    batting: Array<{
      batsman: { id: string; name: string };
      dismissal: string;
      bowler?: { id: string; name: string };
      catcher?: { id: string; name: string };
      'dismissal-text'?: string;
      r: number;
      b: number;
      '4s': number;
      '6s': number;
      sr: number;
    }>;
    bowling: Array<{
      bowler: { id: string; name: string };
      o: number;
      m: number;
      r: number;
      w: number;
      eco: number;
    }>;
    totals?: {
      R: number;
      O: number;
      W: number;
    };
  }>;
}

const CRIC_API_KEY = import.meta.env.VITE_CRICKET_API_KEY || 'dbcd87c0-a9e8-408b-b010-909e51aafdeb';
const BASE_URL = 'https://api.cricapi.com/v1';

export function cricMatchToMatch(c: CricApiMatch): Match {
  const t1Name = c.teams?.[0] || 'Team 1';
  const t2Name = c.teams?.[1] || 'Team 2';
  const t1Short = c.teamInfo?.[0]?.shortname || t1Name.substring(0, 4).toUpperCase();
  const t2Short = c.teamInfo?.[1]?.shortname || t2Name.substring(0, 4).toUpperCase();
  const t1Logo = c.teamInfo?.[0]?.img;
  const t2Logo = c.teamInfo?.[1]?.img;

  let status: MatchStatus = 'SCHEDULED';
  if (c.matchEnded) {
    status = 'COMPLETED';
  } else if (c.matchStarted) {
    status = 'LIVE';
  }

  let format: MatchFormat = 'T20';
  const typeStr = (c.matchType || '').toLowerCase();
  if (typeStr.includes('odi')) format = 'ODI';
  else if (typeStr.includes('test')) format = 'TEST';

  const parts = (c.name || '').split(',');
  const comp = parts.length > 2 ? parts[parts.length - 1].trim() : parts[0];

  return {
    id: c.id,
    title: c.name,
    format,
    venue: c.venue || 'International Cricket Stadium',
    competition: comp,
    startTime: c.dateTimeGMT || c.date || new Date().toISOString(),
    status,
    resultSummary: c.status,
    homeTeam: {
      id: 9001,
      name: t1Name,
      shortName: t1Short,
      logoUrl: t1Logo
    },
    awayTeam: {
      id: 9002,
      name: t2Name,
      shortName: t2Short,
      logoUrl: t2Logo
    }
  };
}

export const cricApiService = {
  // Current & Recent Live Matches
  async getCurrentMatches(): Promise<CricApiMatch[]> {
    const cacheKey = 'cricstash_cricapi_current_cache';
    const cacheTimeKey = 'cricstash_cricapi_current_time';
    const now = Date.now();
    const cachedTime = sessionStorage.getItem(cacheTimeKey);

    // 10-minute cache to strictly respect 100 hits/day
    if (cachedTime && now - parseInt(cachedTime, 10) < 10 * 60 * 1000) {
      const cached = sessionStorage.getItem(cacheKey);
      if (cached) {
        try {
          return JSON.parse(cached);
        } catch {
          // ignore
        }
      }
    }

    try {
      const res = await fetch(`${BASE_URL}/currentMatches?apikey=${CRIC_API_KEY}&offset=0`);
      if (!res.ok) throw new Error(`CricAPI error: ${res.status}`);
      const json = await res.json();
      if (json.status === 'success' && Array.isArray(json.data)) {
        sessionStorage.setItem(cacheKey, JSON.stringify(json.data));
        sessionStorage.setItem(cacheTimeKey, now.toString());
        return json.data;
      }
    } catch (err) {
      console.warn('Failed to fetch from CricAPI currentMatches:', err);
      const cached = sessionStorage.getItem(cacheKey);
      if (cached) {
        try {
          return JSON.parse(cached);
        } catch {
          // ignore
        }
      }
    }
    return [];
  },

  // Upcoming Matches & Fixtures
  async getUpcomingMatches(): Promise<CricApiMatch[]> {
    const cacheKey = 'cricstash_cricapi_upcoming_cache';
    const cacheTimeKey = 'cricstash_cricapi_upcoming_time';
    const now = Date.now();
    const cachedTime = sessionStorage.getItem(cacheTimeKey);

    if (cachedTime && now - parseInt(cachedTime, 10) < 15 * 60 * 1000) {
      const cached = sessionStorage.getItem(cacheKey);
      if (cached) {
        try {
          return JSON.parse(cached);
        } catch {
          // ignore
        }
      }
    }

    try {
      const res = await fetch(`${BASE_URL}/matches?apikey=${CRIC_API_KEY}&offset=0`);
      if (!res.ok) throw new Error(`CricAPI error: ${res.status}`);
      const json = await res.json();
      if (json.status === 'success' && Array.isArray(json.data)) {
        sessionStorage.setItem(cacheKey, JSON.stringify(json.data));
        sessionStorage.setItem(cacheTimeKey, now.toString());
        return json.data;
      }
    } catch (err) {
      console.warn('Failed to fetch from CricAPI upcoming matches:', err);
      const cached = sessionStorage.getItem(cacheKey);
      if (cached) {
        try {
          return JSON.parse(cached);
        } catch {
          // ignore
        }
      }
    }
    return [];
  },

  // Full Scorecard
  async getMatchScorecard(matchId: string): Promise<CricApiScorecard | null> {
    const cacheKey = `cricstash_cricapi_scorecard_${matchId}`;
    const cached = sessionStorage.getItem(cacheKey);
    if (cached) {
      try {
        return JSON.parse(cached);
      } catch {
        // ignore
      }
    }

    try {
      const res = await fetch(`${BASE_URL}/match_scorecard?apikey=${CRIC_API_KEY}&id=${matchId}`);
      if (!res.ok) throw new Error(`CricAPI error: ${res.status}`);
      const json = await res.json();
      if (json.status === 'success' && json.data) {
        sessionStorage.setItem(cacheKey, JSON.stringify(json.data));
        return json.data;
      }
    } catch (err) {
      console.warn('Failed to fetch CricAPI scorecard:', err);
    }
    return null;
  }
};
