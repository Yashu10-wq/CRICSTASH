import { apiClient } from './apiClient';
import { AuthUser } from '../types';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  role?: 'ADMIN' | 'SCORER' | 'VIEWER';
}

export interface AuthResponseData {
  token: string;
  type: string;
  username: string;
  role: 'ROLE_ADMIN' | 'ROLE_SCORER' | 'ROLE_VIEWER';
  expiresInMs: number;
}

export const authService = {
  async login(payload: LoginPayload): Promise<AuthUser> {
    const data = await apiClient.post<AuthResponseData>('/auth/login', payload);
    const user: AuthUser = {
      username: data.username,
      role: data.role,
      token: data.token
    };
    localStorage.setItem('cricstash_token', data.token);
    localStorage.setItem('cricstash_user', JSON.stringify(user));
    return user;
  },

  async register(payload: RegisterPayload): Promise<AuthUser> {
    const formattedRole = payload.role
      ? payload.role.startsWith('ROLE_')
        ? payload.role
        : `ROLE_${payload.role}`
      : 'ROLE_VIEWER';

    const data = await apiClient.post<AuthResponseData>('/auth/register', {
      ...payload,
      role: formattedRole
    });
    const user: AuthUser = {
      username: data.username,
      role: data.role,
      token: data.token
    };
    localStorage.setItem('cricstash_token', data.token);
    localStorage.setItem('cricstash_user', JSON.stringify(user));
    return user;
  },

  logout(): void {
    localStorage.removeItem('cricstash_token');
    localStorage.removeItem('cricstash_user');
  },

  getCurrentUser(): AuthUser | null {
    const stored = localStorage.getItem('cricstash_user');
    if (!stored) return null;
    try {
      return JSON.parse(stored) as AuthUser;
    } catch {
      return null;
    }
  }
};
