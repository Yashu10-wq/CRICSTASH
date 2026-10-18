import React from 'react';
import { BowlingFigure } from '../../types';

interface BowlingTableProps {
  bowlingFigures: BowlingFigure[];
  teamName: string;
}

export const BowlingTable: React.FC<BowlingTableProps> = ({ bowlingFigures, teamName }) => {
  if (!bowlingFigures || bowlingFigures.length === 0) {
    return (
      <div className="p-8 text-center text-sm text-cricket-muted bg-white rounded-xl border border-cricket-border">
        No bowling figures recorded yet for {teamName}.
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl border border-cricket-border shadow-card overflow-hidden">
      <div className="px-5 py-3.5 bg-gray-50/80 border-b border-cricket-border flex items-center justify-between">
        <h3 className="text-sm font-bold text-cricket-navy uppercase tracking-wider">
          {teamName} Bowling
        </h3>
        <span className="text-xs text-cricket-muted font-medium">Innings Bowling Figures</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse text-sm">
          <thead>
            <tr className="border-b border-cricket-border bg-gray-50/50 text-xs font-bold text-cricket-muted uppercase tracking-wider">
              <th className="py-3 px-4 sm:px-6">Bowler</th>
              <th className="py-3 px-3 text-right">O</th>
              <th className="py-3 px-3 text-right">M</th>
              <th className="py-3 px-3 text-right">R</th>
              <th className="py-3 px-3 text-right font-extrabold text-cricket-red">W</th>
              <th className="py-3 px-3 text-right">ECON</th>
              <th className="py-3 px-3 text-right">WD</th>
              <th className="py-3 px-4 sm:px-6 text-right">NB</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {bowlingFigures.map((figure) => {
              const isRecent = figure.legalBalls > 0 && figure.legalBalls % 6 !== 0;

              return (
                <tr
                  key={figure.id}
                  className={`hover:bg-gray-50/60 transition-colors ${
                    isRecent ? 'bg-blue-50/20' : ''
                  }`}
                >
                  <td className="py-3.5 px-4 sm:px-6">
                    <span className="font-bold text-cricket-navy">
                      {figure.playerName}
                    </span>
                  </td>
                  <td className="py-3.5 px-3 text-right font-semibold text-cricket-navy">
                    {figure.overs}
                  </td>
                  <td className="py-3.5 px-3 text-right text-cricket-muted">
                    {figure.maidens}
                  </td>
                  <td className="py-3.5 px-3 text-right text-cricket-muted">
                    {figure.runsConceded}
                  </td>
                  <td className="py-3.5 px-3 text-right font-extrabold text-cricket-red">
                    {figure.wickets}
                  </td>
                  <td className="py-3.5 px-3 text-right font-semibold text-cricket-navy">
                    {figure.economy?.toFixed(2) || '0.00'}
                  </td>
                  <td className="py-3.5 px-3 text-right text-cricket-muted text-xs">
                    {figure.wides}
                  </td>
                  <td className="py-3.5 px-4 sm:px-6 text-right text-cricket-muted text-xs">
                    {figure.noBalls}
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
