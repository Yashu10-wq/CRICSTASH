import React from 'react';
import { MatchStatus } from '../../types';

interface StatusBadgeProps {
  status: MatchStatus;
  size?: 'sm' | 'md' | 'lg';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'md' }) => {
  const sizeClasses = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-xs font-semibold px-2.5 py-1',
    lg: 'text-sm font-bold px-3 py-1.5'
  }[size];

  switch (status) {
    case 'LIVE':
      return (
        <span className={`inline-flex items-center gap-1.5 rounded-full bg-red-50 text-cricket-red border border-red-200 ${sizeClasses}`}>
          <span className="w-2 h-2 rounded-full bg-cricket-red live-dot" />
          LIVE
        </span>
      );
    case 'INNINGS_BREAK':
      return (
        <span className={`inline-flex items-center rounded-full bg-amber-50 text-amber-700 border border-amber-200 ${sizeClasses}`}>
          INNINGS BREAK
        </span>
      );
    case 'COMPLETED':
      return (
        <span className={`inline-flex items-center rounded-full bg-blue-50 text-cricket-navy border border-blue-200 ${sizeClasses}`}>
          COMPLETED
        </span>
      );
    case 'SCHEDULED':
      return (
        <span className={`inline-flex items-center rounded-full bg-gray-100 text-gray-700 border border-gray-200 ${sizeClasses}`}>
          UPCOMING
        </span>
      );
    case 'ABANDONED':
    case 'CANCELLED':
      return (
        <span className={`inline-flex items-center rounded-full bg-red-100 text-red-700 border border-red-300 ${sizeClasses}`}>
          {status}
        </span>
      );
    default:
      return (
        <span className={`inline-flex items-center rounded-full bg-gray-100 text-gray-600 ${sizeClasses}`}>
          {status}
        </span>
      );
  }
};
