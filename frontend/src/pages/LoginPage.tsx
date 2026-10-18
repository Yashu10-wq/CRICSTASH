import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Shield, Lock, User, Mail, AlertCircle, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { PageContainer } from '../components/layout/PageContainer';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login, register, isAuthenticated } = useAuth();

  const [isRegistering, setIsRegistering] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<'ADMIN' | 'SCORER' | 'VIEWER'>('SCORER');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (isAuthenticated) {
    navigate('/');
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError(null);
      if (isRegistering) {
        await register({ username, email, password, role });
      } else {
        await login({ username, password });
      }
      navigate('/matches');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = async (u: string, p: string, r: 'ADMIN' | 'SCORER') => {
    try {
      setLoading(true);
      setError(null);
      // Try logging in
      try {
        await login({ username: u, password: p });
        navigate('/scorer');
      } catch {
        // If user doesn't exist, register them
        await register({ username: u, email: `${u}@cricstash.internal`, password: p, role: r });
        navigate('/scorer');
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Quick login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageContainer maxWidth="5xl" className="py-12">
      <div className="max-w-md mx-auto bg-white rounded-3xl border border-cricket-border shadow-card-hover p-8 space-y-6">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-cricket-navy to-cricket-blue text-white font-black text-xl flex items-center justify-center mx-auto shadow-md">
            CS
          </div>
          <h1 className="text-2xl font-extrabold text-cricket-navy">
            {isRegistering ? 'Create CricStash Account' : 'Sign In to CricStash'}
          </h1>
          <p className="text-xs text-cricket-muted">
            {isRegistering
              ? 'Register with an authorized role to score matches'
              : 'Sign in to access match administration and live scoring'}
          </p>
        </div>

        {error && (
          <div className="p-3 bg-red-50 text-red-700 text-xs rounded-xl border border-red-200 flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-cricket-red shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4 text-xs font-semibold">
          <div>
            <label className="block text-cricket-navy uppercase mb-1">Username</label>
            <div className="relative">
              <User className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-cricket-muted" />
              <input
                type="text"
                required
                placeholder="Enter username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full bg-gray-50 border border-gray-200 rounded-xl pl-9 pr-3 py-2.5 text-sm font-medium focus:bg-white focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
              />
            </div>
          </div>

          {isRegistering && (
            <div>
              <label className="block text-cricket-navy uppercase mb-1">Email</label>
              <div className="relative">
                <Mail className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-cricket-muted" />
                <input
                  type="email"
                  required
                  placeholder="name@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full bg-gray-50 border border-gray-200 rounded-xl pl-9 pr-3 py-2.5 text-sm font-medium focus:bg-white focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
                />
              </div>
            </div>
          )}

          <div>
            <label className="block text-cricket-navy uppercase mb-1">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-cricket-muted" />
              <input
                type="password"
                required
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-gray-50 border border-gray-200 rounded-xl pl-9 pr-3 py-2.5 text-sm font-medium focus:bg-white focus:outline-none focus:ring-2 focus:ring-cricket-blue/20"
              />
            </div>
          </div>

          {isRegistering && (
            <div>
              <label className="block text-cricket-navy uppercase mb-1">Role Permission</label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value as any)}
                className="w-full bg-gray-50 border border-gray-200 rounded-xl px-3 py-2.5 text-sm font-bold text-cricket-navy"
              >
                <option value="SCORER">Official Scorer (Scoring & Delivery Entry)</option>
                <option value="ADMIN">Administrator (Full Control & Match Setup)</option>
                <option value="VIEWER">Viewer (Public Read-Only)</option>
              </select>
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-xl bg-cricket-red hover:bg-cricket-red-dark text-white font-extrabold text-sm shadow-md transition-all active:scale-95 disabled:opacity-50 mt-2"
          >
            {loading ? 'Authenticating...' : isRegistering ? 'Register Account' : 'Sign In'}
          </button>
        </form>

        <div className="pt-2 text-center text-xs text-cricket-muted">
          {isRegistering ? (
            <span>
              Already have an account?{' '}
              <button
                onClick={() => setIsRegistering(false)}
                className="font-bold text-cricket-navy hover:underline"
              >
                Sign In
              </button>
            </span>
          ) : (
            <span>
              Need a scorer account?{' '}
              <button
                onClick={() => setIsRegistering(true)}
                className="font-bold text-cricket-red hover:underline"
              >
                Register Here
              </button>
            </span>
          )}
        </div>

        {/* Quick Demo Helper Section */}
        <div className="mt-6 pt-6 border-t border-gray-100 space-y-3">
          <div className="text-[11px] font-bold text-cricket-muted uppercase tracking-wider text-center">
            ⚡ Quick Demo Accounts
          </div>
          <div className="grid grid-cols-2 gap-2 text-xs">
            <button
              onClick={() => handleQuickLogin('official_scorer', 'ScorerPass123!', 'SCORER')}
              className="p-2.5 rounded-xl border border-blue-200 bg-blue-50/70 hover:bg-blue-100 text-cricket-blue font-bold text-center transition-colors"
            >
              Scorer Account
            </button>
            <button
              onClick={() => handleQuickLogin('super_admin', 'AdminPass123!', 'ADMIN')}
              className="p-2.5 rounded-xl border border-red-200 bg-red-50/70 hover:bg-red-100 text-cricket-red font-bold text-center transition-colors"
            >
              Admin Account
            </button>
          </div>
        </div>
      </div>
    </PageContainer>
  );
};
