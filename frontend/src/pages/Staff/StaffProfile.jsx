import React from 'react';
import { Link } from 'react-router-dom';
import { Calendar, ClipboardList } from 'lucide-react';

const StaffProfile = () => {
  return (
    <div className="premium-card" style={{ padding: '1.5rem' }}>
      <h2 style={{ marginBottom: '1rem', fontSize: '1.25rem' }}>Staff Dashboard</h2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <Link to="/staff/schedule" className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Calendar size={18} /> View Schedule
        </Link>
        <Link to="/staff/reservations" className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <ClipboardList size={18} /> Manage Reservations
        </Link>
      </div>
    </div>
  );
};

export default StaffProfile;
