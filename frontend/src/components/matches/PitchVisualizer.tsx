import React from 'react';
import { BallEvent, Innings } from '../../types';
import { BallChip } from '../ui/BallChip';

interface PitchVisualizerProps {
  innings?: Innings;
  lastBall?: BallEvent;
  recentDeliveries?: BallEvent[];
}

export const PitchVisualizer: React.FC<PitchVisualizerProps> = ({
  innings,
  lastBall,
  recentDeliveries = []
}) => {
  return (
    <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-5 overflow-hidden">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-bold text-cricket-navy uppercase tracking-wider flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
          Live Field & Pitch View
        </h3>
        {innings && (
          <span className="text-xs font-semibold text-cricket-muted">
            CRR: <strong className="text-cricket-navy">{innings.currentRunRate?.toFixed(2) || '0.00'}</strong>
          </span>
        )}
      </div>

      <div className="relative w-full aspect-square max-w-[280px] mx-auto rounded-full bg-gradient-to-b from-emerald-600 to-green-700 border-4 border-white shadow-inner flex items-center justify-center overflow-hidden">
        {/* Boundary Rope */}
        <div className="absolute inset-2 rounded-full border border-dashed border-white/40 pointer-events-none" />

        {/* 30-yard Inner Circle */}
        <div className="absolute w-[68%] h-[68%] rounded-full border border-white/30 pointer-events-none" />

        {/* Pitch / 22-Yard Strip */}
        <div className="relative w-12 h-28 bg-[#DEB887] rounded-sm shadow-md border border-amber-900/20 flex flex-col justify-between py-1 items-center">
          {/* Non-striker Bowling Stumps */}
          <div className="flex gap-0.5">
            <span className="w-1 h-1.5 bg-amber-950 rounded-xs"></span>
            <span className="w-1 h-1.5 bg-amber-950 rounded-xs"></span>
            <span className="w-1 h-1.5 bg-amber-950 rounded-xs"></span>
          </div>

          {/* Crease markings */}
          <div className="w-full border-t border-white/60"></div>

          {/* Ball landing spot */}
          <div className="w-3.5 h-3.5 rounded-full bg-cricket-red border-2 border-white shadow-lg animate-ping"></div>

          {/* Striker Batting Stumps */}
          <div className="flex gap-0.5">
            <span className="w-1 h-1.5 bg-amber-950 rounded-xs"></span>
            <span className="w-1 h-1.5 bg-amber-950 rounded-xs"></span>
            <span className="w-1 h-1.5 bg-amber-950 rounded-xs"></span>
          </div>
        </div>

        {/* Shot Trajectory Indicator (if boundary or run) */}
        {lastBall && lastBall.batterRuns >= 4 && (
          <svg className="absolute inset-0 w-full h-full pointer-events-none">
            <defs>
              <linearGradient id="shotGrad" x1="50%" y1="65%" x2="85%" y2="20%">
                <stop offset="0%" stopColor="#FFFFFF" stopOpacity="0.2" />
                <stop offset="100%" stopColor="#E53945" stopOpacity="1" />
              </linearGradient>
            </defs>
            <path
              d="M 140 170 Q 180 120 220 60"
              fill="none"
              stroke="url(#shotGrad)"
              strokeWidth="3.5"
              strokeDasharray="6 3"
            />
          </svg>
        )}
      </div>

      {/* Last Delivery Action Card */}
      <div className="mt-4 p-3 bg-gray-50 rounded-xl border border-gray-100 text-center">
        {lastBall ? (
          <div>
            <div className="text-xs text-cricket-muted mb-1">
              Over {lastBall.overNumber}.{lastBall.deliveryInOver} • {lastBall.bowlerName} to {lastBall.strikerName}
            </div>
            <div className="text-sm font-extrabold text-cricket-navy">
              {lastBall.isWicket ? (
                <span className="text-cricket-red">WICKET! ({lastBall.wicketType})</span>
              ) : lastBall.batterRuns === 6 ? (
                <span className="text-cricket-navy">IT'S A SIX!</span>
              ) : lastBall.batterRuns === 4 ? (
                <span className="text-blue-600">FOUR RUNS!</span>
              ) : (
                <span>{lastBall.batterRuns + lastBall.totalExtras} Runs</span>
              )}
            </div>
            {lastBall.description && (
              <div className="text-xs text-cricket-muted mt-0.5 italic">{lastBall.description}</div>
            )}
          </div>
        ) : (
          <div className="text-xs text-cricket-muted">Awaiting live deliveries for this over</div>
        )}
      </div>

      {/* Recent Deliveries Carousel */}
      {recentDeliveries.length > 0 && (
        <div className="mt-4 pt-3 border-t border-cricket-border">
          <div className="text-[11px] font-bold text-cricket-muted uppercase tracking-wider mb-2 text-left">
            This Over:
          </div>
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
            {recentDeliveries.slice(-8).map((ball) => (
              <BallChip key={ball.id} ball={ball} size="sm" />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
