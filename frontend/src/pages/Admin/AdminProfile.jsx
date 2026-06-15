import React from 'react';
import { Link } from 'react-router-dom';
import { Users, Settings } from 'lucide-react';

const AdminProfile = () => {
  return (
    <div className="premium-card" style={{ padding: '1.5rem' }}>
      <h2 style={{ marginBottom: '1rem', fontSize: '1.25rem' }}>Admin Dashboard</h2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <Link to="/admin/user-management" className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Users size={18} /> Manage Users
        </Link>
        <Link to="/admin/settings" className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Settings size={18} /> Library Settings
        </Link>
      </div>
    </div>
  );
};

export default AdminProfile;
