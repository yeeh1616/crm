import React from 'react';
import { useAuth } from '../context/AuthContext';

const ReportPage: React.FC = () => {
  const { user, logout } = useAuth();
  
  return (
    <div className="app">
      <header className="app-header">
        <h1>CRM System - Reports</h1>
        <div>
          <span>Welcome, {user?.username}</span>
          <button onClick={logout} style={{ marginLeft: '1rem' }}>Logout</button>
        </div>
      </header>
      <div className="content">
        <p>Report Page - To be implemented with analytics and export functionality</p>
      </div>
    </div>
  );
};

export default ReportPage;

