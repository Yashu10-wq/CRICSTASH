import React from 'react';
import { Team } from '../../types';

interface TeamBadgeProps {
  team: Team;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  showFullName?: boolean;
}

export const TeamBadge: React.FC<TeamBadgeProps> = ({ team, size = 'md', showFullName = true }) => {
  const getInitials = (name: string, shortName?: string) => {
    if (shortName && shortName.length <= 4) return shortName.toUpperCase();
    return name
      .split(' ')
      .map((n) => n[0])
      .slice(0, 3)
      .join('')
      .toUpperCase();
  };

  const avatarSize = {
    sm: 'w-7 h-7 text-xs',
    md: 'w-9 h-9 text-sm',
    lg: 'w-12 h-12 text-base font-bold',
    xl: 'w-16 h-16 text-xl font-extrabold'
  }[size];

  // Deterministic color palette for teams without logos
  const colors = [
    'bg-blue-600 text-white',
    'bg-emerald-600 text-white',
    'bg-indigo-600 text-white',
    'bg-rose-600 text-white',
    'bg-amber-600 text-white',
    'bg-cyan-700 text-white',
    'bg-cricket-navy text-white'
  ];
  const colorIndex = Math.abs(team.id || team.name.charCodeAt(0)) % colors.length;
  const badgeColor = colors[colorIndex];

  return (
    <div className="flex items-center gap-2.5">
      {team.logoUrl ? (
        <img
          src={team.logoUrl}
          alt={team.name}
          className={`${avatarSize} rounded-full object-cover border border-cricket-border shadow-sm`}
          onError={(e) => {
            // fallback to initials on error
            (e.target as HTMLElement).style.display = 'none';
          }}
        />
      ) : (
        <div
          className={`${avatarSize} rounded-full ${badgeColor} flex items-center justify-center font-bold tracking-wider shadow-sm shrink-0 border border-white/20`}
        >
          {getInitials(team.name, team.shortName)}
        </div>
      )}

      {showFullName && (
        <div className="flex flex-col text-left">
          <span className="font-semibold text-cricket-text leading-tight">{team.name}</span>
          {team.shortName && team.shortName !== team.name && (
            <span className="text-xs text-cricket-muted uppercase tracking-wide">{team.shortName}</span>
          )}
        </div>
      )}
    </div>
  );
};
