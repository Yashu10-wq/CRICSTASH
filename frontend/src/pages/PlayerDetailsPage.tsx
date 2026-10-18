import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Trophy, Award, Flame, Zap } from 'lucide-react';
import { Player, PlayerStatistics } from '../types';
import { playerService } from '../services/playerService';
import { statisticsService } from '../services/statisticsService';
import { PageContainer } from '../components/layout/PageContainer';
import { LoadingSkeleton, ErrorState } from '../components/ui/States';

export const PlayerDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const playerId = Number(id);

  const [player, setPlayer] = useState<Player | null>(null);
  const [stats, setStats] = useState<PlayerStatistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!playerId) return;

    const loadData = async () => {
      try {
        setLoading(true);
        setError(null);
        const [playerData, statsData] = await Promise.all([
          playerService.getPlayerById(playerId),
          statisticsService.getPlayerStatistics(playerId).catch(() => null)
        ]);
        setPlayer(playerData);
        setStats(statsData);
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : 'Failed to retrieve player profile');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [playerId]);

  if (loading) {
    return (
      <PageContainer>
        <LoadingSkeleton rows={3} />
      </PageContainer>
    );
  }

  if (error || !player) {
    return (
      <PageContainer>
        <ErrorState message={error || 'Player not found'} />
      </PageContainer>
    );
  }

  return (
    <PageContainer className="space-y-8">
      <Link to="/players" className="inline-flex items-center gap-1.5 text-xs font-bold text-cricket-muted hover:text-cricket-navy">
        <ArrowLeft className="w-4 h-4" />
        Back to Player Directory
      </Link>

      {/* Player Header Banner inspired by reference image 2 */}
      <div className="bg-gradient-to-r from-cricket-red to-rose-700 text-white rounded-3xl p-6 sm:p-8 shadow-card relative overflow-hidden">
        <div className="absolute right-0 top-0 w-80 h-80 rounded-full bg-white/5 pointer-events-none transform translate-x-1/4 -translate-y-1/4"></div>

        <div className="relative flex flex-col sm:flex-row items-center sm:items-start gap-6 text-center sm:text-left">
          <div className="w-24 h-24 sm:w-28 sm:h-28 rounded-full bg-white text-cricket-navy font-black text-4xl flex items-center justify-center shadow-lg shrink-0 border-4 border-white/20">
            {player.fullName.charAt(0)}
          </div>

          <div className="space-y-2 flex-1">
            <div className="flex flex-wrap items-center justify-center sm:justify-start gap-2">
              <span className="px-3 py-1 rounded-full text-xs font-black bg-white/20 uppercase tracking-wider backdrop-blur-sm">
                {player.primaryRole.replace('_', ' ')}
              </span>
              {player.nationality && (
                <span className="text-xs text-white/80 font-medium">{player.nationality}</span>
              )}
            </div>

            <h1 className="text-3xl sm:text-4xl font-black tracking-tight">{player.fullName}</h1>

            <div className="flex flex-wrap items-center justify-center sm:justify-start gap-4 text-xs text-white/90 pt-1">
              {player.battingStyle && (
                <span>
                  <strong>Batting:</strong> {player.battingStyle.replace('_', ' ')}
                </span>
              )}
              {player.bowlingStyle && (
                <span>
                  <strong>Bowling:</strong> {player.bowlingStyle.replace('_', ' ')}
                </span>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Career Overview Statistics */}
      {stats && (
        <div className="space-y-8">
          {/* Batting Performance Card */}
          <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-6 space-y-6">
            <h2 className="text-base font-extrabold text-cricket-navy flex items-center gap-2">
              <Flame className="w-5 h-5 text-cricket-red" />
              Career Batting Record
            </h2>

            <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-8 gap-4 text-center">
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Innings</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.inningsBatted}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Runs</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.totalRuns}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">High Score</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.highestScore}</div>
              </div>
              <div className="p-3 bg-red-50/60 rounded-xl border border-red-100">
                <div className="text-[11px] font-bold text-cricket-red uppercase">Average</div>
                <div className="text-lg font-black text-cricket-red mt-1">{stats.battingAverage?.toFixed(2) || '0.00'}</div>
              </div>
              <div className="p-3 bg-blue-50/60 rounded-xl border border-blue-100">
                <div className="text-[11px] font-bold text-cricket-blue uppercase">Strike Rate</div>
                <div className="text-lg font-black text-cricket-blue mt-1">{stats.battingStrikeRate?.toFixed(2) || '0.00'}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Balls</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.ballsFaced}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Fours</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.fours}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Sixes</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.sixes}</div>
              </div>
            </div>
          </div>

          {/* Bowling Performance Card */}
          <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-6 space-y-6">
            <h2 className="text-base font-extrabold text-cricket-navy flex items-center gap-2">
              <Zap className="w-5 h-5 text-cricket-blue" />
              Career Bowling Figures
            </h2>

            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-4 text-center">
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Overs</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.oversBowled || '0.0'}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Maidens</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.maidens}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Runs Conceded</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.runsConceded}</div>
              </div>
              <div className="p-3 bg-red-50/60 rounded-xl border border-red-100">
                <div className="text-[11px] font-bold text-cricket-red uppercase">Wickets</div>
                <div className="text-lg font-black text-cricket-red mt-1">{stats.wicketsTaken}</div>
              </div>
              <div className="p-3 bg-blue-50/60 rounded-xl border border-blue-100">
                <div className="text-[11px] font-bold text-cricket-blue uppercase">Economy</div>
                <div className="text-lg font-black text-cricket-blue mt-1">{stats.bowlingEconomy?.toFixed(2) || '0.00'}</div>
              </div>
              <div className="p-3 bg-gray-50 rounded-xl">
                <div className="text-[11px] font-bold text-cricket-muted uppercase">Bowling Avg</div>
                <div className="text-lg font-black text-cricket-navy mt-1">{stats.bowlingAverage?.toFixed(2) || '0.00'}</div>
              </div>
            </div>
          </div>
        </div>
      )}
    </PageContainer>
  );
};
