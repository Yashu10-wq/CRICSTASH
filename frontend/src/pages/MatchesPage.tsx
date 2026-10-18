import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { Radio, Trophy, Calendar, CheckCircle2, Plus, Filter, Search } from 'lucide-react';
import { Match, MatchFormat, MatchStatus } from '../types';
import { matchService } from '../services/matchService';
import { MatchCard } from '../components/matches/MatchCard';
import { PageContainer } from '../components/layout/PageContainer';
import { LoadingSkeleton, EmptyState, ErrorState } from '../components/ui/States';
import { useAuth } from '../context/AuthContext';

export const MatchesPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const statusParam = searchParams.get('status') as MatchStatus | null;

  const [matches, setMatches] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [activeTab, setActiveTab] = useState<string>(statusParam || 'ALL');
  const [formatFilter, setFormatFilter] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');

  const { isAdmin } = useAuth();

  useEffect(() => {
    if (statusParam) {
      setActiveTab(statusParam);
    }
  }, [statusParam]);

  const fetchMatches = async () => {
    try {
      setLoading(true);
      setError(null);

      const statusQuery = activeTab !== 'ALL' ? (activeTab as MatchStatus) : undefined;
      const res = await matchService.getAllMatches({ status: statusQuery, size: 50 });
      setMatches(res.content || []);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to retrieve match listings');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMatches();
  }, [activeTab]);

  const handleTabChange = (tab: string) => {
    setActiveTab(tab);
    if (tab === 'ALL') {
      searchParams.delete('status');
    } else {
      searchParams.set('status', tab);
    }
    setSearchParams(searchParams);
  };

  const filteredMatches = matches.filter((m) => {
    if (formatFilter !== 'ALL' && m.format !== formatFilter) {
      return false;
    }
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      const matchTitle = m.title.toLowerCase();
      const home = m.homeTeam.name.toLowerCase();
      const away = m.awayTeam.name.toLowerCase();
      const comp = m.competition?.toLowerCase() || '';
      return matchTitle.includes(q) || home.includes(q) || away.includes(q) || comp.includes(q);
    }
    return true;
  });

  return (
    <PageContainer className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-cricket-border">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-cricket-navy tracking-tight">
            Cricket Matches & Centre
          </h1>
          <p className="text-sm text-cricket-muted mt-1">
            Real-time scorecards, upcoming international schedules, and historical match archives.
          </p>
        </div>

        {isAdmin && (
          <Link
            to="/matches/new"
            className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-cricket-navy hover:bg-cricket-navy-dark text-white font-bold text-xs shadow-sm transition-colors self-start sm:self-auto"
          >
            <Plus className="w-4 h-4" />
            Schedule New Match
          </Link>
        )}
      </div>

      {/* Tabs & Filters */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        {/* Status Category Tabs */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-cricket-border rounded-xl shadow-xs overflow-x-auto">
          {[
            { id: 'ALL', label: 'All Matches', icon: Trophy },
            { id: 'LIVE', label: 'Live Now', icon: Radio, highlight: true },
            { id: 'SCHEDULED', label: 'Upcoming', icon: Calendar },
            { id: 'COMPLETED', label: 'Results', icon: CheckCircle2 }
          ].map((tab) => {
            const isCurrent = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => handleTabChange(tab.id)}
                className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs font-bold transition-all shrink-0 ${
                  isCurrent
                    ? 'bg-cricket-navy text-white shadow-sm'
                    : 'text-cricket-muted hover:text-cricket-navy hover:bg-gray-50'
                }`}
              >
                <tab.icon
                  className={`w-3.5 h-3.5 ${
                    tab.highlight && isCurrent ? 'text-red-400 animate-pulse' : ''
                  }`}
                />
                {tab.label}
              </button>
            );
          })}
        </div>

        {/* Search & Format Filter */}
        <div className="flex items-center gap-3">
          <div className="relative flex-1 sm:w-64">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-cricket-muted" />
            <input
              type="text"
              placeholder="Search team or series..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-white border border-cricket-border rounded-xl pl-9 pr-3 py-2 text-xs font-medium focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
            />
          </div>

          <div className="flex items-center gap-1.5 bg-white border border-cricket-border rounded-xl px-2.5 py-1.5">
            <Filter className="w-3.5 h-3.5 text-cricket-muted" />
            <select
              value={formatFilter}
              onChange={(e) => setFormatFilter(e.target.value)}
              className="bg-transparent text-xs font-bold text-cricket-navy focus:outline-none"
            >
              <option value="ALL">All Formats</option>
              <option value="T20">T20</option>
              <option value="ODI">ODI</option>
              <option value="TEST">Test</option>
            </select>
          </div>
        </div>
      </div>

      {/* Match Grid */}
      {loading ? (
        <LoadingSkeleton rows={4} />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchMatches} />
      ) : filteredMatches.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredMatches.map((match) => (
            <MatchCard key={match.id} match={match} />
          ))}
        </div>
      ) : (
        <EmptyState
          title="No matches found"
          message={`No matches found matching the criteria "${activeTab}".`}
          actionText="Clear Filters"
          onAction={() => {
            setActiveTab('ALL');
            setFormatFilter('ALL');
            setSearchQuery('');
          }}
        />
      )}
    </PageContainer>
  );
};
