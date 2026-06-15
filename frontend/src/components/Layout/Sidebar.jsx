import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Users, UserCheck, BookOpen, UserCircle, PenTool, History, BookmarkPlus, MessageSquare, LogOut, Library, Bell, DollarSign, FileText } from 'lucide-react';
import authService from '../../services/auth.service';

const Sidebar = () => {
  const user = authService.getCurrentUser();
  const role = user?.role || 'GUEST';

  const adminLinks = [
    { name: 'Dashboard', path: '/admin', icon: <LayoutDashboard size={20} /> },
    { name: 'Members', path: '/admin/members', icon: <Users size={20} /> },
    { name: 'Registrations', path: '/admin/registrations', icon: <UserCheck size={20} /> },
    { name: 'About Statements', path: '/admin/about', icon: <FileText size={20} /> },
  ];

  const staffLinks = [
    { name: 'Dashboard', path: '/staff', icon: <LayoutDashboard size={20} /> },
    { name: 'Manage Members', path: '/staff/members', icon: <Users size={20} /> },
    { name: 'Books', path: '/staff/books', icon: <BookOpen size={20} /> },
    { name: 'Authors', path: '/staff/authors', icon: <PenTool size={20} /> },
    { name: 'Borrow & Return', path: '/staff/transactions', icon: <History size={20} /> },
    { name: 'Fines', path: '/staff/fines', icon: <DollarSign size={20} /> },
    { name: 'Reservations', path: '/staff/reservations', icon: <BookmarkPlus size={20} /> },
    { name: 'Notifications', path: '/staff/notifications', icon: <Bell size={20} /> },
    { name: 'Feedback', path: '/feedback', icon: <MessageSquare size={20} /> },
    { name: 'About', path: '/about', icon: <FileText size={20} /> },
  ];

  const memberLinks = [
    { name: 'Dashboard', path: '/member', icon: <LayoutDashboard size={20} /> },
    { name: 'Book History', path: '/member/history', icon: <History size={20} /> },
    { name: 'My Reservations', path: '/member/reservations', icon: <BookmarkPlus size={20} /> },
    { name: 'Notifications', path: '/member/notifications', icon: <Bell size={20} /> },
    { name: 'Feedback', path: '/feedback', icon: <MessageSquare size={20} /> },
    { name: 'Additional Service', path: '/member/additional-service', icon: <BookOpen size={20} /> },
    { name: 'About', path: '/about', icon: <FileText size={20} /> },
  ];

  const links = role === 'ADMIN' 
    ? [...adminLinks, { divider: true }, ...staffLinks] 
    : role === 'STAFF' 
      ? staffLinks 
      : memberLinks;

  return (
    <div className="sidebar">
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '2.5rem', paddingLeft: '0.5rem' }}>
        <Library className="text-primary" size={32} style={{ flexShrink: 0 }} />
        <h2 style={{ fontSize: '1.05rem', fontWeight: 700, lineHeight: 1.2 }}>Thalahena Public Library</h2>
      </div>

      <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        {links.map((link, index) => {
          if (link.divider) {
            return (
              <div
                key={`divider-${index}`}
                style={{
                  marginTop: '1rem',
                  paddingTop: '1rem',
                  borderTop: '1px solid rgba(255,255,255,0.1)',
                  marginBottom: '0.5rem'
                }}
              />
            );
          }
          return (
            <NavLink
              key={`${link.path}-${index}`}
              to={link.path}
              style={({ isActive }) => ({
                display: 'flex',
                alignItems: 'center',
                gap: '0.75rem',
                padding: '0.75rem 1rem',
                borderRadius: '0.5rem',
                textDecoration: 'none',
                color: isActive ? 'white' : 'var(--text-muted)',
                background: isActive ? 'var(--primary)' : 'transparent',
                fontWeight: 500,
                transition: 'all 0.2s',
              })}
            >
              {link.icon}
              {link.name}
            </NavLink>
          );
        })}

        <div style={{ marginTop: '2rem', paddingTop: '2rem', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
          <NavLink
            to="/profile"
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: '0.75rem 1rem',
              borderRadius: '0.5rem',
              textDecoration: 'none',
              color: isActive ? 'white' : 'var(--text-muted)',
              background: isActive ? 'var(--primary)' : 'transparent',
              fontWeight: 500,
            })}
          >
            <UserCircle size={20} />
            My Profile
          </NavLink>

          <button
            onClick={() => { authService.logout(); window.location.href = '/login'; }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: '0.75rem 1rem',
              marginTop: '0.5rem',
              width: '100%',
              background: 'transparent',
              border: 'none',
              color: 'var(--danger)',
              cursor: 'pointer',
              fontWeight: 500,
              textAlign: 'left'
            }}
          >
            <LogOut size={20} />
            Logout
          </button>
        </div>
      </nav>
    </div>
  );
};

export default Sidebar;
