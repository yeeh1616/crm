import api from './api';
import { LoginResponse } from '../types/User';

export const authService = {
  login: async (username: string, password: string): Promise<LoginResponse> => {
    localStorage.removeItem("token");
    const response = await api.post<LoginResponse>('/auth/login', {
      username,
      password,
    });
    return response.data;
  },

  logout: async (): Promise<void> => {
    // Add logout logic if needed
  },
};

