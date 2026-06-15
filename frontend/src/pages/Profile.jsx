import React, { useState, useEffect, useRef } from 'react';
import authService from '../services/auth.service';
import api from '../services/api';
import { Camera, Save, Key, User, Mail, Phone, Calendar, Shield, CreditCard, CheckCircle, AlertCircle, Loader } from 'lucide-react';

const Profile = () => {
  const user = authService.getCurrentUser();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  
  // Alert/Notification State
  const [notification, setNotification] = useState(null); // { type: 'success'|'error', message: '' }

  // Form Fields State (Editable)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    birthDate: '',
    gender: '',
    whatsapp: '',
    socialMedia: '',
    department: '',
    position: ''
  });

  // Password Change Modal State
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [passwordError, setPasswordError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState('');
  const [changingPassword, setChangingPassword] = useState(false);

  const fileInputRef = useRef(null);

  useEffect(() => {
    fetchProfile();
  }, []);

  const triggerNotification = (type, message) => {
    setNotification({ type, message });
    setTimeout(() => {
      setNotification(null);
    }, 5000);
  };

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const response = await api.get('/profile');
      const data = response.data;
      setProfile(data);
      setFormData({
        firstName: data.firstName || '',
        lastName: data.lastName || '',
        email: data.email || '',
        phone: data.phone || '',
        birthDate: data.birthDate || '',
        gender: data.gender || '',
        whatsapp: data.whatsapp || '',
        socialMedia: data.socialMedia || '',
        department: data.department || '',
        position: data.position || ''
      });
    } catch (err) {
      console.error('Error fetching profile:', err);
      triggerNotification('error', 'Failed to load profile details.');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePasswordInputChange = (e) => {
    const { name, value } = e.target;
    setPasswordForm((prev) => ({
      ...prev,
      [name]: value
    }));
    setPasswordError('');
  };

  const validateForm = () => {
    if (!formData.email.trim()) {
      triggerNotification('error', 'Email address is required.');
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      triggerNotification('error', 'Please enter a valid email address.');
      return false;
    }
    
    if (formData.phone) {
      const phoneRegex = /^[0-9+\s-]{8,15}$/;
      if (!phoneRegex.test(formData.phone)) {
        triggerNotification('error', 'Please enter a valid phone number (8-15 digits).');
        return false;
      }
    }
    return true;
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setSaving(true);
    try {
      const response = await api.put('/profile', formData);
      setProfile(response.data);
      setIsEditing(false);
      triggerNotification('success', 'Profile updated successfully!');
      
      // Update local storage user display name if email or username changes
      const currentUser = authService.getCurrentUser();
      if (currentUser) {
        currentUser.email = response.data.email;
        localStorage.setItem('user', JSON.stringify(currentUser));
      }
    } catch (err) {
      console.error('Error updating profile:', err);
      const errMsg = err.response?.data?.message || 'Failed to update profile details.';
      triggerNotification('error', errMsg);
    } finally {
      setSaving(false);
    }
  };

  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      triggerNotification('error', 'File size exceeds 5MB limit.');
      return;
    }

    const uploadData = new FormData();
    uploadData.append('file', file);

    setSaving(true);
    try {
      const response = await api.post('/profile/upload-image', uploadData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      const newImagePath = response.data.message;
      setProfile(prev => ({
        ...prev,
        profilePicture: newImagePath
      }));
      
      triggerNotification('success', 'Profile picture updated successfully!');
      
      // Also update navbar and local storage info
      const currentUser = authService.getCurrentUser();
      if (currentUser) {
        currentUser.profilePicture = newImagePath;
        localStorage.setItem('user', JSON.stringify(currentUser));
      }
    } catch (err) {
      console.error('Error uploading image:', err);
      triggerNotification('error', 'Failed to upload profile picture.');
    } finally {
      setSaving(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (!passwordForm.currentPassword) {
      setPasswordError('Current password is required.');
      return;
    }
    if (passwordForm.newPassword.length < 6) {
      setPasswordError('New password must be at least 6 characters.');
      return;
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError('Passwords do not match.');
      return;
    }

    setChangingPassword(true);
    setPasswordError('');
    setPasswordSuccess('');

    try {
      await api.put('/profile/change-password', passwordForm);
      setPasswordSuccess('Password changed successfully!');
      setPasswordForm({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
      setTimeout(() => {
        setShowPasswordModal(false);
        setPasswordSuccess('');
      }, 2000);
    } catch (err) {
      console.error('Error changing password:', err);
      const errMsg = err.response?.data?.message || 'Failed to change password.';
      setPasswordError(errMsg);
    } finally {
      setChangingPassword(false);
    }
  };

  const getProfileImage = () => {
    if (profile?.profilePicture) {
      if (profile.profilePicture.startsWith('http')) return profile.profilePicture;
      return `http://localhost:8081${profile.profilePicture}`;
    }
    return null;
  };

  const getInitials = () => {
    if (profile?.firstName) {
      return (profile.firstName[0] + (profile.lastName ? profile.lastName[0] : '')).toUpperCase();
    }
    return profile?.username ? profile.username[0].toUpperCase() : '?';
  };

  const getRoleBadgeStyle = (role) => {
    switch (role) {
      case 'ADMIN':
        return { background: 'rgba(239, 68, 68, 0.1)', color: 'var(--danger)', border: '1px solid rgba(239, 68, 68, 0.2)' };
      case 'STAFF':
        return { background: 'rgba(59, 130, 246, 0.1)', color: '#3b82f6', border: '1px solid rgba(59, 130, 246, 0.2)' };
      case 'MEMBER':
      default:
        return { background: 'rgba(99, 102, 241, 0.1)', color: 'var(--primary)', border: '1px solid rgba(99, 102, 241, 0.2)' };
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '60vh', gap: '1rem' }}>
        <Loader className="animate-spin" size={40} color="var(--primary)" style={{ animation: 'spin 1s linear infinite' }} />
        <p style={{ color: 'var(--text-muted)', fontWeight: 500 }}>Loading profile details...</p>
        <style>{`
          @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', paddingBottom: '3rem' }}>
      
      {/* Page Title */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700, color: 'var(--text-main)' }}>My Profile</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage your personal details, account status and preferences</p>
        </div>
        {!isEditing && (
          <button onClick={() => setIsEditing(true)} className="btn btn-primary">
            Edit Profile
          </button>
        )}
      </div>

      {/* Notification Alert Banner */}
      {notification && (
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.75rem',
          padding: '1rem',
          borderRadius: '0.75rem',
          marginBottom: '1.5rem',
          border: '1px solid',
          background: notification.type === 'success' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
          color: notification.type === 'success' ? 'var(--success)' : 'var(--danger)',
          borderColor: notification.type === 'success' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)'
        }}>
          {notification.type === 'success' ? <CheckCircle size={20} /> : <AlertCircle size={20} />}
          <span style={{ fontSize: '0.9rem', fontWeight: 500 }}>{notification.message}</span>
        </div>
      )}

      {/* Main Grid Layout */}
      <div style={{ display: 'grid', gridTemplateColumns: '320px 1fr', gap: '2rem' }}>
        
        {/* Left Side: Avatar Card */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <div className="premium-card" style={{ textAlign: 'center', position: 'relative' }}>
            {/* Avatar container */}
            <div style={{ position: 'relative', width: '130px', height: '130px', margin: '0 auto 1.5rem', borderRadius: '50%' }}>
              {getProfileImage() ? (
                <img
                  src={getProfileImage()}
                  alt="Profile"
                  style={{ width: '100%', height: '100%', borderRadius: '50%', objectFit: 'cover', border: '3px solid var(--border-light)' }}
                />
              ) : (
                <div style={{
                  width: '100%',
                  height: '100%',
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, var(--primary) 0%, #a5b4fc 100%)',
                  color: 'white',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '3rem',
                  fontWeight: 700,
                  boxShadow: '0 4px 12px rgba(99,102,241,0.2)'
                }}>
                  {getInitials()}
                </div>
              )}
              {/* Photo Upload Trigger Button */}
              <button
                onClick={handleUploadClick}
                title="Change profile picture"
                style={{
                  position: 'absolute',
                  bottom: '2px',
                  right: '2px',
                  background: 'var(--primary)',
                  color: 'white',
                  border: '2px solid var(--card-dark)',
                  borderRadius: '50%',
                  width: '36px',
                  height: '36px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                  transition: 'all 0.2s'
                }}
                onMouseEnter={e => { e.currentTarget.style.transform = 'scale(1.1)'; }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'scale(1)'; }}
              >
                <Camera size={16} />
              </button>
              <input
                type="file"
                ref={fileInputRef}
                onChange={handleFileChange}
                accept="image/*"
                style={{ display: 'none' }}
              />
            </div>

            {/* User Meta Information */}
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.25rem' }}>
              {profile?.fullName || profile?.username}
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1rem' }}>@{profile?.username}</p>
            
            {/* Role Badge */}
            <div style={{ display: 'inline-block', padding: '0.35rem 1rem', borderRadius: '2rem', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', ...getRoleBadgeStyle(profile?.role) }}>
              {profile?.role}
            </div>

            {/* Quick stats divider */}
            <div style={{ margin: '1.5rem 0', height: '1px', background: 'var(--border-light)' }}></div>
            
            {/* Display simple status badge */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.875rem' }}>
              <span style={{ color: 'var(--text-muted)' }}>Account Status</span>
              <span style={{
                color: profile?.active ? 'var(--success)' : 'var(--danger)',
                fontWeight: 700,
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.25rem'
              }}>
                <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: profile?.active ? 'var(--success)' : 'var(--danger)' }}></span>
                {profile?.active ? 'Active' : 'Inactive'}
              </span>
            </div>
          </div>

          {/* Member Statistics Panel */}
          {profile?.role === 'MEMBER' && (
            <div className="premium-card" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.25rem' }}>Borrowing Statistics</h3>
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Total Borrowed</span>
                <span style={{ fontWeight: 700, color: 'var(--text-main)', fontSize: '1rem' }}>{profile.totalBooksBorrowed || 0}</span>
              </div>
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Currently Borrowed</span>
                <span style={{ fontWeight: 700, color: 'var(--text-main)', fontSize: '1rem' }}>{profile.currentBorrowedBooks || 0}</span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Outstanding Fines</span>
                <span style={{ fontWeight: 700, color: (profile.outstandingFines > 0 ? 'var(--danger)' : 'var(--success)'), fontSize: '1rem' }}>
                  Rs. {profile.outstandingFines ? profile.outstandingFines.toFixed(2) : '0.00'}
                </span>
              </div>
            </div>
          )}

          {/* Left bottom: Quick Security Buttons */}
          <div className="premium-card" style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <button
              onClick={() => setShowPasswordModal(true)}
              className="btn btn-secondary"
              style={{ width: '100%', justifyContent: 'center', gap: '0.5rem' }}
            >
              <Key size={16} />
              Change Password
            </button>
          </div>
        </div>

        {/* Right Side: Account Details Form */}
        <div className="premium-card" style={{ height: 'fit-content' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Account & Profile Details</h3>
            {isEditing && (
              <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--primary)', textTransform: 'uppercase', padding: '0.2rem 0.5rem', background: 'rgba(99,102,241,0.1)', borderRadius: '0.25rem' }}>
                Editing Profile
              </span>
            )}
          </div>

          <form onSubmit={handleUpdateProfile} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            
            {/* Section: Personal Info */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', borderBottom: '1px solid var(--border-light)', paddingBottom: '0.5rem' }}>
                <User size={18} color="var(--primary)" />
                <span style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-main)' }}>Personal Info</span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>First Name</label>
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder="First Name"
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  />
                </div>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Last Name</label>
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder="Last Name"
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Birth Date</label>
                  <input
                    type="date"
                    name="birthDate"
                    value={formData.birthDate}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  />
                </div>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Gender</label>
                  <select
                    name="gender"
                    value={formData.gender}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  >
                    <option value="">Select Gender</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Section: Contact Info */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '0.5rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', borderBottom: '1px solid var(--border-light)', paddingBottom: '0.5rem' }}>
                <Phone size={18} color="var(--primary)" />
                <span style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-main)' }}>Contact Details</span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Email Address *</label>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder="example@gmail.com"
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                    required
                  />
                </div>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Phone Number</label>
                  <input
                    type="text"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder="Phone Number"
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>WhatsApp Number</label>
                  <input
                    type="text"
                    name="whatsapp"
                    value={formData.whatsapp}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder="WhatsApp Number"
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  />
                </div>
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Social Media Link</label>
                  <input
                    type="text"
                    name="socialMedia"
                    value={formData.socialMedia}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder="Social Media Profile Link"
                    style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                  />
                </div>
              </div>
            </div>

            {/* Section: Staff Specific (Show only for STAFF role) */}
            {profile?.role === 'STAFF' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '0.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', borderBottom: '1px solid var(--border-light)', paddingBottom: '0.5rem' }}>
                  <Shield size={18} color="var(--primary)" />
                  <span style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-main)' }}>Staff Work Info</span>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                  <div>
                    <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Department</label>
                    <input
                      type="text"
                      name="department"
                      value={formData.department}
                      onChange={handleInputChange}
                      disabled={!isEditing}
                      placeholder="e.g. Reference, Circulation"
                      style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                    />
                  </div>
                  <div>
                    <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Position</label>
                    <input
                      type="text"
                      name="position"
                      value={formData.position}
                      onChange={handleInputChange}
                      disabled={!isEditing}
                      placeholder="e.g. Senior Assistant"
                      style={{ background: !isEditing ? 'var(--bg-secondary)' : '#fff' }}
                    />
                  </div>
                </div>
              </div>
            )}

            {/* Section: Account & System Details (Read-only metadata) */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '0.5rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', borderBottom: '1px solid var(--border-light)', paddingBottom: '0.5rem' }}>
                <CreditCard size={18} color="var(--primary)" />
                <span style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-main)' }}>Account Metadata</span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                {profile?.role === 'STAFF' && (
                  <div>
                    <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Staff ID</label>
                    <input
                      type="text"
                      value={profile.staffId || ''}
                      disabled
                      style={{ background: 'var(--bg-secondary)' }}
                    />
                  </div>
                )}
                {profile?.role === 'MEMBER' && (
                  <div>
                    <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Member ID</label>
                    <input
                      type="text"
                      value={profile.memberId || ''}
                      disabled
                      style={{ background: 'var(--bg-secondary)' }}
                    />
                  </div>
                )}
                {profile?.role === 'MEMBER' && (
                  <div>
                    <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Membership Date</label>
                    <input
                      type="text"
                      value={profile.membershipDate ? new Date(profile.membershipDate).toLocaleDateString() : 'N/A'}
                      disabled
                      style={{ background: 'var(--bg-secondary)' }}
                    />
                  </div>
                )}
                <div>
                  <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Account Created</label>
                  <input
                    type="text"
                    value={profile.createdAt ? new Date(profile.createdAt).toLocaleString() : 'N/A'}
                    disabled
                    style={{ background: 'var(--bg-secondary)' }}
                  />
                </div>
                {profile.lastLogin && (
                  <div>
                    <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Last Login Time</label>
                    <input
                      type="text"
                      value={new Date(profile.lastLogin).toLocaleString()}
                      disabled
                      style={{ background: 'var(--bg-secondary)' }}
                    />
                  </div>
                )}
              </div>
            </div>

            {/* Action buttons during edit */}
            {isEditing && (
              <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem', justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => {
                    setIsEditing(false);
                    // Reset to loaded profile details
                    setFormData({
                      firstName: profile.firstName || '',
                      lastName: profile.lastName || '',
                      email: profile.email || '',
                      phone: profile.phone || '',
                      birthDate: profile.birthDate || '',
                      gender: profile.gender || '',
                      whatsapp: profile.whatsapp || '',
                      socialMedia: profile.socialMedia || '',
                      department: profile.department || '',
                      position: profile.position || ''
                    });
                  }}
                  className="btn btn-secondary"
                  disabled={saving}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  style={{ gap: '0.5rem' }}
                  disabled={saving}
                >
                  {saving ? (
                    <>
                      <Loader size={16} className="animate-spin" style={{ animation: 'spin 1s linear infinite' }} />
                      Saving...
                    </>
                  ) : (
                    <>
                      <Save size={16} />
                      Save Changes
                    </>
                  )}
                </button>
              </div>
            )}
          </form>
        </div>

      </div>

      {/* Password Change Popup Modal */}
      {showPasswordModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(15, 23, 42, 0.65)',
          backdropFilter: 'blur(4px)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1100,
          animation: 'fadeIn 0.2s ease-out'
        }}>
          <div style={{
            background: 'var(--card-dark)',
            border: '1px solid var(--border-light)',
            padding: '2rem',
            borderRadius: '1rem',
            maxWidth: '450px',
            width: '90%',
            boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04)',
            position: 'relative'
          }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem', color: 'var(--text-main)' }}>Change Password</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1.5rem' }}>
              Ensure your new password contains at least 6 characters.
            </p>

            {passwordError && (
              <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                padding: '0.75rem',
                borderRadius: '0.5rem',
                background: 'rgba(239, 68, 68, 0.1)',
                color: 'var(--danger)',
                marginBottom: '1rem',
                fontSize: '0.8rem',
                fontWeight: 500
              }}>
                <AlertCircle size={16} />
                <span>{passwordError}</span>
              </div>
            )}

            {passwordSuccess && (
              <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                padding: '0.75rem',
                borderRadius: '0.5rem',
                background: 'rgba(16, 185, 129, 0.1)',
                color: 'var(--success)',
                marginBottom: '1rem',
                fontSize: '0.8rem',
                fontWeight: 500
              }}>
                <CheckCircle size={16} />
                <span>{passwordSuccess}</span>
              </div>
            )}

            <form onSubmit={handlePasswordSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Current Password</label>
                <input
                  type="password"
                  name="currentPassword"
                  value={passwordForm.currentPassword}
                  onChange={handlePasswordInputChange}
                  placeholder="Enter current password"
                  required
                />
              </div>

              <div>
                <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>New Password</label>
                <input
                  type="password"
                  name="newPassword"
                  value={passwordForm.newPassword}
                  onChange={handlePasswordInputChange}
                  placeholder="Enter new password"
                  required
                />
              </div>

              <div>
                <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.4rem', display: 'block' }}>Confirm New Password</label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={passwordForm.confirmPassword}
                  onChange={handlePasswordInputChange}
                  placeholder="Confirm new password"
                  required
                />
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem', justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => {
                    setShowPasswordModal(false);
                    setPasswordError('');
                    setPasswordSuccess('');
                    setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
                  }}
                  className="btn btn-secondary"
                  disabled={changingPassword}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={changingPassword}
                  style={{ gap: '0.5rem' }}
                >
                  {changingPassword ? (
                    <>
                      <Loader size={16} className="animate-spin" style={{ animation: 'spin 1s linear infinite' }} />
                      Updating...
                    </>
                  ) : (
                    'Change Password'
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Profile;
