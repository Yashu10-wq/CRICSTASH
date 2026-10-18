import React from 'react';
import { Link } from 'react-router-dom';

export const Footer: React.FC = () => {
  return (
    <footer className="mt-auto bg-white border-t border-cricket-border py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          {/* Brand */}
          <div className="space-y-3">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-cricket-navy flex items-center justify-center text-white font-extrabold text-sm">
                CS
              </div>
              <span className="text-lg font-extrabold text-cricket-navy">
                CRIC<span className="text-cricket-red">STASH</span>
              </span>
            </div>
            <p className="text-xs text-cricket-muted leading-relaxed">
              Every Ball. Every Run. Every Moment. A modern, real-time cricket score management system and match center.
            </p>
          </div>

          {/* Matches Links */}
          <div>
            <h4 className="text-xs font-bold text-cricket-navy uppercase tracking-wider mb-3">Live Cricket</h4>
            <ul className="space-y-2 text-sm text-cricket-muted">
              <li>
                <Link to="/matches?status=LIVE" className="hover:text-cricket-red transition-colors">
                  Live Match Centre
                </Link>
              </li>
              <li>
                <Link to="/matches?status=SCHEDULED" className="hover:text-cricket-red transition-colors">
                  Upcoming Fixtures
                </Link>
              </li>
              <li>
                <Link to="/matches?status=COMPLETED" className="hover:text-cricket-red transition-colors">
                  Recent Match Results
                </Link>
              </li>
            </ul>
          </div>

          {/* Directory */}
          <div>
            <h4 className="text-xs font-bold text-cricket-navy uppercase tracking-wider mb-3">Directory</h4>
            <ul className="space-y-2 text-sm text-cricket-muted">
              <li>
                <Link to="/teams" className="hover:text-cricket-red transition-colors">
                  Teams & Squads
                </Link>
              </li>
              <li>
                <Link to="/players" className="hover:text-cricket-red transition-colors">
                  Players & Profiles
                </Link>
              </li>
              <li>
                <Link to="/scorer" className="hover:text-cricket-red transition-colors">
                  Scorer & Admin Hub
                </Link>
              </li>
            </ul>
          </div>

          {/* Technology & Architecture */}
          <div>
            <h4 className="text-xs font-bold text-cricket-navy uppercase tracking-wider mb-3">Platform</h4>
            <p className="text-xs text-cricket-muted leading-relaxed mb-2">
              Powered by Spring Boot 3, Neon PostgreSQL, STOMP WebSockets, and React 19.
            </p>
            <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded bg-green-50 text-green-700 text-[11px] font-semibold border border-green-200">
              <span className="w-1.5 h-1.5 rounded-full bg-green-500"></span>
              Live Neon DB Sync
            </div>
          </div>
        </div>

        <div className="pt-8 border-t border-cricket-border flex flex-col sm:flex-row items-center justify-between text-xs text-cricket-muted gap-4">
          <p>© {new Date().getFullYear()} CricStash Score Management System. All rights reserved.</p>
          <div className="flex gap-6">
            <span>T20 • ODI • Test Matches</span>
            <span>Sub-second WebSockets</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
