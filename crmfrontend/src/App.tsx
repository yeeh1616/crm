import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import CustomerListPage from './pages/CustomerListPage';
import LeadListPage from './pages/LeadListPage';
import ActivityListPage from './pages/ActivityListPage';
import ReportPage from './pages/ReportPage';
import './App.css';

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/customers"
        element={
          <PrivateRoute>
            <CustomerListPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/leads/:customerId"
        element={
          <PrivateRoute>
            <LeadListPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/activities/:leadId"
        element={
          <PrivateRoute>
            <ActivityListPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/reports"
        element={
          <PrivateRoute>
            <ReportPage />
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Navigate to="/customers" />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

