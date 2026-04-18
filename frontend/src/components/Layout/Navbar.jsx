import React from 'react';
import authService from '../../services/auth.service';
import { Bell, Search, User } from 'lucide-react';

const Navbar = () => {
  const user = authService.getCurrentUser();

  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <div style={{ position: 'relative', width: '300px' }}>
        <Search style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
        <input 
          type="text" 
          placeholder="Search for books, authors..." 
          style={{ paddingLeft: '2.5rem' }} 
        />
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <div style={{ position: 'relative', cursor: 'pointer' }}>
          <Bell size={20} className="text-muted" />
          <span style={{ position: 'absolute', top: '-4px', right: '-4px', background: 'var(--danger)', width: '8px', height: '8px', borderRadius: '50%' }}></span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{ textAlign: 'right' }}>
            <p style={{ fontWeight: 600, fontSize: '0.875rem' }}>{user?.username}</p>
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user?.roles?.[0] || 'Guest'}</p>
          </div>
          <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyItems: 'center', justifyContent: 'center' }}>
            <User size={20} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Navbar;
