import React, { useEffect, useState } from 'react';
import { Bell, Check } from 'lucide-react';
import api from '../../services/api';

const MemberNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const response = await api.get('/notifications');
      setNotifications(response.data);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      await api.put(`/notifications/${notificationId}/read`);
      fetchNotifications();
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await api.put('/notifications/read-all');
      fetchNotifications();
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>My Notifications</h1>
          <p style={{ color: 'var(--text-muted)' }}>
            {unreadCount > 0 ? `${unreadCount} unread notifications` : 'All caught up!'}
          </p>
        </div>
        {unreadCount > 0 && (
          <button onClick={handleMarkAllAsRead} className="btn btn-secondary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Check size={20} />
            Mark All Read
          </button>
        )}
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {notifications.length > 0 ? (
          notifications.map((notification) => (
            <div
              key={notification.id}
              className="premium-card"
              style={{
                borderLeft: notification.isRead ? '3px solid var(--border-color)' : '3px solid var(--primary)',
                opacity: notification.isRead ? 0.7 : 1
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
                    <Bell size={20} color={!notification.isRead ? 'var(--primary)' : 'var(--text-muted)'} />
                    <span style={{
                      padding: '0.25rem 0.75rem',
                      borderRadius: '0.25rem',
                      background: notification.type === 'GENERAL' ? 'var(--primary)' :
                                 notification.type === 'DUE_DATE' ? '#f59e0b' :
                                 notification.type === 'OVERDUE' ? 'var(--danger)' :
                                 'var(--success)',
                      color: 'white',
                      fontSize: '0.75rem',
                      fontWeight: 600
                    }}>
                      {notification.type}
                    </span>
                    {!notification.isRead && (
                      <span style={{
                        padding: '0.25rem 0.5rem',
                        borderRadius: '0.25rem',
                        background: 'var(--success)',
                        color: 'white',
                        fontSize: '0.65rem',
                        fontWeight: 600
                      }}>
                        NEW
                      </span>
                    )}
                  </div>
                  <p style={{ lineHeight: 1.6, marginBottom: '0.75rem' }}>{notification.message}</p>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    <span>{notification.userName ? `From Staff` : 'Public Notification'}</span>
                    <span>{new Date(notification.createdAt).toLocaleString()}</span>
                  </div>
                </div>
                {!notification.isRead && (
                  <div style={{ marginLeft: '1rem' }}>
                    <button
                      onClick={() => handleMarkAsRead(notification.id)}
                      className="btn btn-sm btn-success"
                      title="Mark as Read"
                    >
                      <Check size={16} />
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))
        ) : (
          <div className="premium-card" style={{ textAlign: 'center', padding: '3rem' }}>
            <Bell size={48} color="var(--text-muted)" style={{ marginBottom: '1rem' }} />
            <p style={{ color: 'var(--text-muted)' }}>No notifications yet</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default MemberNotifications;
