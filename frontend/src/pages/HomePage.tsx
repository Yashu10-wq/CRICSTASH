import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Radio, Trophy, ArrowRight, RefreshCw, Flame, Calendar, MapPin, Activity } from 'lucide-react';
import { Match } from '../types';
import { matchService } from '../services/matchService';
import { cricApiService, CricApiMatch } from '../services/cricApiService';
import { MatchCard } from '../components/matches/MatchCard';
import { PageContainer } from '../components/layout/PageContainer';
import { LoadingSkeleton } from '../components/ui/States';

export const HomePage: React.FC = () => {
  const [matches, setMatches] = useState<Match[]>([]);
  const [rawCricMatches, setRawCricMatches] = useState<CricApiMatch[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<'ALL' | 'LIVE' | 'COMPLETED' | 'SCHEDULED'>('ALL');

  const fetchMatches = async () => {
    try {
      setLoading(true);
      setError(null);
      const [res, rawCric] = await Promise.all([
        matchService.getAllMatches({ size: 15 }),
        cricApiService.getCurrentMatches().catch(() => [] as CricApiMatch[])
      ]);
      setMatches(res.content || []);
      setRawCricMatches(rawCric || []);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to retrieve match data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMatches();
  }, []);

  const liveMatches = matches.filter((m) => m.status === 'LIVE' || m.status === 'INNINGS_BREAK');
  const completedMatches = matches.filter((m) => m.status === 'COMPLETED');
  const scheduledMatches = matches.filter((m) => m.status === 'SCHEDULED');

  const filteredMatches =
    filter === 'LIVE'
      ? liveMatches
      : filter === 'COMPLETED'
      ? completedMatches
      : filter === 'SCHEDULED'
      ? scheduledMatches
      : matches;

  return (
    <div className="space-y-8 pb-16 bg-[#F9FAFB] min-h-screen">
      {/* Top Live Ticker Strip */}
      <div className="bg-[#111827] text-white py-2.5 px-4 overflow-x-auto shadow-inner border-b border-gray-800">
        <div className="max-w-7xl mx-auto flex items-center gap-6 text-xs whitespace-nowrap">
          <div className="flex items-center gap-2 font-extrabold text-[#E53945] uppercase tracking-wider shrink-0">
            <span className="w-2 h-2 rounded-full bg-[#E53945] animate-ping" />
            LIVE TICKER
          </div>
          <div className="flex items-center gap-8 divide-x divide-gray-800">
            {matches.slice(0, 6).map((m) => (
              <Link
                key={m.id}
                to={`/matches/${m.id}`}
                className="pl-6 flex items-center gap-3 hover:text-red-400 transition-colors"
              >
                <span className="font-extrabold text-gray-200">
                  {m.homeTeam?.shortName || m.homeTeam?.name?.substring(0, 4)} vs{' '}
                  {m.awayTeam?.shortName || m.awayTeam?.name?.substring(0, 4)}
                </span>
                <span className="text-[11px] text-gray-400">
                  {m.resultSummary ? m.resultSummary : m.status === 'LIVE' ? 'LIVE NOW' : 'SCHEDULED'}
                </span>
              </Link>
            ))}
          </div>
        </div>
      </div>

      {/* Hero Section - Pure White & Red Minimalist Sports Design */}
      <section className="bg-white border-b border-gray-200 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-8">
          <div className="max-w-2xl space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-red-50 border border-red-200 text-xs font-extrabold text-[#E53945]">
              <span className="w-2 h-2 rounded-full bg-[#E53945] animate-pulse" />
              Live International & Domestic Match Engine
            </div>

            <h1 className="text-3xl sm:text-5xl font-black text-gray-900 tracking-tight leading-none">
              REAL-TIME CRICKET <br />
              <span className="text-[#E53945]">LIVE SCORE CENTRE</span>
            </h1>

            <p className="text-sm sm:text-base text-gray-600 font-medium leading-relaxed">
              Instant ball-by-ball commentary, real-time scorecards, live squad rosters, and precision cricket management connected to official live feeds.
            </p>

            <div className="pt-2 flex flex-wrap items-center gap-3">
              <button
                onClick={() => setFilter('LIVE')}
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#E53945] hover:bg-[#C5222E] text-white font-bold text-xs uppercase tracking-wider transition-all shadow-md active:scale-95"
              >
                <Radio className="w-4 h-4 animate-pulse" />
                Live Matches ({liveMatches.length})
              </button>
              <Link
                to="/matches"
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-800 font-bold text-xs uppercase tracking-wider transition-all"
              >
                All Fixtures ({matches.length})
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          </div>

          {/* Quick Match Highlight Card */}
          {liveMatches.length > 0 && (
            <div className="w-full md:w-96 bg-gray-50 rounded-2xl border-2 border-red-100 p-5 shadow-xs space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-extrabold text-[#E53945] bg-red-100/70 px-2 py-0.5 rounded">
                  FEATURED MATCH
                </span>
                <span className="text-xs font-bold text-gray-500 uppercase">{liveMatches[0].format}</span>
              </div>

              <div>
                <Link
                  to={`/matches/${liveMatches[0].id}`}
                  className="font-extrabold text-base text-gray-900 hover:text-[#E53945] transition-colors"
                >
                  {liveMatches[0].title}
                </Link>
                <div className="text-xs text-gray-500 mt-1 flex items-center gap-1.5">
                  <MapPin className="w-3.5 h-3.5 text-gray-400" />
                  {liveMatches[0].venue || 'Melbourne Cricket Ground'}
                </div>
              </div>

              <div className="p-3 bg-white rounded-xl border border-gray-200 flex items-center justify-between">
                <div className="font-bold text-sm text-gray-800">
                  {liveMatches[0].homeTeam?.name} vs {liveMatches[0].awayTeam?.name}
                </div>
                <div className="text-right font-black text-sm text-[#E53945]">
                  {liveMatches[0].resultSummary || 'LIVE'}
                </div>
              </div>

              <Link
                to={`/matches/${liveMatches[0].id}`}
                className="block text-center py-2 rounded-lg bg-[#E53945] text-white text-xs font-bold uppercase tracking-wider hover:bg-[#C5222E] transition-colors"
              >
                View Live Centre →
              </Link>
            </div>
          )}
        </div>
      </section>

      {/* Main Content Area */}
      <PageContainer className="space-y-8">
        {/* Filter Navigation Tabs */}
        <div className="flex items-center justify-between border-b border-gray-200 pb-3">
          <div className="flex items-center gap-2 sm:gap-4 overflow-x-auto">
            <button
              onClick={() => setFilter('ALL')}
              className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors ${
                filter === 'ALL'
                  ? 'bg-[#E53945] text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-gray-300'
              }`}
            >
              All Matches ({matches.length})
            </button>
            <button
              onClick={() => setFilter('LIVE')}
              className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors flex items-center gap-1.5 ${
                filter === 'LIVE'
                  ? 'bg-[#E53945] text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-gray-300'
              }`}
            >
              <span className={`w-2 h-2 rounded-full ${filter === 'LIVE' ? 'bg-white' : 'bg-[#E53945]'}`} />
              Live ({liveMatches.length})
            </button>
            <button
              onClick={() => setFilter('COMPLETED')}
              className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors ${
                filter === 'COMPLETED'
                  ? 'bg-[#E53945] text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-gray-300'
              }`}
            >
              Results ({completedMatches.length})
            </button>
            <button
              onClick={() => setFilter('SCHEDULED')}
              className={`px-3 py-1.5 rounded-lg text-xs font-extrabold uppercase tracking-wider transition-colors ${
                filter === 'SCHEDULED'
                  ? 'bg-[#E53945] text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-gray-300'
              }`}
            >
              Upcoming ({scheduledMatches.length})
            </button>
          </div>

          <button
            onClick={fetchMatches}
            title="Refresh match data"
            className="p-1.5 rounded-lg border border-gray-200 bg-white text-gray-500 hover:text-[#E53945] hover:border-red-200 transition-colors"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        {/* Match Cards Grid */}
        {loading ? (
          <LoadingSkeleton rows={3} />
        ) : filteredMatches.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredMatches.map((match) => (
              <MatchCard key={match.id} match={match} featured={match.status === 'LIVE'} />
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-2xl border border-gray-200 p-12 text-center text-gray-400 text-sm">
            No matches found for the selected category.
          </div>
        )}

        {/* Trending Players & Key Stars - Minimalist White & Red (Reference Screenshot 2 style) */}
        <section className="mt-12 bg-white rounded-2xl border border-gray-200 p-6 sm:p-8 shadow-xs">
          <div className="flex items-center justify-between mb-6 pb-4 border-b border-gray-100">
            <div className="flex items-center gap-2">
              <Flame className="w-5 h-5 text-[#E53945]" />
              <h2 className="text-lg font-black text-gray-900 tracking-tight uppercase">
                Featured International Players
              </h2>
            </div>
            <Link
              to="/players"
              className="text-xs font-extrabold text-[#E53945] hover:underline flex items-center gap-1"
            >
              View Player Directory →
            </Link>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {[
              { id: 2, name: 'Virat Kohli', team: 'India', role: 'Batter', stat: 'Runs: 4,008 | SR: 137.96' },
              { id: 1, name: 'Rohit Sharma', team: 'India', role: 'Batter', stat: 'Runs: 4,231 | 5x 100s' },
              { id: 6, name: 'Mitchell Starc', team: 'Australia', role: 'Bowler', stat: 'Wickets: 73 | Econ: 7.64' },
              { id: 4, name: 'Pat Cummins', team: 'Australia', role: 'Bowler', stat: 'Wickets: 57 | Econ: 7.42' }
            ].map((p) => (
              <Link
                key={p.id}
                to={`/players/${p.id}`}
                className="p-4 rounded-xl bg-gray-50 hover:bg-red-50/50 border border-gray-200 hover:border-red-200 transition-all space-y-2 block"
              >
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-extrabold text-[#E53945] uppercase tracking-wider">
                    {p.team}
                  </span>
                  <span className="text-[10px] font-bold text-gray-400 uppercase">{p.role}</span>
                </div>
                <div className="font-extrabold text-sm text-gray-900">{p.name}</div>
                <div className="text-xs text-gray-500 font-medium">{p.stat}</div>
              </Link>
            ))}
          </div>
        </section>
      </PageContainer>
    </div>
  );
};
