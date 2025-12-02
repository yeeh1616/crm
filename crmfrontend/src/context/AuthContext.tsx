import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authUtils } from '../utils/auth';
import { User } from '../types/User';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = authUtils.getToken();
    const storedUser = authUtils.getUser();
    if (token && storedUser) {
      setUser(storedUser);
      setIsAuthenticated(true);
    }
  }, []);

  const login = (token: string, userData: User) => {
    authUtils.setToken(token);
    authUtils.setUser(userData);
    setUser(userData);
    setIsAuthenticated(true);
  };

  const logout = () => {
    authUtils.clearAuth();
    setUser(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

