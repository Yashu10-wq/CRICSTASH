import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Radio, MapPin, Calendar, RefreshCw, Trophy, ArrowLeft, Activity } from 'lucide-react';
import { Match, Scorecard, BallEvent, ScoreUpdateMessage } from '../types';
import { matchService } from '../services/matchService';
import { inningsService } from '../services/inningsService';
import { teamService } from '../services/teamService';
import { cricApiService, CricApiScorecard } from '../services/cricApiService';
import { websocketService, ConnectionStatus } from '../services/websocketService';
import { PageContainer } from '../components/layout/PageContainer';
import { TeamBadge } from '../components/ui/TeamBadge';
import { StatusBadge } from '../components/ui/StatusBadge';
import { BallChip } from '../components/ui/BallChip';
import { BattingTable } from '../components/scorecard/BattingTable';
import { BowlingTable } from '../components/scorecard/BowlingTable';
import { PitchVisualizer } from '../components/matches/PitchVisualizer';
import { ScorerConsole } from '../components/scoring/ScorerConsole';
import { LoadingSkeleton, ErrorState } from '../components/ui/States';
import { useAuth } from '../context/AuthContext';
import { Player } from '../types';

export const MatchDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const rawId = id || '';
  const isNumericId = /^\d+$/.test(rawId);
  const matchId = isNumericId ? Number(rawId) : 0;

  const [match, setMatch] = useState<Match | null>(null);
  const [scorecards, setScorecards] = useState<Scorecard[]>([]);
  const [deliveries, setDeliveries] = useState<BallEvent[]>([]);
  const [homePlayers, setHomePlayers] = useState<Player[]>([]);
  const [awayPlayers, setAwayPlayers] = useState<Player[]>([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'centre' | 'scorecard' | 'summary' | 'scorer'>('centre');
  const [wsStatus, setWsStatus] = useState<ConnectionStatus>('DISCONNECTED');

  const { isScorer } = useAuth();

  const loadMatchData = async () => {
    if (!rawId) return;
    try {
      setLoading(true);
      setError(null);

      if (isNumericId) {
        // Backend DB Match
        const [matchData, scData] = await Promise.all([
          matchService.getMatchById(matchId),
          inningsService.getMatchScorecards(matchId).catch(() => [] as Scorecard[])
        ]);

        setMatch(matchData);
        setScorecards(scData);

        if (matchData.homeTeam?.id && matchData.awayTeam?.id) {
          const [hp, ap] = await Promise.all([
            teamService.getTeamPlayers(matchData.homeTeam.id).catch(() => []),
            teamService.getTeamPlayers(matchData.awayTeam.id).catch(() => [])
          ]);
          setHomePlayers(hp);
          setAwayPlayers(ap);
        }

        if (scData.length > 0) {
          const latestInnings = scData[scData.length - 1].innings;
          const dels = await inningsService.getDeliveries(latestInnings.id).catch(() => []);
          setDeliveries(dels);
        }
      } else {
        // CricAPI Match
        const [matchData, cricCard] = await Promise.all([
          matchService.getMatchById(rawId),
          cricApiService.getMatchScorecard(rawId)
        ]);
        setMatch(matchData);

        if (cricCard && cricCard.scorecard) {
          const mappedScorecards: Scorecard[] = cricCard.scorecard.map((inn, idx) => {
            const teamName = inn.inning.split(' Inning')[0] || `Innings ${idx + 1}`;
            return {
              innings: {
                id: idx + 1,
                matchId: 9999,
                inningsNumber: idx + 1,
                battingTeam: { id: idx + 1, name: teamName, shortName: teamName.substring(0, 4).toUpperCase() },
                bowlingTeam: { id: idx + 2, name: 'Opponent', shortName: 'OPP' },
                totalRuns: inn.totals?.R ?? (inn.batting?.reduce((sum, b) => sum + (b.r || 0), 0) || 0),
                wickets: inn.totals?.W ?? (inn.batting?.filter((b) => b.dismissal && b.dismissal !== 'not out').length || 0),
                legalBalls: Math.floor((inn.totals?.O || 20) * 6),
                overs: `${inn.totals?.O || '20.0'}`,
                status: 'COMPLETED'
              },
              battingScores: (inn.batting || []).map((b, bIdx) => ({
                id: bIdx + 1,
                playerId: bIdx + 1,
                playerName: b.batsman?.name || `Batter ${bIdx + 1}`,
                battingPosition: bIdx + 1,
                runs: b.r || 0,
                ballsFaced: b.b || 0,
                fours: b['4s'] || 0,
                sixes: b['6s'] || 0,
                strikeRate: b.sr || 0,
                isOut: !!b.dismissal && b.dismissal.toLowerCase() !== 'not out',
                dismissalType: b['dismissal-text'] || b.dismissal || 'not out',
                onStrike: bIdx === 0,
                atCrease: !b.dismissal || b.dismissal.toLowerCase() === 'not out'
              })),
              bowlingFigures: (inn.bowling || []).map((bw, bwIdx) => ({
                id: bwIdx + 1,
                playerId: bwIdx + 1,
                playerName: bw.bowler?.name || `Bowler ${bwIdx + 1}`,
                overs: `${bw.o || 0}`,
                maidens: bw.m || 0,
                runsConceded: bw.r || 0,
                wickets: bw.w || 0,
                economy: bw.eco || 0,
                dotBalls: 0,
                wides: 0,
                noBalls: 0,
                legalBalls: Math.floor((bw.o || 0) * 6)
              })),
              recentDeliveries: []
            };
          });
          setScorecards(mappedScorecards);
        }
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to retrieve match details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMatchData();
  }, [rawId]);

  // STOMP WebSocket Live Subscription for local backend matches
  useEffect(() => {
    if (!isNumericId || !matchId) return;

    const unsubscribe = websocketService.subscribeToMatch(
      matchId,
      (update: ScoreUpdateMessage) => {
        setScorecards((prev) => {
          return prev.map((sc) => {
            if (sc.innings.id === update.inningsId) {
              return {
                ...sc,
                innings: {
                  ...sc.innings,
                  totalRuns: update.totalRuns,
                  wickets: update.wickets,
                  overs: update.overs,
                  legalBalls: update.legalBalls,
                  currentRunRate: update.currentRunRate,
                  targetRuns: update.targetRuns || sc.innings.targetRuns,
                  requiredRunRate: update.requiredRunRate || sc.innings.requiredRunRate
                }
              };
            }
            return sc;
          });
        });

        if (match) {
          setMatch((prev) => (prev ? { ...prev, status: update.matchStatus as any } : prev));
        }

        if (update.inningsId) {
          inningsService.getDeliveries(update.inningsId).then((dels) => setDeliveries(dels)).catch(() => {});
        }
      },
      (status) => setWsStatus(status)
    );

    return () => {
      unsubscribe();
    };
  }, [matchId, isNumericId]);

  if (loading) {
    return (
      <PageContainer>
        <LoadingSkeleton rows={4} />
      </PageContainer>
    );
  }

  if (error || !match) {
    return (
      <PageContainer>
        <ErrorState message={error || 'Match not found'} onRetry={loadMatchData} />
      </PageContainer>
    );
  }

  const currentScorecard = scorecards.length > 0 ? scorecards[scorecards.length - 1] : undefined;
  const currentInnings = currentScorecard?.innings;
  const activeBatters = currentScorecard?.battingScores.slice(0, 2) || [];
  const activeBowler = currentScorecard?.bowlingFigures[0];

  return (
    <div className="space-y-6 pb-16 bg-[#FAFAFA] min-h-screen">
      {/* Top Breadcrumb & Live Strip */}
      <div className="bg-white border-b border-gray-200">
        <PageContainer className="py-3 flex items-center justify-between">
          <Link
            to="/matches"
            className="inline-flex items-center gap-1.5 text-xs font-bold text-gray-500 hover:text-[#E53945] transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to All Matches
          </Link>

          <div className="flex items-center gap-3">
            {isNumericId && (
              <div
                className={`flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[10px] font-bold border ${
                  wsStatus === 'CONNECTED'
                    ? 'bg-red-50 text-[#E53945] border-red-200'
                    : 'bg-gray-100 text-gray-600 border-gray-200'
                }`}
              >
                <span
                  className={`w-2 h-2 rounded-full ${
                    wsStatus === 'CONNECTED' ? 'bg-[#E53945] animate-ping' : 'bg-gray-400'
                  }`}
                />
                {wsStatus === 'CONNECTED' ? 'LIVE SYNC' : 'STATIC'}
              </div>
            )}
            <StatusBadge status={match.status} size="sm" />
            <button
              onClick={loadMatchData}
              title="Refresh scorecard"
              className="p-1 rounded-md text-gray-400 hover:text-[#E53945] transition-colors"
            >
              <RefreshCw className="w-3.5 h-3.5" />
            </button>
          </div>
        </PageContainer>
      </div>

      {/* Main Hero Scoreboard Card - Pure White & Red Styling */}
      <PageContainer className="py-0">
        <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
          {/* Red Header Tag */}
          <div className="bg-[#E53945] text-white px-6 py-2.5 flex items-center justify-between text-xs font-bold tracking-wide">
            <div className="flex items-center gap-2">
              <span className="bg-white/20 px-2 py-0.5 rounded text-[11px] font-extrabold uppercase">
                {match.format}
              </span>
              <span className="truncate max-w-md">{match.title}</span>
            </div>
            {match.venue && (
              <span className="hidden sm:inline-flex items-center gap-1 text-white/90">
                <MapPin className="w-3.5 h-3.5" />
                {match.venue}
              </span>
            )}
          </div>

          {/* Large Match Summary Display */}
          <div className="p-6 lg:p-8 grid grid-cols-1 md:grid-cols-2 gap-8 items-center border-b border-gray-100">
            {/* Team 1 Score */}
            <div className="flex items-center justify-between p-5 rounded-xl bg-gray-50/80 border border-gray-100">
              <TeamBadge team={match.homeTeam} size="lg" />
              <div className="text-right">
                {scorecards[0] ? (
                  <div>
                    <div className="text-3xl sm:text-4xl font-black text-gray-900 tracking-tight">
                      {scorecards[0].innings.totalRuns}
                      <span className="text-[#E53945]">/{scorecards[0].innings.wickets}</span>
                    </div>
                    <div className="text-xs font-bold text-gray-500 mt-0.5">
                      {scorecards[0].innings.overs} ov
                    </div>
                  </div>
                ) : (
                  <span className="text-xs font-bold text-gray-400 bg-gray-200/60 px-3 py-1.5 rounded-md">
                    Yet to bat
                  </span>
                )}
              </div>
            </div>

            {/* Team 2 Score */}
            <div className="flex items-center justify-between p-5 rounded-xl bg-gray-50/80 border border-gray-100">
              <TeamBadge team={match.awayTeam} size="lg" />
              <div className="text-right">
                {scorecards[1] ? (
                  <div>
                    <div className="text-3xl sm:text-4xl font-black text-gray-900 tracking-tight">
                      {scorecards[1].innings.totalRuns}
                      <span className="text-[#E53945]">/{scorecards[1].innings.wickets}</span>
                    </div>
                    <div className="text-xs font-bold text-gray-500 mt-0.5">
                      {scorecards[1].innings.overs} ov
                    </div>
                  </div>
                ) : (
                  <span className="text-xs font-bold text-gray-400 bg-gray-200/60 px-3 py-1.5 rounded-md">
                    Yet to bat
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Result / Toss / Status Ribbon */}
          <div className="px-6 py-3.5 bg-gray-50/60 flex flex-wrap items-center justify-between gap-3 text-xs">
            <div className="font-extrabold text-[#E53945]">
              {match.resultSummary ? (
                <span>• {match.resultSummary}</span>
              ) : match.status === 'LIVE' ? (
                <span className="flex items-center gap-1.5 animate-pulse">
                  <span className="w-2.5 h-2.5 rounded-full bg-[#E53945]" />
                  Live Match in Progress
                </span>
              ) : (
                <span>Match Scheduled</span>
              )}
            </div>

            <div className="text-gray-500 font-medium">
              {match.startTime && new Date(match.startTime).toLocaleString()}
            </div>
          </div>
        </div>

        {/* Tab Navigation - Minimalist Red & White */}
        <div className="mt-8 flex items-center border-b border-gray-200 gap-8">
          <button
            onClick={() => setActiveTab('centre')}
            className={`pb-3.5 text-sm font-extrabold transition-all border-b-2 flex items-center gap-2 ${
              activeTab === 'centre'
                ? 'border-[#E53945] text-[#E53945]'
                : 'border-transparent text-gray-500 hover:text-gray-900'
            }`}
          >
            <Activity className="w-4 h-4" />
            Live Match Centre
          </button>
          <button
            onClick={() => setActiveTab('scorecard')}
            className={`pb-3.5 text-sm font-extrabold transition-all border-b-2 flex items-center gap-2 ${
              activeTab === 'scorecard'
                ? 'border-[#E53945] text-[#E53945]'
                : 'border-transparent text-gray-500 hover:text-gray-900'
            }`}
          >
            <Trophy className="w-4 h-4" />
            Full Scorecard
          </button>
          {isNumericId && isScorer && currentInnings && currentScorecard && (
            <button
              onClick={() => setActiveTab('scorer')}
              className={`pb-3.5 text-sm font-extrabold transition-all border-b-2 flex items-center gap-2 ${
                activeTab === 'scorer'
                  ? 'border-[#E53945] text-[#E53945]'
                  : 'border-transparent text-gray-500 hover:text-gray-900'
              }`}
            >
              Scorer Console
            </button>
          )}
        </div>

        {/* Tab 1: Live Match Centre */}
        {activeTab === 'centre' && (
          <div className="mt-6 space-y-6">
            {/* Live At-Crease Batsmen & Bowler Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Active Batsmen Card */}
              <div className="bg-white rounded-xl border border-gray-200 p-5 shadow-xs">
                <div className="flex items-center justify-between pb-3 border-b border-gray-100 mb-3">
                  <h3 className="text-xs font-extrabold text-gray-500 uppercase tracking-wider">
                    Batter at Crease
                  </h3>
                  <span className="text-[11px] font-bold text-gray-400">R (B) 4s 6s SR</span>
                </div>

                {activeBatters.length > 0 ? (
                  <div className="space-y-3">
                    {activeBatters.map((b, idx) => (
                      <div key={b.id || idx} className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <span className="font-extrabold text-sm text-gray-900">
                            {b.playerName}
                            {idx === 0 && <span className="text-[#E53945] ml-1 font-black">*</span>}
                          </span>
                          {b.isOut && (
                            <span className="text-[10px] text-gray-400">({b.dismissalType})</span>
                          )}
                        </div>
                        <div className="flex items-center gap-3 text-xs font-bold text-gray-700">
                          <span className="text-sm font-black text-gray-900">{b.runs}</span>
                          <span className="text-gray-400">({b.ballsFaced})</span>
                          <span className="text-gray-500">{b.fours}</span>
                          <span className="text-gray-500">{b.sixes}</span>
                          <span className="text-[#E53945] font-extrabold">{b.strikeRate.toFixed(1)}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="py-4 text-center text-xs text-gray-400 font-medium">
                    No active batters recorded
                  </div>
                )}
              </div>

              {/* Active Bowler Card */}
              <div className="bg-white rounded-xl border border-gray-200 p-5 shadow-xs">
                <div className="flex items-center justify-between pb-3 border-b border-gray-100 mb-3">
                  <h3 className="text-xs font-extrabold text-gray-500 uppercase tracking-wider">
                    Current Bowler
                  </h3>
                  <span className="text-[11px] font-bold text-gray-400">O M R W ECO</span>
                </div>

                {activeBowler ? (
                  <div className="flex items-center justify-between">
                    <span className="font-extrabold text-sm text-gray-900">
                      {activeBowler.playerName}
                    </span>
                    <div className="flex items-center gap-3 text-xs font-bold text-gray-700">
                      <span>{activeBowler.overs}</span>
                      <span className="text-gray-400">{activeBowler.maidens}</span>
                      <span>{activeBowler.runsConceded}</span>
                      <span className="text-[#E53945] font-black text-sm">{activeBowler.wickets}</span>
                      <span className="text-gray-500">{activeBowler.economy.toFixed(2)}</span>
                    </div>
                  </div>
                ) : (
                  <div className="py-4 text-center text-xs text-gray-400 font-medium">
                    No active bowler figures
                  </div>
                )}
              </div>
            </div>

            {/* Recent Balls Strip */}
            {deliveries.length > 0 && (
              <div className="bg-white rounded-xl border border-gray-200 p-4 shadow-xs flex items-center gap-3 overflow-x-auto">
                <span className="text-xs font-extrabold text-gray-500 uppercase shrink-0">
                  Recent Balls:
                </span>
                <div className="flex items-center gap-2">
                  {deliveries.slice(-12).map((del) => (
                    <BallChip key={del.id} ball={del} size="md" />
                  ))}
                </div>
              </div>
            )}

            {/* Pitch Visualizer & Commentary Stream */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
              <PitchVisualizer lastBall={deliveries[deliveries.length - 1]} />

              {/* Ball by Ball Commentary Stream */}
              <div className="bg-white rounded-xl border border-gray-200 p-5 shadow-xs">
                <h3 className="text-xs font-extrabold text-gray-500 uppercase tracking-wider mb-4 pb-2 border-b border-gray-100 flex items-center justify-between">
                  <span>Ball-By-Ball Commentary</span>
                  <span className="text-gray-400 font-normal">{deliveries.length} balls bowled</span>
                </h3>

                {deliveries.length > 0 ? (
                  <div className="space-y-3.5 max-h-[460px] overflow-y-auto pr-2">
                    {deliveries.slice().reverse().map((d) => (
                      <div
                        key={d.id}
                        className="p-3 rounded-lg bg-gray-50/80 border border-gray-100 hover:border-red-200 transition-colors flex items-start gap-3 text-xs"
                      >
                        <div className="shrink-0 font-black text-gray-800 bg-white border border-gray-200 px-2 py-1 rounded text-[11px]">
                          {d.overNumber}.{d.deliveryInOver}
                        </div>
                        <div className="flex-1 space-y-1">
                          <div className="flex items-center justify-between">
                            <span className="font-extrabold text-gray-900">
                              {d.strikerName} vs {d.bowlerName}
                            </span>
                            <BallChip ball={d} size="sm" />
                          </div>
                          <p className="text-gray-600 font-medium leading-relaxed">
                            {d.description || 'Delivery completed.'}
                          </p>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="p-8 text-center text-xs text-gray-400">
                    No ball events recorded yet for this match.
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Full Scorecard */}
        {activeTab === 'scorecard' && (
          <div className="mt-6 space-y-8">
            {scorecards.length > 0 ? (
              scorecards.map((sc, idx) => (
                <div key={sc.innings.id || idx} className="space-y-4">
                  <div className="bg-[#E53945] text-white px-5 py-3 rounded-xl font-extrabold text-sm flex items-center justify-between shadow-xs">
                    <span>
                      Innings {sc.innings.inningsNumber}: {sc.innings.battingTeam.name}
                    </span>
                    <span className="text-lg">
                      {sc.innings.totalRuns}/{sc.innings.wickets}{' '}
                      <span className="text-xs font-semibold text-white/80">({sc.innings.overs} ov)</span>
                    </span>
                  </div>

                  <div className="bg-white rounded-xl border border-gray-200 shadow-xs overflow-hidden">
                    <BattingTable teamName={sc.innings.battingTeam.name} battingScores={sc.battingScores} />
                  </div>

                  <div className="bg-white rounded-xl border border-gray-200 shadow-xs overflow-hidden">
                    <BowlingTable teamName={sc.innings.bowlingTeam.name} bowlingFigures={sc.bowlingFigures} />
                  </div>
                </div>
              ))
            ) : (
              <div className="bg-white rounded-xl border border-gray-200 p-12 text-center text-gray-400 text-sm">
                Scorecard data is not available yet.
              </div>
            )}
          </div>
        )}

        {/* Tab 3: Scorer Console (if role permitted) */}
        {activeTab === 'scorer' && isNumericId && currentInnings && currentScorecard && (
          <div className="mt-6">
            <ScorerConsole
              innings={currentInnings}
              scorecard={currentScorecard}
              battingPlayers={homePlayers}
              bowlingPlayers={awayPlayers}
              onScoreUpdated={(newSc) => {
                setScorecards((prev) => prev.map((s) => (s.innings.id === newSc.innings.id ? newSc : s)));
              }}
            />
          </div>
        )}
      </PageContainer>
    </div>
  );
};
