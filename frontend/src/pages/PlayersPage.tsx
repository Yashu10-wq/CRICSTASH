import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, User, Filter, ArrowRight } from 'lucide-react';
import { Player, PlayerRole } from '../types';
import { playerService } from '../services/playerService';
import { PageContainer } from '../components/layout/PageContainer';
import { LoadingSkeleton, EmptyState, ErrorState } from '../components/ui/States';

export const PlayersPage: React.FC = () => {
  const [players, setPlayers] = useState<Player[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [selectedRole, setSelectedRole] = useState<string>('ALL');

  const fetchPlayers = async () => {
    try {
      setLoading(true);
      setError(null);
      const roleParam = selectedRole !== 'ALL' ? (selectedRole as PlayerRole) : undefined;
      const res = await playerService.getPlayers({
        query: search.trim() || undefined,
        role: roleParam,
        size: 50
      });
      setPlayers(res.content || []);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to retrieve player directory');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlayers();
  }, [search, selectedRole]);

  return (
    <PageContainer className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-cricket-border">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-cricket-navy tracking-tight">
            Player Directory
          </h1>
          <p className="text-sm text-cricket-muted mt-1">
            Search career profiles, batting styles, bowling figures, and player averages.
          </p>
        </div>

        {/* Search & Role Filter */}
        <div className="flex items-center gap-3">
          <div className="relative w-full sm:w-64">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-cricket-muted" />
            <input
              type="text"
              placeholder="Search player name..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full bg-white border border-cricket-border rounded-xl pl-9 pr-3 py-2 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
            />
          </div>

          <div className="flex items-center gap-1.5 bg-white border border-cricket-border rounded-xl px-2.5 py-1.5">
            <Filter className="w-3.5 h-3.5 text-cricket-muted" />
            <select
              value={selectedRole}
              onChange={(e) => setSelectedRole(e.target.value)}
              className="bg-transparent text-xs font-bold text-cricket-navy focus:outline-none"
            >
              <option value="ALL">All Roles</option>
              <option value="BATTER">Batters</option>
              <option value="BOWLER">Bowlers</option>
              <option value="ALL_ROUNDER">All-Rounders</option>
              <option value="WICKETKEEPER">Wicketkeepers</option>
            </select>
          </div>
        </div>
      </div>

      {loading ? (
        <LoadingSkeleton rows={4} />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchPlayers} />
      ) : players.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
          {players.map((player) => (
            <Link
              key={player.id}
              to={`/players/${player.id}`}
              className="p-5 bg-white rounded-2xl border border-cricket-border shadow-card hover:shadow-card-hover hover:border-gray-300 transition-all flex flex-col justify-between group"
            >
              <div className="space-y-3">
                <div className="w-12 h-12 rounded-full bg-gradient-to-tr from-cricket-navy to-cricket-blue text-white font-black text-base flex items-center justify-center shadow-sm">
                  {player.fullName.charAt(0)}
                </div>
                <div>
                  <h3 className="font-extrabold text-sm text-cricket-navy group-hover:text-cricket-red transition-colors">
                    {player.fullName}
                  </h3>
                  <div className="text-xs text-cricket-muted font-medium mt-0.5">
                    {player.nationality || 'Cricket Player'}
                  </div>
                </div>
                <div className="flex flex-wrap gap-1.5 pt-1">
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-gray-100 text-cricket-text uppercase">
                    {player.primaryRole.replace('_', ' ')}
                  </span>
                  {player.battingStyle && (
                    <span className="px-2 py-0.5 rounded text-[10px] font-medium bg-blue-50 text-cricket-blue">
                      {player.battingStyle.replace('_', ' ')}
                    </span>
                  )}
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-gray-100 flex items-center justify-between text-xs font-semibold text-cricket-muted group-hover:text-cricket-navy">
                <span>View Career Stats</span>
                <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
              </div>
            </Link>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No players found"
          message="Players registered in the database will appear here."
        />
      )}
    </PageContainer>
  );
};
