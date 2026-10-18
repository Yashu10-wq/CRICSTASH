import React from 'react';
import { BallEvent } from '../../types';

interface BallChipProps {
  ball?: BallEvent;
  runs?: number;
  isWicket?: boolean;
  extraType?: string;
  size?: 'sm' | 'md' | 'lg';
}

export const BallChip: React.FC<BallChipProps> = ({
  ball,
  runs = ball ? ball.batterRuns + ball.totalExtras : 0,
  isWicket = ball ? ball.isWicket : false,
  extraType = ball ? ball.extraType : undefined,
  size = 'md'
}) => {
  const sizeClass = {
    sm: 'w-6 h-6 text-xs font-semibold',
    md: 'w-8 h-8 text-xs font-bold',
    lg: 'w-10 h-10 text-sm font-bold'
  }[size];

  // Wicket ball
  if (isWicket) {
    return (
      <span
        title={ball?.wicketType ? `Wicket (${ball.wicketType})` : 'Wicket'}
        className={`${sizeClass} rounded-full bg-cricket-red text-white flex items-center justify-center shadow-sm shrink-0 border border-red-600`}
      >
        W
      </span>
    );
  }

  // Six
  if (runs >= 6) {
    return (
      <span
        title="SIX"
        className={`${sizeClass} rounded-full bg-cricket-navy text-white flex items-center justify-center shadow-sm shrink-0 border border-cricket-navy-dark`}
      >
        6
      </span>
    );
  }

  // Four
  if (runs === 4) {
    return (
      <span
        title="FOUR"
        className={`${sizeClass} rounded-full bg-blue-600 text-white flex items-center justify-center shadow-sm shrink-0 border border-blue-700`}
      >
        4
      </span>
    );
  }

  // Extras
  if (extraType === 'WIDE') {
    return (
      <span
        title={`Wide (${runs} runs)`}
        className={`${sizeClass} rounded-full bg-amber-100 text-amber-800 border border-amber-300 flex items-center justify-center shrink-0`}
      >
        {runs > 1 ? `${runs}Wd` : 'Wd'}
      </span>
    );
  }

  if (extraType === 'NO_BALL') {
    return (
      <span
        title={`No Ball (${runs} runs)`}
        className={`${sizeClass} rounded-full bg-amber-100 text-amber-800 border border-amber-300 flex items-center justify-center shrink-0`}
      >
        {runs > 1 ? `${runs}Nb` : 'Nb'}
      </span>
    );
  }

  if (extraType === 'BYE' || extraType === 'LEG_BYE') {
    return (
      <span
        title={`${extraType} (${runs} runs)`}
        className={`${sizeClass} rounded-full bg-gray-100 text-gray-700 border border-gray-300 flex items-center justify-center shrink-0`}
      >
        {runs}{extraType === 'BYE' ? 'B' : 'LB'}
      </span>
    );
  }

  // Dot ball
  if (runs === 0) {
    return (
      <span
        title="Dot ball"
        className={`${sizeClass} rounded-full bg-gray-100 text-gray-500 border border-gray-200 flex items-center justify-center shrink-0`}
      >
        •
      </span>
    );
  }

  // 1, 2, 3 runs
  return (
    <span
      title={`${runs} runs`}
      className={`${sizeClass} rounded-full bg-white text-cricket-text border border-cricket-border flex items-center justify-center shadow-sm shrink-0`}
    >
      {runs}
    </span>
  );
};
