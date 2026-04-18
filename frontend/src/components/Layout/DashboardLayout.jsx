import React from 'react';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

const DashboardLayout = ({ children }) => {
  return (
    <div className="dashboard-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar />
        <div style={{ marginTop: '2rem' }}>
          {children}
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;
