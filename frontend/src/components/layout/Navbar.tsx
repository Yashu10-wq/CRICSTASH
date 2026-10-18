import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Menu, X, Radio, Trophy, Users, User, Shield, LogOut, LogIn, Plus } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Navbar: React.FC = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const location = useLocation();
  const { user, logout, isScorer, isAdmin } = useAuth();

  const isActive = (path: string) => {
    if (path === '/') return location.pathname === '/';
    return location.pathname.startsWith(path);
  };

  const navLinks = [
    { name: 'Home', path: '/' },
    { name: 'Live Matches', path: '/matches?status=LIVE', icon: Radio },
    { name: 'Matches & Fixtures', path: '/matches', icon: Trophy },
    { name: 'Teams', path: '/teams', icon: Users },
    { name: 'Players', path: '/players', icon: User }
  ];

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-cricket-border shadow-header">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Brand */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cricket-navy to-cricket-blue flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
              <span className="text-white font-extrabold text-xl tracking-tighter">CS</span>
            </div>
            <div className="flex flex-col text-left">
              <span className="text-xl font-extrabold tracking-tight text-cricket-navy flex items-center gap-1">
                CRIC<span className="text-cricket-red">STASH</span>
              </span>
              <span className="text-[10px] font-semibold text-cricket-muted tracking-widest uppercase -mt-1">
                Real-Time Match Centre
              </span>
            </div>
          </Link>

          {/* Desktop Nav Links */}
          <nav className="hidden md:flex items-center gap-1">
            {navLinks.map((link) => {
              const active = isActive(link.path);
              return (
                <Link
                  key={link.name}
                  to={link.path}
                  className={`px-3.5 py-2 rounded-lg text-sm font-semibold transition-all flex items-center gap-1.5 ${
                    active
                      ? 'bg-cricket-navy-light text-cricket-navy'
                      : 'text-cricket-muted hover:text-cricket-navy hover:bg-gray-50'
                  }`}
                >
                  {link.icon && (
                    <link.icon
                      className={`w-4 h-4 ${link.name === 'Live Matches' ? 'text-cricket-red animate-pulse' : ''}`}
                    />
                  )}
                  {link.name}
                </Link>
              );
            })}

            {isScorer && (
              <Link
                to="/scorer"
                className={`px-3.5 py-2 rounded-lg text-sm font-semibold transition-all flex items-center gap-1.5 ${
                  location.pathname === '/scorer'
                    ? 'bg-red-50 text-cricket-red border border-red-200'
                    : 'text-cricket-red hover:bg-red-50'
                }`}
              >
                <Shield className="w-4 h-4 text-cricket-red" />
                Scorer Console
              </Link>
            )}
          </nav>

          {/* User Auth & Actions */}
          <div className="hidden md:flex items-center gap-3">
            {isAdmin && (
              <Link
                to="/matches/new"
                className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-cricket-navy hover:bg-cricket-navy-dark text-white text-xs font-semibold shadow-sm transition-colors"
              >
                <Plus className="w-3.5 h-3.5" />
                New Match
              </Link>
            )}

            {user ? (
              <div className="flex items-center gap-2 pl-3 border-l border-cricket-border">
                <div className="flex flex-col text-right">
                  <span className="text-xs font-bold text-cricket-navy">{user.username}</span>
                  <span className="text-[10px] font-semibold text-cricket-red uppercase">
                    {user.role.replace('ROLE_', '')}
                  </span>
                </div>
                <button
                  onClick={logout}
                  title="Sign out"
                  className="p-1.5 rounded-lg text-cricket-muted hover:text-cricket-red hover:bg-gray-100 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg bg-cricket-red hover:bg-cricket-red-dark text-white text-xs font-bold shadow-sm transition-all hover:shadow"
              >
                <LogIn className="w-3.5 h-3.5" />
                Sign In
              </Link>
            )}
          </div>

          {/* Mobile hamburger */}
          <div className="flex md:hidden">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-cricket-navy hover:bg-gray-100 transition-colors"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-cricket-border bg-white px-4 pt-3 pb-6 space-y-2 shadow-xl">
          {navLinks.map((link) => (
            <Link
              key={link.name}
              to={link.path}
              onClick={() => setMobileMenuOpen(false)}
              className="flex items-center gap-2.5 px-3 py-2.5 rounded-lg text-base font-semibold text-cricket-navy hover:bg-gray-50"
            >
              {link.icon && <link.icon className="w-5 h-5 text-cricket-muted" />}
              {link.name}
            </Link>
          ))}

          {isScorer && (
            <Link
              to="/scorer"
              onClick={() => setMobileMenuOpen(false)}
              className="flex items-center gap-2.5 px-3 py-2.5 rounded-lg text-base font-semibold text-cricket-red bg-red-50/50"
            >
              <Shield className="w-5 h-5" />
              Scorer Console
            </Link>
          )}

          <div className="pt-4 border-t border-cricket-border flex items-center justify-between">
            {user ? (
              <div className="flex items-center justify-between w-full">
                <div>
                  <div className="text-sm font-bold text-cricket-navy">{user.username}</div>
                  <div className="text-xs text-cricket-red uppercase font-semibold">
                    {user.role.replace('ROLE_', '')}
                  </div>
                </div>
                <button
                  onClick={() => {
                    logout();
                    setMobileMenuOpen(false);
                  }}
                  className="flex items-center gap-1 px-3 py-1.5 rounded-lg text-xs font-semibold bg-gray-100 text-cricket-muted"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  Sign Out
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="w-full text-center py-2.5 rounded-lg bg-cricket-red text-white text-sm font-bold"
              >
                Sign In
              </Link>
            )}
          </div>
        </div>
      )}
    </header>
  );
};
