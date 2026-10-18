import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, User, Shield, Trophy } from 'lucide-react';
import { Team, Player, TeamStatistics } from '../types';
import { teamService } from '../services/teamService';
import { statisticsService } from '../services/statisticsService';
import { PageContainer } from '../components/layout/PageContainer';
import { TeamBadge } from '../components/ui/TeamBadge';
import { LoadingSkeleton, ErrorState } from '../components/ui/States';

export const TeamDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const teamId = Number(id);

  const [team, setTeam] = useState<Team | null>(null);
  const [players, setPlayers] = useState<Player[]>([]);
  const [stats, setStats] = useState<TeamStatistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!teamId) return;

    const loadData = async () => {
      try {
        setLoading(true);
        setError(null);
        const [teamData, playerData, statsData] = await Promise.all([
          teamService.getTeamById(teamId),
          teamService.getTeamPlayers(teamId).catch(() => [] as Player[]),
          statisticsService.getTeamStatistics(teamId).catch(() => null)
        ]);
        setTeam(teamData);
        setPlayers(playerData);
        setStats(statsData);
      } catch (err: unknown) {
        setError(err instanceof Error ? err.message : 'Failed to retrieve team profile');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [teamId]);

  if (loading) {
    return (
      <PageContainer>
        <LoadingSkeleton rows={3} />
      </PageContainer>
    );
  }

  if (error || !team) {
    return (
      <PageContainer>
        <ErrorState message={error || 'Team not found'} />
      </PageContainer>
    );
  }

  return (
    <PageContainer className="space-y-8">
      <Link to="/teams" className="inline-flex items-center gap-1.5 text-xs font-bold text-cricket-muted hover:text-cricket-navy">
        <ArrowLeft className="w-4 h-4" />
        Back to Teams
      </Link>

      {/* Team Profile Header */}
      <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-6 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div className="flex items-center gap-5">
          <TeamBadge team={team} size="xl" showFullName={false} />
          <div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-cricket-navy">{team.name}</h1>
            <div className="flex items-center gap-2 text-xs text-cricket-muted mt-1">
              <span className="font-bold text-cricket-red uppercase tracking-wider">{team.shortName}</span>
              {team.country && <span>• {team.country}</span>}
              <span>• {players.length} Registered Players</span>
            </div>
          </div>
        </div>

        {/* Win/Loss Record */}
        {stats && (
          <div className="flex items-center gap-4 bg-gray-50 p-4 rounded-xl border border-gray-100 text-center">
            <div>
              <div className="text-xs text-cricket-muted font-bold">Played</div>
              <div className="text-lg font-black text-cricket-navy">{stats.matchesPlayed}</div>
            </div>
            <div className="w-px h-8 bg-gray-200"></div>
            <div>
              <div className="text-xs text-cricket-muted font-bold">Won</div>
              <div className="text-lg font-black text-emerald-600">{stats.matchesWon}</div>
            </div>
            <div className="w-px h-8 bg-gray-200"></div>
            <div>
              <div className="text-xs text-cricket-muted font-bold">Lost</div>
              <div className="text-lg font-black text-cricket-red">{stats.matchesLost}</div>
            </div>
            <div className="w-px h-8 bg-gray-200"></div>
            <div>
              <div className="text-xs text-cricket-muted font-bold">Win %</div>
              <div className="text-lg font-black text-cricket-blue">{stats.winPercentage}%</div>
            </div>
          </div>
        )}
      </div>

      {/* Squad Roster */}
      <section className="space-y-4">
        <h2 className="text-lg font-extrabold text-cricket-navy flex items-center gap-2">
          <User className="w-5 h-5 text-cricket-navy" />
          Playing Squad & Roster
        </h2>

        {players.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {players.map((player) => (
              <Link
                key={player.id}
                to={`/players/${player.id}`}
                className="p-4 bg-white rounded-xl border border-cricket-border shadow-xs hover:shadow-card hover:border-gray-300 transition-all flex items-center justify-between"
              >
                <div>
                  <div className="font-bold text-sm text-cricket-navy">{player.fullName}</div>
                  <div className="text-xs text-cricket-muted capitalize mt-0.5">
                    {player.primaryRole.toLowerCase().replace('_', ' ')}
                  </div>
                </div>
                <span className="text-xs font-semibold text-cricket-blue hover:underline">
                  View Profile →
                </span>
              </Link>
            ))}
          </div>
        ) : (
          <div className="p-8 text-center bg-white rounded-xl border border-cricket-border text-xs text-cricket-muted">
            No players currently assigned to this squad.
          </div>
        )}
      </section>
    </PageContainer>
  );
};
