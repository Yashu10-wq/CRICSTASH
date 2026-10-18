import React from 'react';

interface PageContainerProps {
  children: React.ReactNode;
  className?: string;
  maxWidth?: '5xl' | '6xl' | '7xl' | 'full';
}

export const PageContainer: React.FC<PageContainerProps> = ({
  children,
  className = '',
  maxWidth = '7xl'
}) => {
  const maxWidthClass = {
    '5xl': 'max-w-5xl',
    '6xl': 'max-w-6xl',
    '7xl': 'max-w-7xl',
    full: 'max-w-full'
  }[maxWidth];

  return (
    <main className={`flex-1 w-full ${maxWidthClass} mx-auto px-4 sm:px-6 lg:px-8 py-6 md:py-8 ${className}`}>
      {children}
    </main>
  );
};
