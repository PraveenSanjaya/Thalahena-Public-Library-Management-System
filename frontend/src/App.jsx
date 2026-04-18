import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import ForgotPassword from './pages/ForgotPassword';
import AccessDenied from './pages/AccessDenied';
import DashboardLayout from './components/Layout/DashboardLayout';
import AdminDashboard from './pages/Admin/AdminDashboard';
import StaffDashboard from './pages/Staff/StaffDashboard';
import MemberDashboard from './pages/Member/MemberDashboard';
import Profile from './pages/Profile';
import UserManagement from './pages/Admin/UserManagement';
import MemberManagement from './pages/Admin/MemberManagement';
import RegistrationManagement from './pages/Admin/RegistrationManagement';
import AboutManagement from './pages/Admin/AboutManagement';
import BookManagement from './pages/Staff/BookManagement';
import AuthorManagement from './pages/Staff/AuthorManagement';
import TransactionManagement from './pages/Staff/TransactionManagement';
import StaffMemberManagement from './pages/Staff/MemberManagement';
import FineManagement from './pages/Staff/FineManagement';
import ReservationManagement from './pages/Staff/ReservationManagement';
import NotificationManagement from './pages/Staff/NotificationManagement';
import BookHistory from './pages/Member/BookHistory';
import Reservations from './pages/Member/Reservations';
import Feedback from './pages/Member/Feedback';
import authService from './services/auth.service';
import api from './services/api';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const user = authService.getCurrentUser();
  
  // Debug: Log user data to console
  console.log('=== ProtectedRoute Debug ===');
  console.log('User from localStorage:', user);
  console.log('User role:', user?.role);
  console.log('User role type:', typeof user?.role);
  console.log('Allowed roles:', allowedRoles);
  console.log('Has token:', !!user?.token);
  console.log('===========================');
  
  // If no user/token, redirect to login
  if (!user || !user.token) {
    console.log('❌ No user or token found, redirecting to login');
    return <Navigate to="/login" replace />;
  }
  
  // If roles are specified and user doesn't have required role
  if (allowedRoles && allowedRoles.length > 0) {
    const userRole = user.role;
    const hasAccess = allowedRoles.includes(userRole);
    
    console.log('🔍 Role check:');
    console.log('  User role:', userRole);
    console.log('  Required roles:', allowedRoles);
    console.log('  Has access:', hasAccess);
    
    if (!hasAccess) {
      console.log('❌ Access denied! Role mismatch');
      return <Navigate to="/access-denied" replace />;
    }
  }
  
  console.log('✅ Access granted');
  return children;
};

function App() {
  const user = authService.getCurrentUser();
  
  const getDefaultRoute = () => {
    if (!user || !user.token) return <Navigate to="/login" />;
    if (user.role === 'ADMIN') return <Navigate to="/admin" />;
    if (user.role === 'STAFF') return <Navigate to="/staff" />;
    if (user.role === 'MEMBER') return <Navigate to="/member" />;
    return <Navigate to="/login" />;
  };

  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/access-denied" element={<AccessDenied />} />
        
        <Route path="/" element={getDefaultRoute()} />

        {/* Admin Routes */}
        <Route path="/admin" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout><AdminDashboard /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/users" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout><UserManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/members" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout><MemberManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/registrations" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout><RegistrationManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/admin/about" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout><AboutManagement /></DashboardLayout>
          </ProtectedRoute>
        } />

        {/* Staff Routes */}
        <Route path="/staff" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><StaffDashboard /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/members" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><StaffMemberManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/books" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><BookManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/authors" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><AuthorManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/transactions" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><TransactionManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/fines" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><FineManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/reservations" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><ReservationManagement /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/staff/notifications" element={
          <ProtectedRoute allowedRoles={['STAFF', 'ADMIN']}>
            <DashboardLayout><NotificationManagement /></DashboardLayout>
          </ProtectedRoute>
        } />

        {/* Member Routes */}
        <Route path="/member" element={
          <ProtectedRoute allowedRoles={['MEMBER']}>
            <DashboardLayout><MemberDashboard /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/member/history" element={
          <ProtectedRoute allowedRoles={['MEMBER']}>
            <DashboardLayout><BookHistory /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/member/reservations" element={
          <ProtectedRoute allowedRoles={['MEMBER']}>
            <DashboardLayout><Reservations /></DashboardLayout>
          </ProtectedRoute>
        } />
        <Route path="/member/feedback" element={
          <ProtectedRoute allowedRoles={['MEMBER']}>
            <DashboardLayout><Feedback /></DashboardLayout>
          </ProtectedRoute>
        } />

        {/* Common Routes */}
        <Route path="/profile" element={
          <ProtectedRoute>
            <DashboardLayout><Profile /></DashboardLayout>
          </ProtectedRoute>
        } />
      </Routes>
    </Router>
  );
}

export default App;
