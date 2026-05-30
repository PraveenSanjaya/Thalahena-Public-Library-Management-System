import React, { useState } from 'react';
import authService from '../services/auth.service';
import api from '../services/api';
import { Camera, Save, Key, ShieldOff } from 'lucide-react';

const Profile = () => {
  const user = authService.getCurrentUser();
  const [profile, setProfile] = useState({
    username: user.username,
    email: user.email,
  });

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/users/${user.userId}`, profile);
      alert('Profile updated! Please log in again if email changed.');
    } catch (err) {
      alert('Update failed');
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '2rem' }}>My Profile</h1>

      <div style={{ display: 'grid', gridTemplateColumns: '250px 1fr', gap: '2rem' }}>
        <div className="premium-card" style={{ textAlign: 'center' }}>
          <div style={{ position: 'relative', width: '120px', height: '120px', margin: '0 auto 1.5rem', borderRadius: '50%', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <span style={{ fontSize: '2.5rem', fontWeight: 700 }}>{user?.username ? user.username[0].toUpperCase() : '?'}</span>
            <button style={{ position: 'absolute', bottom: 0, right: 0, background: 'var(--bg-dark)', border: '1px solid var(--primary)', borderRadius: '50%', width: '32px', height: '32px', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer' }}>
              <Camera size={16} />
            </button>
          </div>
          <h3 style={{ marginBottom: '0.25rem' }}>{user?.username}</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>{user?.role}</p>
          <div style={{ marginTop: '2rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
             <button className="btn-primary" style={{ background: 'rgba(239, 68, 68, 0.1)', color: 'var(--danger)', border: '1px solid rgba(239, 68, 68, 0.2)' }}>
               <ShieldOff size={16} style={{ marginRight: '0.5rem' }} /> Deactivate Account
             </button>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div className="premium-card">
            <h4 style={{ marginBottom: '1.5rem' }}>Account Information</h4>
            <form onSubmit={handleUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <label style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.5rem', display: 'block' }}>Username</label>
                <input type="text" value={profile.username} disabled />
              </div>
              <div>
                <label style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.5rem', display: 'block' }}>Email Address</label>
                <input type="email" value={profile.email} onChange={(e) => setProfile({...profile, email: e.target.value})} />
              </div>
              <button className="btn-primary" style={{ alignSelf: 'flex-end', marginTop: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Save size={18} /> Save Changes
              </button>
            </form>
          </div>

          <div className="premium-card">
            <h4 style={{ marginBottom: '1.5rem' }}>Security</h4>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <p style={{ fontWeight: 500 }}>Change Password</p>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>Security recommendation: Use at least 12 characters</p>
              </div>
              <button className="btn-primary" style={{ background: 'transparent', border: '1px solid var(--primary)', color: 'var(--primary)' }}>
                Update
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
