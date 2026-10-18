import React, { useState } from 'react';
import { RotateCcw, AlertTriangle, Send, CheckCircle2 } from 'lucide-react';
import { Innings, Scorecard, Player } from '../../types';
import { inningsService, RecordDeliveryPayload } from '../../services/inningsService';

interface ScorerConsoleProps {
  innings: Innings;
  scorecard: Scorecard;
  battingPlayers: Player[];
  bowlingPlayers: Player[];
  onScoreUpdated: (newScorecard: Scorecard) => void;
}

export const ScorerConsole: React.FC<ScorerConsoleProps> = ({
  innings,
  scorecard,
  battingPlayers,
  bowlingPlayers,
  onScoreUpdated
}) => {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Active striker, non-striker, bowler from scorecard
  const activeStriker = scorecard.battingScores.find((b) => b.onStrike && b.atCrease && !b.isOut);
  const activeNonStriker = scorecard.battingScores.find((b) => !b.onStrike && b.atCrease && !b.isOut);
  // Last bowler
  const lastFigure = scorecard.bowlingFigures[scorecard.bowlingFigures.length - 1];

  const [selectedStrikerId, setSelectedStrikerId] = useState<number>(activeStriker?.playerId || 0);
  const [selectedNonStrikerId, setSelectedNonStrikerId] = useState<number>(activeNonStriker?.playerId || 0);
  const [selectedBowlerId, setSelectedBowlerId] = useState<number>(lastFigure?.playerId || 0);

  // Wicket modal state
  const [isWicketModalOpen, setIsWicketModalOpen] = useState(false);
  const [wicketType, setWicketType] = useState<'BOWLED' | 'CAUGHT' | 'LBW' | 'STUMPED' | 'RUN_OUT' | 'HIT_WICKET'>('BOWLED');
  const [dismissedId, setDismissedId] = useState<number>(activeStriker?.playerId || 0);
  const [assistedById, setAssistedById] = useState<number | undefined>(undefined);
  const [incomingBatterId, setIncomingBatterId] = useState<number | undefined>(undefined);

  // Quick delivery submission
  const handleQuickBall = async (
    batterRuns: number,
    extraType?: 'WIDE' | 'NO_BALL' | 'BYE' | 'LEG_BYE',
    extraRuns = 0
  ) => {
    const sId = selectedStrikerId || activeStriker?.playerId;
    const nsId = selectedNonStrikerId || activeNonStriker?.playerId;
    const bId = selectedBowlerId || lastFigure?.playerId;

    if (!sId || !nsId || !bId) {
      setError('Please select striker, non-striker, and bowler.');
      return;
    }

    try {
      setSubmitting(true);
      setError(null);
      setSuccess(null);

      const payload: RecordDeliveryPayload = {
        strikerId: sId,
        nonStrikerId: nsId,
        bowlerId: bId,
        batterRuns,
        extraType,
        extraRuns
      };

      const updated = await inningsService.recordDelivery(innings.id, payload);
      onScoreUpdated(updated);
      setSuccess(`Ball recorded: ${batterRuns + (extraType ? (extraRuns || 1) : 0)} runs`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to record delivery');
    } finally {
      setSubmitting(false);
    }
  };

  // Submit wicket
  const handleWicketSubmit = async () => {
    const sId = selectedStrikerId || activeStriker?.playerId;
    const nsId = selectedNonStrikerId || activeNonStriker?.playerId;
    const bId = selectedBowlerId || lastFigure?.playerId;

    if (!sId || !nsId || !bId) {
      setError('Please select striker, non-striker, and bowler.');
      return;
    }

    try {
      setSubmitting(true);
      setError(null);

      const payload: RecordDeliveryPayload = {
        strikerId: sId,
        nonStrikerId: nsId,
        bowlerId: bId,
        batterRuns: 0,
        isWicket: true,
        wicketType,
        dismissedPlayerId: dismissedId || sId,
        assistedByPlayerId: assistedById,
        incomingBatterId
      };

      const updated = await inningsService.recordDelivery(innings.id, payload);
      onScoreUpdated(updated);
      setIsWicketModalOpen(false);
      setSuccess(`WICKET recorded: ${wicketType}`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to record wicket');
    } finally {
      setSubmitting(false);
    }
  };

  // Undo last ball
  const handleUndo = async () => {
    if (!confirm('Are you sure you want to rollback the last delivery recorded?')) return;

    try {
      setSubmitting(true);
      setError(null);
      const updated = await inningsService.undoLastDelivery(innings.id);
      onScoreUpdated(updated);
      setSuccess('Last delivery rolled back successfully.');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to undo delivery');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-5 space-y-6">
      {/* Header with Undo */}
      <div className="flex items-center justify-between pb-4 border-b border-cricket-border">
        <div>
          <h3 className="text-base font-extrabold text-cricket-navy flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-cricket-red"></span>
            Live Scoring Console
          </h3>
          <p className="text-xs text-cricket-muted mt-0.5">
            Innings #{innings.inningsNumber} • {innings.totalRuns}/{innings.wickets} ({innings.overs} ov)
          </p>
        </div>
        <button
          onClick={handleUndo}
          disabled={submitting}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-gray-300 bg-white hover:bg-gray-50 text-xs font-bold text-cricket-muted hover:text-cricket-red transition-colors disabled:opacity-50"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          Undo Last Ball
        </button>
      </div>

      {/* Alerts */}
      {error && (
        <div className="p-3 bg-red-50 text-red-700 text-xs rounded-xl border border-red-200 flex items-center gap-2">
          <AlertTriangle className="w-4 h-4 shrink-0 text-cricket-red" />
          <span>{error}</span>
        </div>
      )}
      {success && (
        <div className="p-3 bg-green-50 text-green-700 text-xs rounded-xl border border-green-200 flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
          <span>{success}</span>
        </div>
      )}

      {/* Active Crease Selectors */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 p-3.5 bg-gray-50 rounded-xl border border-gray-100 text-xs">
        <div>
          <label className="block font-bold text-cricket-navy mb-1">Striker (*)</label>
          <select
            value={selectedStrikerId || activeStriker?.playerId || ''}
            onChange={(e) => setSelectedStrikerId(Number(e.target.value))}
            className="w-full bg-white border border-gray-200 rounded-lg p-2 font-medium text-cricket-text"
          >
            {battingPlayers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.fullName} {activeStriker?.playerId === p.id ? '(On Strike)' : ''}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block font-bold text-cricket-navy mb-1">Non-Striker</label>
          <select
            value={selectedNonStrikerId || activeNonStriker?.playerId || ''}
            onChange={(e) => setSelectedNonStrikerId(Number(e.target.value))}
            className="w-full bg-white border border-gray-200 rounded-lg p-2 font-medium text-cricket-text"
          >
            {battingPlayers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.fullName} {activeNonStriker?.playerId === p.id ? '(Non-Striker)' : ''}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block font-bold text-cricket-navy mb-1">Bowler</label>
          <select
            value={selectedBowlerId || lastFigure?.playerId || ''}
            onChange={(e) => setSelectedBowlerId(Number(e.target.value))}
            className="w-full bg-white border border-gray-200 rounded-lg p-2 font-medium text-cricket-text"
          >
            {bowlingPlayers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.fullName}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Quick Scoring Buttons */}
      <div>
        <div className="text-xs font-bold text-cricket-muted uppercase tracking-wider mb-2.5">
          Regular Batter Runs
        </div>
        <div className="grid grid-cols-4 sm:grid-cols-6 gap-2">
          {[
            { label: '0 • Dot', runs: 0, color: 'bg-gray-100 hover:bg-gray-200 text-cricket-text' },
            { label: '1 Run', runs: 1, color: 'bg-white border border-gray-300 hover:bg-gray-50 text-cricket-navy' },
            { label: '2 Runs', runs: 2, color: 'bg-white border border-gray-300 hover:bg-gray-50 text-cricket-navy' },
            { label: '3 Runs', runs: 3, color: 'bg-white border border-gray-300 hover:bg-gray-50 text-cricket-navy' },
            { label: '4 FOUR', runs: 4, color: 'bg-blue-600 hover:bg-blue-700 text-white font-black' },
            { label: '6 SIX', runs: 6, color: 'bg-cricket-navy hover:bg-cricket-navy-dark text-white font-black' }
          ].map((btn) => (
            <button
              key={btn.runs}
              onClick={() => handleQuickBall(btn.runs)}
              disabled={submitting}
              className={`py-3 rounded-xl font-bold text-sm shadow-sm transition-transform active:scale-95 disabled:opacity-50 ${btn.color}`}
            >
              {btn.label}
            </button>
          ))}
        </div>
      </div>

      {/* Extras & Wicket Controls */}
      <div>
        <div className="text-xs font-bold text-cricket-muted uppercase tracking-wider mb-2.5">
          Extras & Dismissals
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-5 gap-2">
          <button
            onClick={() => handleQuickBall(0, 'WIDE', 1)}
            disabled={submitting}
            className="py-2.5 rounded-xl border border-amber-300 bg-amber-50 hover:bg-amber-100 text-amber-900 font-bold text-xs shadow-sm transition-all"
          >
            Wide (+1)
          </button>
          <button
            onClick={() => handleQuickBall(0, 'NO_BALL', 1)}
            disabled={submitting}
            className="py-2.5 rounded-xl border border-amber-300 bg-amber-50 hover:bg-amber-100 text-amber-900 font-bold text-xs shadow-sm transition-all"
          >
            No Ball (+1)
          </button>
          <button
            onClick={() => handleQuickBall(1, 'BYE', 0)}
            disabled={submitting}
            className="py-2.5 rounded-xl border border-gray-200 bg-gray-50 hover:bg-gray-100 text-cricket-text font-bold text-xs shadow-sm transition-all"
          >
            1 Bye
          </button>
          <button
            onClick={() => handleQuickBall(1, 'LEG_BYE', 0)}
            disabled={submitting}
            className="py-2.5 rounded-xl border border-gray-200 bg-gray-50 hover:bg-gray-100 text-cricket-text font-bold text-xs shadow-sm transition-all"
          >
            1 Leg Bye
          </button>
          <button
            onClick={() => setIsWicketModalOpen(true)}
            disabled={submitting}
            className="py-2.5 rounded-xl bg-cricket-red hover:bg-cricket-red-dark text-white font-extrabold text-xs shadow-sm transition-all col-span-2 sm:col-span-1"
          >
            ⚡ WICKET
          </button>
        </div>
      </div>

      {/* Wicket Modal */}
      {isWicketModalOpen && (
        <div className="p-4 bg-red-50/70 border border-red-200 rounded-xl space-y-3">
          <div className="flex items-center justify-between">
            <h4 className="text-sm font-bold text-cricket-red">Record Wicket Event</h4>
            <button
              onClick={() => setIsWicketModalOpen(false)}
              className="text-xs text-cricket-muted hover:text-cricket-navy"
            >
              Cancel
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
            <div>
              <label className="block font-semibold text-cricket-navy mb-1">Dismissal Type</label>
              <select
                value={wicketType}
                onChange={(e) => setWicketType(e.target.value as any)}
                className="w-full bg-white border border-gray-300 rounded-lg p-2"
              >
                <option value="BOWLED">Bowled</option>
                <option value="CAUGHT">Caught</option>
                <option value="LBW">LBW</option>
                <option value="STUMPED">Stumped</option>
                <option value="RUN_OUT">Run Out</option>
                <option value="HIT_WICKET">Hit Wicket</option>
              </select>
            </div>

            <div>
              <label className="block font-semibold text-cricket-navy mb-1">Dismissed Batter</label>
              <select
                value={dismissedId}
                onChange={(e) => setDismissedId(Number(e.target.value))}
                className="w-full bg-white border border-gray-300 rounded-lg p-2"
              >
                {battingPlayers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.fullName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block font-semibold text-cricket-navy mb-1">Catcher / Fielder (optional)</label>
              <select
                value={assistedById || ''}
                onChange={(e) => setAssistedById(e.target.value ? Number(e.target.value) : undefined)}
                className="w-full bg-white border border-gray-300 rounded-lg p-2"
              >
                <option value="">None / Not Applicable</option>
                {bowlingPlayers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.fullName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block font-semibold text-cricket-navy mb-1">Incoming Batter (optional)</label>
              <select
                value={incomingBatterId || ''}
                onChange={(e) => setIncomingBatterId(e.target.value ? Number(e.target.value) : undefined)}
                className="w-full bg-white border border-gray-300 rounded-lg p-2"
              >
                <option value="">Next in order</option>
                {battingPlayers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.fullName}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <button
            onClick={handleWicketSubmit}
            disabled={submitting}
            className="w-full py-2.5 rounded-lg bg-cricket-red hover:bg-cricket-red-dark text-white font-bold text-xs shadow-sm transition-colors flex items-center justify-center gap-1.5"
          >
            <Send className="w-3.5 h-3.5" />
            Confirm Wicket Delivery
          </button>
        </div>
      )}
    </div>
  );
};
