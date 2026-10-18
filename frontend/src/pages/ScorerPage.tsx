import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { Shield, Play, Coins, AlertCircle, CheckCircle2, ChevronRight, RefreshCw } from 'lucide-react';
import { Match, Scorecard, Player, TossDecision } from '../types';
import { matchService } from '../services/matchService';
import { inningsService, StartInningsPayload } from '../services/inningsService';
import { teamService } from '../services/teamService';
import { PageContainer } from '../components/layout/PageContainer';
import { ScorerConsole } from '../components/scoring/ScorerConsole';
import { StatusBadge } from '../components/ui/StatusBadge';
import { LoadingSkeleton } from '../components/ui/States';
import { useAuth } from '../context/AuthContext';

export const ScorerPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const matchIdParam = searchParams.get('matchId');

  const { isScorer } = useAuth();

  const [matches, setMatches] = useState<Match[]>([]);
  const [selectedMatch, setSelectedMatch] = useState<Match | null>(null);
  const [scorecards, setScorecards] = useState<Scorecard[]>([]);
  const [homePlayers, setHomePlayers] = useState<Player[]>([]);
  const [awayPlayers, setAwayPlayers] = useState<Player[]>([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionSuccess, setActionSuccess] = useState<string | null>(null);

  // Toss form
  const [tossWinnerId, setTossWinnerId] = useState<number>(0);
  const [tossDecision, setTossDecision] = useState<TossDecision>('BAT');

  // Start innings form
  const [inningsNumber, setInningsNumber] = useState<number>(1);
  const [battingTeamId, setBattingTeamId] = useState<number>(0);
  const [bowlingTeamId, setBowlingTeamId] = useState<number>(0);
  const [strikerId, setStrikerId] = useState<number>(0);
  const [nonStrikerId, setNonStrikerId] = useState<number>(0);
  const [bowlerId, setBowlerId] = useState<number>(0);
  const [targetRuns, setTargetRuns] = useState<number | undefined>(undefined);

  useEffect(() => {
    loadMatches();
  }, []);

  const loadMatches = async () => {
    try {
      setLoading(true);
      const res = await matchService.getAllMatches({ size: 50 });
      setMatches(res.content || []);

      if (matchIdParam) {
        selectMatch(Number(matchIdParam));
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load matches for scorer');
    } finally {
      setLoading(false);
    }
  };

  const selectMatch = async (matchId: number) => {
    try {
      setError(null);
      setActionSuccess(null);
      const [m, sc] = await Promise.all([
        matchService.getMatchById(matchId),
        inningsService.getMatchScorecards(matchId).catch(() => [] as Scorecard[])
      ]);
      setSelectedMatch(m);
      setScorecards(sc);
      setSearchParams({ matchId: String(matchId) });

      if (m.homeTeam && m.awayTeam) {
        const [hp, ap] = await Promise.all([
          teamService.getTeamPlayers(m.homeTeam.id).catch(() => []),
          teamService.getTeamPlayers(m.awayTeam.id).catch(() => [])
        ]);
        setHomePlayers(hp);
        setAwayPlayers(ap);

        setTossWinnerId(m.homeTeam.id);
        setBattingTeamId(m.homeTeam.id);
        setBowlingTeamId(m.awayTeam.id);

        if (hp.length >= 2) {
          setStrikerId(hp[0].id);
          setNonStrikerId(hp[1].id);
        }
        if (ap.length >= 1) {
          setBowlerId(ap[0].id);
        }
      }

      if (sc.length === 1) {
        setInningsNumber(2);
        // target is Innings 1 runs + 1
        setTargetRuns(sc[0].innings.totalRuns + 1);
        setBattingTeamId(sc[0].innings.bowlingTeam.id);
        setBowlingTeamId(sc[0].innings.battingTeam.id);
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load match details');
    }
  };

  const handleRecordToss = async () => {
    if (!selectedMatch) return;
    try {
      setError(null);
      const matchNumericId = Number(selectedMatch.id);
      const updated = await matchService.recordToss(matchNumericId, {
        tossWinnerTeamId: tossWinnerId,
        tossDecision: tossDecision
      });
      setSelectedMatch(updated);
      setActionSuccess(`Toss recorded: ${updated.tossWinnerTeam?.name} chose to ${updated.tossDecision}`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to record toss');
    }
  };

  const handleStartMatch = async () => {
    if (!selectedMatch) return;
    try {
      setError(null);
      const matchNumericId = Number(selectedMatch.id);
      const updated = await matchService.startMatch(matchNumericId);
      setSelectedMatch(updated);
      setActionSuccess('Match officially started and LIVE!');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to start match');
    }
  };

  const handleStartInnings = async () => {
    if (!selectedMatch) return;
    if (!strikerId || !nonStrikerId || !bowlerId) {
      setError('Please select striker, non-striker, and opening bowler.');
      return;
    }
    if (strikerId === nonStrikerId) {
      setError('Striker and non-striker must be different players.');
      return;
    }

    try {
      setError(null);
      const matchNumericId = Number(selectedMatch.id);
      const payload: StartInningsPayload = {
        inningsNumber,
        battingTeamId,
        bowlingTeamId,
        strikerPlayerId: strikerId,
        nonStrikerPlayerId: nonStrikerId,
        openingBowlerPlayerId: bowlerId,
        targetRuns: inningsNumber > 1 ? targetRuns : undefined
      };

      await inningsService.startInnings(matchNumericId, payload);
      setActionSuccess(`Innings #${inningsNumber} started!`);
      // Reload match and scorecards
      selectMatch(matchNumericId);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to start innings');
    }
  };

  if (!isScorer) {
    return (
      <PageContainer>
        <div className="p-8 text-center bg-white rounded-2xl border border-cricket-border max-w-md mx-auto my-12">
          <Shield className="w-10 h-10 text-cricket-red mx-auto mb-3" />
          <h2 className="text-lg font-bold text-cricket-navy">Scorer Access Required</h2>
          <p className="text-xs text-cricket-muted mt-1 mb-4">
            Only users with Scorer or Administrator credentials can access the scoring console.
          </p>
          <Link to="/login" className="px-4 py-2 bg-cricket-red text-white text-xs font-bold rounded-lg">
            Sign In as Scorer
          </Link>
        </div>
      </PageContainer>
    );
  }

  const currentScorecard = scorecards.length > 0 ? scorecards[scorecards.length - 1] : undefined;
  const currentInnings = currentScorecard?.innings;

  return (
    <PageContainer className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-cricket-border">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-cricket-navy tracking-tight flex items-center gap-2">
            <Shield className="w-7 h-7 text-cricket-red" />
            Official Match Scorer Hub
          </h1>
          <p className="text-sm text-cricket-muted mt-1">
            Conduct toss, start matches, manage innings transitions, and record ball-by-ball deliveries.
          </p>
        </div>

        <button
          onClick={loadMatches}
          className="inline-flex items-center gap-1.5 px-3 py-2 rounded-xl border border-gray-200 bg-white text-xs font-semibold text-cricket-muted hover:text-cricket-navy self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Refresh Matches
        </button>
      </div>

      {/* Alerts */}
      {error && (
        <div className="p-3 bg-red-50 text-red-700 text-xs rounded-xl border border-red-200 flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-cricket-red shrink-0" />
          <span>{error}</span>
        </div>
      )}
      {actionSuccess && (
        <div className="p-3 bg-green-50 text-green-700 text-xs rounded-xl border border-green-200 flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
          <span>{actionSuccess}</span>
        </div>
      )}

      {/* Match Selector Dropdown / Grid */}
      <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-5">
        <label className="block text-xs font-bold text-cricket-navy uppercase mb-2">
          Select Match to Score
        </label>
        <select
          value={selectedMatch?.id || ''}
          onChange={(e) => selectMatch(Number(e.target.value))}
          className="w-full bg-gray-50 border border-gray-200 rounded-xl p-3 text-sm font-semibold text-cricket-navy focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
        >
          <option value="">-- Choose an active or scheduled match --</option>
          {matches.map((m) => (
            <option key={m.id} value={m.id}>
              {m.title} ({m.format}) — [{m.status}]
            </option>
          ))}
        </select>
      </div>

      {selectedMatch && (
        <div className="space-y-6">
          {/* Match Status Bar */}
          <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-5 flex flex-wrap items-center justify-between gap-4">
            <div>
              <div className="text-xs text-cricket-muted font-bold">{selectedMatch.format} Match #{selectedMatch.id}</div>
              <h2 className="text-lg font-extrabold text-cricket-navy">{selectedMatch.title}</h2>
            </div>
            <div className="flex items-center gap-3">
              <StatusBadge status={selectedMatch.status} size="md" />
              <Link
                to={`/matches/${selectedMatch.id}`}
                className="text-xs font-bold text-cricket-blue hover:underline"
              >
                Open Public Match Centre →
              </Link>
            </div>
          </div>

          {/* Lifecycle Step 1: Toss (if scheduled) */}
          {selectedMatch.status === 'SCHEDULED' && !selectedMatch.tossWinnerTeam && (
            <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-6 space-y-4">
              <h3 className="text-sm font-bold text-cricket-navy uppercase flex items-center gap-2">
                <Coins className="w-4 h-4 text-amber-500" />
                Step 1: Record Match Toss
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Toss Winner</label>
                  <select
                    value={tossWinnerId}
                    onChange={(e) => setTossWinnerId(Number(e.target.value))}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    <option value={selectedMatch.homeTeam.id}>{selectedMatch.homeTeam.name}</option>
                    <option value={selectedMatch.awayTeam.id}>{selectedMatch.awayTeam.name}</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Toss Decision</label>
                  <select
                    value={tossDecision}
                    onChange={(e) => setTossDecision(e.target.value as TossDecision)}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    <option value="BAT">Elected to BAT first</option>
                    <option value="BOWL">Elected to BOWL first</option>
                  </select>
                </div>
              </div>

              <button
                onClick={handleRecordToss}
                className="px-5 py-2.5 rounded-xl bg-cricket-navy hover:bg-cricket-navy-dark text-white text-xs font-bold shadow-sm"
              >
                Confirm Toss Result
              </button>
            </div>
          )}

          {/* Lifecycle Step 2: Start Match */}
          {selectedMatch.status === 'SCHEDULED' && selectedMatch.tossWinnerTeam && (
            <div className="bg-amber-50 border border-amber-200 rounded-2xl p-6 flex flex-col sm:flex-row items-center justify-between gap-4">
              <div>
                <h3 className="text-sm font-bold text-amber-900">Step 2: Start Match</h3>
                <p className="text-xs text-amber-700 mt-1">
                  Toss is complete ({selectedMatch.tossWinnerTeam.name} chose to {selectedMatch.tossDecision?.toLowerCase()}). Start the match to transition to LIVE status.
                </p>
              </div>
              <button
                onClick={handleStartMatch}
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-cricket-red hover:bg-cricket-red-dark text-white text-xs font-bold shadow-md shrink-0"
              >
                <Play className="w-4 h-4" />
                Start Match Now
              </button>
            </div>
          )}

          {/* Lifecycle Step 3: Initialize Innings (if no active innings) */}
          {selectedMatch.status === 'LIVE' && (!currentInnings || currentInnings.status === 'COMPLETED') && (
            <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-6 space-y-4">
              <h3 className="text-sm font-bold text-cricket-navy uppercase flex items-center gap-2">
                <Play className="w-4 h-4 text-cricket-red" />
                Step 3: Initialize Innings #{inningsNumber}
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 text-xs">
                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Batting Team</label>
                  <select
                    value={battingTeamId}
                    onChange={(e) => setBattingTeamId(Number(e.target.value))}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    <option value={selectedMatch.homeTeam.id}>{selectedMatch.homeTeam.name}</option>
                    <option value={selectedMatch.awayTeam.id}>{selectedMatch.awayTeam.name}</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Bowling Team</label>
                  <select
                    value={bowlingTeamId}
                    onChange={(e) => setBowlingTeamId(Number(e.target.value))}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    <option value={selectedMatch.awayTeam.id}>{selectedMatch.awayTeam.name}</option>
                    <option value={selectedMatch.homeTeam.id}>{selectedMatch.homeTeam.name}</option>
                  </select>
                </div>

                {inningsNumber > 1 && (
                  <div>
                    <label className="block font-bold text-cricket-navy mb-1">Target Runs</label>
                    <input
                      type="number"
                      value={targetRuns || ''}
                      onChange={(e) => setTargetRuns(Number(e.target.value))}
                      className="w-full bg-white border border-gray-300 rounded-lg p-2.5 font-bold text-cricket-navy"
                    />
                  </div>
                )}

                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Opening Striker (*)</label>
                  <select
                    value={strikerId}
                    onChange={(e) => setStrikerId(Number(e.target.value))}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    {(battingTeamId === selectedMatch.homeTeam.id ? homePlayers : awayPlayers).map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Opening Non-Striker</label>
                  <select
                    value={nonStrikerId}
                    onChange={(e) => setNonStrikerId(Number(e.target.value))}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    {(battingTeamId === selectedMatch.homeTeam.id ? homePlayers : awayPlayers).map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-cricket-navy mb-1">Opening Bowler</label>
                  <select
                    value={bowlerId}
                    onChange={(e) => setBowlerId(Number(e.target.value))}
                    className="w-full bg-white border border-gray-300 rounded-lg p-2.5"
                  >
                    {(bowlingTeamId === selectedMatch.homeTeam.id ? homePlayers : awayPlayers).map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <button
                onClick={handleStartInnings}
                className="px-6 py-2.5 rounded-xl bg-cricket-red hover:bg-cricket-red-dark text-white text-xs font-bold shadow-md"
              >
                Begin Innings #{inningsNumber}
              </button>
            </div>
          )}

          {/* Step 4: Active Scoring Console */}
          {currentInnings && currentInnings.status === 'IN_PROGRESS' && currentScorecard && (
            <ScorerConsole
              innings={currentInnings}
              scorecard={currentScorecard}
              battingPlayers={
                currentInnings.battingTeam.id === selectedMatch.homeTeam.id ? homePlayers : awayPlayers
              }
              bowlingPlayers={
                currentInnings.bowlingTeam.id === selectedMatch.homeTeam.id ? homePlayers : awayPlayers
              }
              onScoreUpdated={(updated) => {
                setScorecards((prev) =>
                  prev.map((sc) => (sc.innings.id === updated.innings.id ? updated : sc))
                );
              }}
            />
          )}
        </div>
      )}
    </PageContainer>
  );
};
