import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Trophy, ArrowRight, ShieldCheck } from 'lucide-react';
import { Team } from '../types';
import { teamService } from '../services/teamService';
import { PageContainer } from '../components/layout/PageContainer';
import { TeamBadge } from '../components/ui/TeamBadge';
import { LoadingSkeleton, EmptyState, ErrorState } from '../components/ui/States';

export const TeamsPage: React.FC = () => {
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');

  const fetchTeams = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await teamService.getTeams({ search: search.trim() || undefined, size: 50 });
      setTeams(res.content || []);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to retrieve teams list');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTeams();
  }, [search]);

  return (
    <PageContainer className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-cricket-border">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-cricket-navy tracking-tight">
            Teams & Franchises
          </h1>
          <p className="text-sm text-cricket-muted mt-1">
            Explore national squads, domestic clubs, player rosters, and tournament records.
          </p>
        </div>

        {/* Search */}
        <div className="relative w-full sm:w-72">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-cricket-muted" />
          <input
            type="text"
            placeholder="Search teams by name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-white border border-cricket-border rounded-xl pl-9 pr-3 py-2 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
          />
        </div>
      </div>

      {loading ? (
        <LoadingSkeleton rows={3} />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchTeams} />
      ) : teams.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {teams.map((team) => (
            <Link
              key={team.id}
              to={`/teams/${team.id}`}
              className="p-5 bg-white rounded-2xl border border-cricket-border shadow-card hover:shadow-card-hover hover:border-gray-300 transition-all flex items-center justify-between group"
            >
              <TeamBadge team={team} size="lg" />
              <div className="flex items-center gap-2 text-xs font-bold text-cricket-muted group-hover:text-cricket-red transition-colors">
                <span>Roster</span>
                <ArrowRight className="w-4 h-4" />
              </div>
            </Link>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No teams registered"
          message="Teams created in the system or Neon DB will appear here."
        />
      )}
    </PageContainer>
  );
};
