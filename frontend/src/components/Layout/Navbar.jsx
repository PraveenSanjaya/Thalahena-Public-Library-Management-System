import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../../services/auth.service';
import api from '../../services/api';
import { Bell, User, Printer, FileDown, Check, X } from 'lucide-react';

const Navbar = () => {
  const user = authService.getCurrentUser();
  const navigate = useNavigate();
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loadingNotifs, setLoadingNotifs] = useState(false);
  const dropdownRef = useRef(null);

  // Fetch notifications on mount and periodically
  useEffect(() => {
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 30000); // refresh every 30s
    return () => clearInterval(interval);
  }, []);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setShowNotifications(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await api.get('/notifications');
      const data = response.data;
      setNotifications(data);
      setUnreadCount(data.filter(n => !n.isRead).length);
    } catch (error) {
      // Silently fail — members may not have access to /notifications
      // They'll see 0 notifications which is fine
      console.log('Could not fetch notifications:', error?.response?.status);
    }
  };

  const handleMarkAsRead = async (notifId, e) => {
    e.stopPropagation();
    try {
      await api.put(`/notifications/${notifId}/read`);
      fetchNotifications();
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleBellClick = () => {
    setShowNotifications(!showNotifications);
    if (!showNotifications) {
      fetchNotifications();
    }
  };

  const handleViewAll = () => {
    setShowNotifications(false);
    const role = user?.role;
    if (role === 'ADMIN' || role === 'STAFF') {
      navigate('/staff/notifications');
    } else {
      navigate('/member/notifications');
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const handleDownloadPDF = () => {
    const originalTitle = document.title;
    document.title = 'Thalahena_Library_Report';
    window.print();
    document.title = originalTitle;
  };

  const getTimeAgo = (dateStr) => {
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    const diffHrs = Math.floor(diffMins / 60);
    if (diffHrs < 24) return `${diffHrs}h ago`;
    const diffDays = Math.floor(diffHrs / 24);
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      {/* Action Buttons */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <button
          onClick={handlePrint}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '0.5rem 1.1rem',
            background: 'var(--primary)',
            color: '#ffffff',
            border: 'none',
            borderRadius: '0.5rem',
            fontWeight: 600,
            fontSize: '0.875rem',
            cursor: 'pointer',
            transition: 'all 0.2s ease',
            fontFamily: 'Outfit, sans-serif',
            boxShadow: '0 2px 8px rgba(99,102,241,0.25)',
          }}
          onMouseEnter={e => {
            e.currentTarget.style.background = 'var(--primary-hover)';
            e.currentTarget.style.transform = 'translateY(-1px)';
            e.currentTarget.style.boxShadow = '0 4px 14px rgba(99,102,241,0.35)';
          }}
          onMouseLeave={e => {
            e.currentTarget.style.background = 'var(--primary)';
            e.currentTarget.style.transform = 'translateY(0)';
            e.currentTarget.style.boxShadow = '0 2px 8px rgba(99,102,241,0.25)';
          }}
        >
          <Printer size={16} />
          Print Report
        </button>

        <button
          onClick={handleDownloadPDF}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '0.5rem 1.1rem',
            background: '#ffffff',
            color: 'var(--primary)',
            border: '1.5px solid var(--primary)',
            borderRadius: '0.5rem',
            fontWeight: 600,
            fontSize: '0.875rem',
            cursor: 'pointer',
            transition: 'all 0.2s ease',
            fontFamily: 'Outfit, sans-serif',
          }}
          onMouseEnter={e => {
            e.currentTarget.style.background = 'rgba(99,102,241,0.06)';
            e.currentTarget.style.transform = 'translateY(-1px)';
            e.currentTarget.style.boxShadow = '0 4px 12px rgba(99,102,241,0.15)';
          }}
          onMouseLeave={e => {
            e.currentTarget.style.background = '#ffffff';
            e.currentTarget.style.transform = 'translateY(0)';
            e.currentTarget.style.boxShadow = 'none';
          }}
        >
          <FileDown size={16} />
          Download PDF
        </button>
      </div>

      {/* Right Side: Bell + User */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        {/* Bell Icon with Dropdown */}
        <div ref={dropdownRef} style={{ position: 'relative' }}>
          <div
            onClick={handleBellClick}
            style={{
              position: 'relative',
              cursor: 'pointer',
              padding: '0.5rem',
              borderRadius: '0.5rem',
              transition: 'background 0.2s',
              background: showNotifications ? 'var(--bg-secondary)' : 'transparent',
            }}
            onMouseEnter={e => { e.currentTarget.style.background = 'var(--bg-secondary)'; }}
            onMouseLeave={e => { if (!showNotifications) e.currentTarget.style.background = 'transparent'; }}
          >
            <Bell size={20} color="var(--text-muted)" />
            {unreadCount > 0 && (
              <span style={{
                position: 'absolute',
                top: '2px',
                right: '2px',
                background: 'var(--danger)',
                color: 'white',
                fontSize: '0.6rem',
                fontWeight: 700,
                minWidth: '16px',
                height: '16px',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                border: '2px solid white',
              }}>
                {unreadCount > 9 ? '9+' : unreadCount}
              </span>
            )}
          </div>

          {/* Notification Dropdown */}
          {showNotifications && (
            <div style={{
              position: 'absolute',
              top: 'calc(100% + 8px)',
              right: 0,
              width: '380px',
              maxHeight: '480px',
              background: 'var(--card-dark)',
              border: '1px solid var(--border-light)',
              borderRadius: '1rem',
              boxShadow: '0 10px 40px rgba(0,0,0,0.15)',
              zIndex: 1000,
              overflow: 'hidden',
              animation: 'fadeInDown 0.2s ease',
            }}>
              {/* Header */}
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '1rem 1.25rem',
                borderBottom: '1px solid var(--border-light)',
              }}>
                <div>
                  <h3 style={{ fontSize: '1rem', fontWeight: 700, margin: 0 }}>Notifications</h3>
                  {unreadCount > 0 && (
                    <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', margin: 0 }}>
                      {unreadCount} unread
                    </p>
                  )}
                </div>
                <button
                  onClick={() => setShowNotifications(false)}
                  style={{
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    padding: '0.25rem',
                    color: 'var(--text-muted)',
                  }}
                >
                  <X size={18} />
                </button>
              </div>

              {/* Notification Items */}
              <div style={{ maxHeight: '340px', overflowY: 'auto' }}>
                {notifications.length > 0 ? (
                  notifications.slice(0, 8).map((notif) => (
                    <div
                      key={notif.id}
                      style={{
                        padding: '0.875rem 1.25rem',
                        borderBottom: '1px solid var(--border-light)',
                        background: notif.isRead ? 'transparent' : 'rgba(99,102,241,0.04)',
                        cursor: 'default',
                        transition: 'background 0.15s',
                      }}
                      onMouseEnter={e => { e.currentTarget.style.background = 'var(--bg-secondary)'; }}
                      onMouseLeave={e => { e.currentTarget.style.background = notif.isRead ? 'transparent' : 'rgba(99,102,241,0.04)'; }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '0.75rem' }}>
                        <div style={{ flex: 1, minWidth: 0 }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                            {!notif.isRead && (
                              <span style={{
                                width: '6px',
                                height: '6px',
                                borderRadius: '50%',
                                background: 'var(--primary)',
                                flexShrink: 0,
                              }} />
                            )}
                            <span style={{
                              padding: '0.15rem 0.5rem',
                              borderRadius: '0.25rem',
                              fontSize: '0.65rem',
                              fontWeight: 600,
                              background: notif.type === 'GENERAL' ? 'rgba(99,102,241,0.1)' :
                                         notif.type === 'DUE_DATE' ? 'rgba(245,158,11,0.1)' :
                                         notif.type === 'OVERDUE' ? 'rgba(239,68,68,0.1)' :
                                         'rgba(16,185,129,0.1)',
                              color: notif.type === 'GENERAL' ? 'var(--primary)' :
                                     notif.type === 'DUE_DATE' ? '#f59e0b' :
                                     notif.type === 'OVERDUE' ? 'var(--danger)' :
                                     'var(--success)',
                            }}>
                              {notif.type}
                            </span>
                            <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                              {getTimeAgo(notif.createdAt)}
                            </span>
                          </div>
                          <p style={{
                            fontSize: '0.8rem',
                            lineHeight: 1.4,
                            margin: 0,
                            color: 'var(--text-main)',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                          }}>
                            {notif.message}
                          </p>
                          <p style={{ fontSize: '0.7rem', color: 'var(--text-muted)', margin: '0.15rem 0 0 0' }}>
                            To: {notif.userName || 'Everyone'}
                          </p>
                        </div>
                        {!notif.isRead && (
                          <button
                            onClick={(e) => handleMarkAsRead(notif.id, e)}
                            title="Mark as read"
                            style={{
                              background: 'none',
                              border: 'none',
                              cursor: 'pointer',
                              padding: '0.25rem',
                              color: 'var(--success)',
                              flexShrink: 0,
                            }}
                          >
                            <Check size={14} />
                          </button>
                        )}
                      </div>
                    </div>
                  ))
                ) : (
                  <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
                    <Bell size={32} color="var(--text-muted)" style={{ marginBottom: '0.5rem', opacity: 0.5 }} />
                    <p style={{ fontSize: '0.85rem', margin: 0 }}>No notifications</p>
                  </div>
                )}
              </div>

              {/* Footer */}
              <div style={{
                padding: '0.75rem 1.25rem',
                borderTop: '1px solid var(--border-light)',
                textAlign: 'center',
              }}>
                <button
                  onClick={handleViewAll}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: 'var(--primary)',
                    cursor: 'pointer',
                    fontWeight: 600,
                    fontSize: '0.85rem',
                    fontFamily: 'Outfit, sans-serif',
                  }}
                  onMouseEnter={e => { e.currentTarget.style.textDecoration = 'underline'; }}
                  onMouseLeave={e => { e.currentTarget.style.textDecoration = 'none'; }}
                >
                  View All Notifications
                </button>
              </div>
            </div>
          )}
        </div>

        <div 
          onClick={() => navigate('/profile')}
          style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', cursor: 'pointer' }}
          onMouseEnter={e => { e.currentTarget.style.opacity = 0.85; }}
          onMouseLeave={e => { e.currentTarget.style.opacity = 1; }}
        >
          <div style={{ textAlign: 'right' }}>
            <p style={{ fontWeight: 600, fontSize: '0.875rem' }}>{user?.username}</p>
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user?.role || user?.roles?.[0] || 'Guest'}</p>
          </div>
          <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
            {user?.profilePicture ? (
              <img 
                src={user.profilePicture.startsWith('http') ? user.profilePicture : `http://localhost:8081${user.profilePicture}`} 
                alt="Avatar" 
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
              />
            ) : (
              <User size={20} color="#fff" />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Navbar;
