import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight, Calendar, MapPin, Radio, Shield } from 'lucide-react';
import { Match } from '../../types';
import { TeamBadge } from '../ui/TeamBadge';
import { StatusBadge } from '../ui/StatusBadge';
import { useAuth } from '../../context/AuthContext';

interface MatchCardProps {
  match: Match;
  featured?: boolean;
}

export const MatchCard: React.FC<MatchCardProps> = ({ match, featured = false }) => {
  const { isScorer } = useAuth();
  const isLive = match.status === 'LIVE' || match.status === 'INNINGS_BREAK';

  const formatStartTime = (dateStr: string) => {
    try {
      const date = new Date(dateStr);
      return date.toLocaleDateString(undefined, {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return dateStr;
    }
  };

  return (
    <div
      className={`bg-white rounded-2xl border transition-all duration-200 overflow-hidden ${
        featured
          ? 'border-red-200 shadow-card-hover ring-1 ring-red-100'
          : 'border-cricket-border shadow-card hover:shadow-card-hover hover:border-gray-300'
      }`}
    >
      {/* Card Header */}
      <div className="px-5 py-3.5 bg-gray-50/70 border-b border-cricket-border flex items-center justify-between">
        <div className="flex items-center gap-2 text-xs font-semibold text-cricket-muted">
          <span className="px-2 py-0.5 rounded bg-white border border-gray-200 font-bold text-cricket-navy">
            {match.format}
          </span>
          {match.competition && (
            <span className="truncate max-w-[150px] sm:max-w-xs">{match.competition}</span>
          )}
        </div>
        <StatusBadge status={match.status} size="sm" />
      </div>

      {/* Main Match Body */}
      <Link to={`/matches/${match.id}`} className="block p-5 hover:bg-gray-50/40 transition-colors">
        <div className="space-y-4">
          {/* Home Team */}
          <div className="flex items-center justify-between">
            <TeamBadge team={match.homeTeam} size="md" />
            <div className="text-right">
              {match.winnerTeam?.id === match.homeTeam.id && (
                <span className="text-[10px] font-bold text-emerald-600 bg-emerald-50 px-1.5 py-0.5 rounded mr-1.5">
                  WON
                </span>
              )}
            </div>
          </div>

          {/* Away Team */}
          <div className="flex items-center justify-between">
            <TeamBadge team={match.awayTeam} size="md" />
            <div className="text-right">
              {match.winnerTeam?.id === match.awayTeam.id && (
                <span className="text-[10px] font-bold text-emerald-600 bg-emerald-50 px-1.5 py-0.5 rounded mr-1.5">
                  WON
                </span>
              )}
            </div>
          </div>
        </div>

        {/* Result or Status Summary */}
        <div className="mt-4 pt-3 border-t border-gray-100 flex items-center justify-between">
          <div className="text-xs font-medium text-cricket-muted truncate pr-2">
            {match.resultSummary ? (
              <span className="font-semibold text-cricket-navy">{match.resultSummary}</span>
            ) : match.status === 'LIVE' ? (
              <span className="text-cricket-red font-semibold flex items-center gap-1.5">
                <Radio className="w-3.5 h-3.5 animate-pulse" />
                Live in progress
              </span>
            ) : (
              <div className="flex items-center gap-3 text-cricket-muted">
                {match.venue && (
                  <span className="flex items-center gap-1 truncate max-w-[160px]">
                    <MapPin className="w-3.5 h-3.5" />
                    {match.venue}
                  </span>
                )}
                <span className="flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5" />
                  {formatStartTime(match.startTime)}
                </span>
              </div>
            )}
          </div>
          <ChevronRight className="w-4 h-4 text-cricket-muted shrink-0" />
        </div>
      </Link>

      {/* Scorer Quick Action */}
      {isScorer && isLive && (
        <div className="px-5 py-2.5 bg-red-50/50 border-t border-red-100 flex items-center justify-between">
          <span className="text-xs font-semibold text-cricket-red">Scorer Console</span>
          <Link
            to={`/scorer?matchId=${match.id}`}
            className="inline-flex items-center gap-1 text-xs font-bold text-cricket-red hover:underline"
          >
            <Shield className="w-3.5 h-3.5" />
            Score Match →
          </Link>
        </div>
      )}
    </div>
  );
};
