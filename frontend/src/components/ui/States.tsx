import React from 'react';
import { AlertCircle, RefreshCw, Trophy } from 'lucide-react';

export const LoadingSkeleton: React.FC<{ rows?: number }> = ({ rows = 3 }) => {
  return (
    <div className="space-y-4 animate-pulse w-full">
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="bg-white rounded-xl p-5 border border-cricket-border shadow-card">
          <div className="flex justify-between items-center mb-4">
            <div className="h-4 bg-gray-200 rounded w-1/4"></div>
            <div className="h-5 bg-gray-200 rounded-full w-16"></div>
          </div>
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <div className="h-6 bg-gray-200 rounded w-1/3"></div>
              <div className="h-6 bg-gray-200 rounded w-16"></div>
            </div>
            <div className="flex justify-between items-center">
              <div className="h-6 bg-gray-200 rounded w-1/3"></div>
              <div className="h-6 bg-gray-200 rounded w-16"></div>
            </div>
          </div>
          <div className="mt-4 pt-3 border-t border-gray-100 flex justify-between">
            <div className="h-3 bg-gray-200 rounded w-1/2"></div>
            <div className="h-3 bg-gray-200 rounded w-1/6"></div>
          </div>
        </div>
      ))}
    </div>
  );
};

export const EmptyState: React.FC<{
  title?: string;
  message?: string;
  actionText?: string;
  onAction?: () => void;
}> = ({
  title = 'No matches found',
  message = 'There are no matches currently scheduled or active for this filter.',
  actionText,
  onAction
}) => {
  return (
    <div className="bg-white rounded-2xl p-10 border border-cricket-border shadow-card text-center max-w-lg mx-auto my-8">
      <div className="w-16 h-16 rounded-full bg-cricket-navy-light flex items-center justify-center mx-auto mb-4 text-cricket-navy">
        <Trophy className="w-8 h-8 text-cricket-navy" />
      </div>
      <h3 className="text-lg font-bold text-cricket-text mb-1">{title}</h3>
      <p className="text-sm text-cricket-muted mb-6">{message}</p>
      {actionText && onAction && (
        <button
          onClick={onAction}
          className="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg bg-cricket-red hover:bg-cricket-red-dark text-white font-medium text-sm transition-colors shadow-sm"
        >
          {actionText}
        </button>
      )}
    </div>
  );
};

export const ErrorState: React.FC<{
  title?: string;
  message?: string;
  onRetry?: () => void;
}> = ({
  title = 'Failed to load data',
  message = 'We encountered an issue connecting to the backend server. Please verify the service is running.',
  onRetry
}) => {
  return (
    <div className="bg-red-50/50 rounded-2xl p-8 border border-red-200 text-center max-w-lg mx-auto my-8">
      <div className="w-14 h-14 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-3 text-cricket-red">
        <AlertCircle className="w-7 h-7" />
      </div>
      <h3 className="text-base font-bold text-cricket-text mb-1">{title}</h3>
      <p className="text-sm text-red-700/80 mb-5">{message}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-white border border-red-300 text-cricket-red hover:bg-red-50 font-medium text-sm transition-colors shadow-sm"
        >
          <RefreshCw className="w-4 h-4" />
          Retry Connection
        </button>
      )}
    </div>
  );
};
