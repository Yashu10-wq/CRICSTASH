import React from 'react';
import { BattingScore } from '../../types';

interface BattingTableProps {
  battingScores: BattingScore[];
  teamName: string;
}

export const BattingTable: React.FC<BattingTableProps> = ({ battingScores, teamName }) => {
  if (!battingScores || battingScores.length === 0) {
    return (
      <div className="p-8 text-center text-sm text-cricket-muted bg-white rounded-xl border border-cricket-border">
        No batting figures recorded yet for {teamName}.
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl border border-cricket-border shadow-card overflow-hidden">
      <div className="px-5 py-3.5 bg-gray-50/80 border-b border-cricket-border flex items-center justify-between">
        <h3 className="text-sm font-bold text-cricket-navy uppercase tracking-wider">
          {teamName} Batting
        </h3>
        <span className="text-xs text-cricket-muted font-medium">Innings Scorecard</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse text-sm">
          <thead>
            <tr className="border-b border-cricket-border bg-gray-50/50 text-xs font-bold text-cricket-muted uppercase tracking-wider">
              <th className="py-3 px-4 sm:px-6">Batter</th>
              <th className="py-3 px-4">Dismissal</th>
              <th className="py-3 px-3 text-right">R</th>
              <th className="py-3 px-3 text-right">B</th>
              <th className="py-3 px-3 text-right">4s</th>
              <th className="py-3 px-3 text-right">6s</th>
              <th className="py-3 px-4 sm:px-6 text-right">SR</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {battingScores.map((score) => {
              const isActive = score.atCrease && !score.isOut;
              const isStriker = score.onStrike;

              return (
                <tr
                  key={score.id}
                  className={`hover:bg-gray-50/60 transition-colors ${
                    isActive ? 'bg-red-50/20 font-semibold' : ''
                  }`}
                >
                  <td className="py-3.5 px-4 sm:px-6">
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-cricket-navy">
                        {score.playerName}
                        {isStriker ? ' *' : ''}
                      </span>
                      {isActive && (
                        <span className="px-1.5 py-0.5 rounded text-[10px] font-extrabold bg-red-100 text-cricket-red uppercase tracking-wider">
                          Batting
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="py-3.5 px-4 text-xs text-cricket-muted">
                    {score.isOut ? (
                      <span className="text-gray-700">
                        {score.dismissalType.replace('_', ' ')}
                        {score.bowlerName ? ` b ${score.bowlerName}` : ''}
                        {score.fielderName ? ` c ${score.fielderName}` : ''}
                      </span>
                    ) : (
                      <span className="text-emerald-700 font-semibold">not out</span>
                    )}
                  </td>
                  <td className="py-3.5 px-3 text-right font-extrabold text-cricket-navy">
                    {score.runs}
                  </td>
                  <td className="py-3.5 px-3 text-right text-cricket-muted">
                    {score.ballsFaced}
                  </td>
                  <td className="py-3.5 px-3 text-right font-medium text-cricket-muted">
                    {score.fours}
                  </td>
                  <td className="py-3.5 px-3 text-right font-medium text-cricket-muted">
                    {score.sixes}
                  </td>
                  <td className="py-3.5 px-4 sm:px-6 text-right font-semibold text-cricket-navy">
                    {score.strikeRate?.toFixed(1) || '0.0'}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};
