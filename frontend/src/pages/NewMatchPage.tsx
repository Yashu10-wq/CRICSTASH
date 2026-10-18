import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, PlusCircle, AlertCircle } from 'lucide-react';
import { Team, MatchFormat } from '../types';
import { teamService } from '../services/teamService';
import { matchService, CreateMatchPayload } from '../services/matchService';
import { PageContainer } from '../components/layout/PageContainer';
import { useAuth } from '../context/AuthContext';

export const NewMatchPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAdmin } = useAuth();

  const [teams, setTeams] = useState<Team[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [title, setTitle] = useState('');
  const [format, setFormat] = useState<MatchFormat>('T20');
  const [maxOvers, setMaxOvers] = useState<number>(20);
  const [homeTeamId, setHomeTeamId] = useState<number>(0);
  const [awayTeamId, setAwayTeamId] = useState<number>(0);
  const [venue, setVenue] = useState('');
  const [competition, setCompetition] = useState('');
  const [startTime, setStartTime] = useState(new Date().toISOString().slice(0, 16));

  useEffect(() => {
    teamService.getTeams({ size: 100 }).then((res) => {
      setTeams(res.content || []);
      if (res.content && res.content.length >= 2) {
        setHomeTeamId(res.content[0].id);
        setAwayTeamId(res.content[1].id);
      }
    });
  }, []);

  const handleFormatChange = (newFormat: MatchFormat) => {
    setFormat(newFormat);
    if (newFormat === 'T20') setMaxOvers(20);
    else if (newFormat === 'ODI') setMaxOvers(50);
    else if (newFormat === 'TEST') setMaxOvers(90);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!homeTeamId || !awayTeamId) {
      setError('Please select both home and away teams.');
      return;
    }
    if (homeTeamId === awayTeamId) {
      setError('Home and away teams must be different.');
      return;
    }

    try {
      setSubmitting(true);
      setError(null);

      const payload: CreateMatchPayload = {
        title: title || `${teams.find((t) => t.id === homeTeamId)?.shortName || 'Home'} vs ${teams.find((t) => t.id === awayTeamId)?.shortName || 'Away'}`,
        format,
        maxOversPerInnings: maxOvers,
        homeTeamId,
        awayTeamId,
        venue,
        competition,
        startTime: new Date(startTime).toISOString()
      };

      const created = await matchService.createMatch(payload);
      navigate(`/matches/${created.id}`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to schedule match');
    } finally {
      setSubmitting(false);
    }
  };

  if (!isAdmin) {
    return (
      <PageContainer>
        <div className="p-8 text-center bg-white rounded-2xl border border-cricket-border max-w-md mx-auto my-12">
          <AlertCircle className="w-10 h-10 text-cricket-red mx-auto mb-3" />
          <h2 className="text-lg font-bold text-cricket-navy">Administrator Access Required</h2>
          <p className="text-xs text-cricket-muted mt-1 mb-4">
            Only users with the Administrator role can schedule and create matches.
          </p>
          <Link to="/login" className="px-4 py-2 bg-cricket-red text-white text-xs font-bold rounded-lg">
            Sign In as Admin
          </Link>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer maxWidth="5xl" className="space-y-6">
      <Link to="/matches" className="inline-flex items-center gap-1.5 text-xs font-bold text-cricket-muted hover:text-cricket-navy">
        <ArrowLeft className="w-4 h-4" />
        Back to Matches
      </Link>

      <div className="bg-white rounded-2xl border border-cricket-border shadow-card p-6 sm:p-8 space-y-6">
        <div>
          <h1 className="text-2xl font-extrabold text-cricket-navy">Schedule New Match</h1>
          <p className="text-xs text-cricket-muted mt-1">
            Configure participating teams, format parameters, venue, and start time.
          </p>
        </div>

        {error && (
          <div className="p-3 bg-red-50 text-red-700 text-xs rounded-xl border border-red-200">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6 text-sm">
          {/* Match Title */}
          <div>
            <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
              Match Title
            </label>
            <input
              type="text"
              required
              placeholder="e.g. India vs Australia - 1st T20I"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
            />
          </div>

          {/* Teams Selection */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
                Home / First Team
              </label>
              <select
                required
                value={homeTeamId}
                onChange={(e) => setHomeTeamId(Number(e.target.value))}
                className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
              >
                {teams.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name} ({t.shortName})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
                Away / Second Team
              </label>
              <select
                required
                value={awayTeamId}
                onChange={(e) => setAwayTeamId(Number(e.target.value))}
                className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
              >
                {teams.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name} ({t.shortName})
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Format & Overs */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
                Match Format
              </label>
              <select
                value={format}
                onChange={(e) => handleFormatChange(e.target.value as MatchFormat)}
                className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
              >
                <option value="T20">T20 (20 Overs)</option>
                <option value="ODI">One Day International (50 Overs)</option>
                <option value="TEST">Test Match</option>
                <option value="CUSTOM">Custom Limited Overs</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
                Max Overs Per Innings
              </label>
              <input
                type="number"
                min={1}
                max={150}
                value={maxOvers}
                onChange={(e) => setMaxOvers(Number(e.target.value))}
                className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
              />
            </div>
          </div>

          {/* Venue & Competition */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
                Venue / Stadium
              </label>
              <input
                type="text"
                placeholder="e.g. Melbourne Cricket Ground"
                value={venue}
                onChange={(e) => setVenue(e.target.value)}
                className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
                Tournament / Competition
              </label>
              <input
                type="text"
                placeholder="e.g. World Cup 2026"
                value={competition}
                onChange={(e) => setCompetition(e.target.value)}
                className="w-full bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
              />
            </div>
          </div>

          {/* Start Time */}
          <div>
            <label className="block text-xs font-bold text-cricket-navy uppercase mb-1">
              Start Date & Time
            </label>
            <input
              type="datetime-local"
              required
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              className="w-full sm:w-72 bg-white border border-gray-300 rounded-xl px-4 py-2.5 text-sm"
            />
          </div>

          <div className="pt-4 border-t border-gray-100 flex items-center justify-end gap-3">
            <Link
              to="/matches"
              className="px-5 py-2.5 rounded-xl border border-gray-300 text-cricket-muted hover:bg-gray-50 text-xs font-bold"
            >
              Cancel
            </Link>
            <button
              type="submit"
              disabled={submitting}
              className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-cricket-red hover:bg-cricket-red-dark text-white text-xs font-bold shadow-md transition-colors disabled:opacity-50"
            >
              <PlusCircle className="w-4 h-4" />
              Schedule Match
            </button>
          </div>
        </form>
      </div>
    </PageContainer>
  );
};
