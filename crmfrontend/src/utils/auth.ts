const TOKEN_KEY = 'token';
const USER_KEY = 'user';

export const authUtils = {
  getToken: (): string | null => {
    return localStorage.getItem(TOKEN_KEY);
  },

  setToken: (token: string): void => {
    localStorage.setItem(TOKEN_KEY, token);
  },

  removeToken: (): void => {
    localStorage.removeItem(TOKEN_KEY);
  },

  getUser: (): any | null => {
    const userStr = localStorage.getItem(USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  },

  setUser: (user: any): void => {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  removeUser: (): void => {
    localStorage.removeItem(USER_KEY);
  },

  isAuthenticated: (): boolean => {
    return !!authUtils.getToken();
  },

  clearAuth: (): void => {
    authUtils.removeToken();
    authUtils.removeUser();
  },
};

