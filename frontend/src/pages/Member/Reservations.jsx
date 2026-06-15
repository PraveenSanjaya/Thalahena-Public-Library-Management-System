import React, { useEffect, useState } from 'react';
import { Bookmark, Clock, CheckCircle2, AlertCircle, XCircle, AlertTriangle } from 'lucide-react';
import api from '../../services/api';
import authService from '../../services/auth.service';

const Reservations = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const user = authService.getCurrentUser();

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      const response = await api.get(`/reservations/user/${user.userId}`);
      setReservations(response.data);
    } catch (error) {
      console.error('Error fetching reservations:', error);
    } finally {
      setLoading(false);
    }
  };

  const isExpired = (res) => {
    if (!res.expiryDate) return false;
    return new Date(res.expiryDate) < new Date() && res.status === 'PENDING';
  };

  const getStaffActionLabel = (res) => {
    if (isExpired(res)) return { label: 'Expired', icon: <AlertTriangle size={16} />, color: 'var(--danger, #dc3545)' };
    switch (res.status) {
      case 'APPROVED': return { label: 'Approved', icon: <CheckCircle2 size={16} />, color: 'var(--success)' };
      case 'COMPLETED': return { label: 'Book Issued', icon: <CheckCircle2 size={16} />, color: 'var(--success)' };
      case 'REJECTED': return { label: 'Rejected', icon: <XCircle size={16} />, color: 'var(--danger, #dc3545)' };
      case 'CANCELLED': return { label: 'Cancelled', icon: <XCircle size={16} />, color: 'var(--text-muted)' };
      case 'AVAILABLE': return { label: 'Available', icon: <CheckCircle2 size={16} />, color: 'var(--success)' };
      case 'UNAVAILABLE': return { label: 'Unavailable', icon: <AlertCircle size={16} />, color: 'var(--warning)' };
      default: return res.processed
        ? { label: 'Acknowledged', icon: <CheckCircle2 size={16} />, color: 'var(--success)' }
        : { label: 'Pending Review', icon: <Clock size={16} />, color: 'orange' };
    }
  };

  const getStatusBadge = (res) => {
    if (isExpired(res)) return { text: 'EXPIRED', className: 'badge-danger' };
    const map = {
      PENDING: { text: 'PENDING', className: 'badge-warning' },
      APPROVED: { text: 'APPROVED', className: 'badge-success' },
      COMPLETED: { text: 'COMPLETED', className: 'badge-success' },
      REJECTED: { text: 'REJECTED', className: 'badge-danger' },
      CANCELLED: { text: 'CANCELLED', className: 'badge-secondary' },
      AVAILABLE: { text: 'AVAILABLE', className: 'badge-info' },
      UNAVAILABLE: { text: 'UNAVAILABLE', className: 'badge-danger' }
    };
    return map[res.status] || { text: res.status || 'PENDING', className: 'badge-secondary' };
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <div style={{ color: 'var(--primary)', fontWeight: 600 }}>Loading reservations...</div>
      </div>
    );
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Bookmark className="text-primary" size={24} />
          My Reservations
        </h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Track the status of books you have reserved. Reservations expire after 3 days if not approved.
        </p>
      </div>

      <div className="premium-card" style={{ padding: 0, overflow: 'hidden' }}>
        {reservations.length > 0 ? (
          <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ textAlign: 'left', borderBottom: '1px solid var(--border-light)' }}>
                <th style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontWeight: 600 }}>Book Title</th>
                <th style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontWeight: 600 }}>ISBN</th>
                <th style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontWeight: 600 }}>Reserved</th>
                <th style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontWeight: 600 }}>Expires</th>
                <th style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontWeight: 600 }}>Staff Action</th>
                <th style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontWeight: 600 }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {reservations.map((res) => {
                const action = getStaffActionLabel(res);
                const badge = getStatusBadge(res);
                return (
                  <tr key={res.id} style={{ borderBottom: '1px solid var(--border-light)', transition: 'background-color 0.2s' }}>
                    <td style={{ padding: '1rem 1.5rem', fontWeight: 500 }}>{res.bookTitle}</td>
                    <td style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)' }}>{res.bookIsbn}</td>
                    <td style={{ padding: '1rem 1.5rem' }}>
                      {new Date(res.reservationDate).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                    </td>
                    <td style={{ padding: '1rem 1.5rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                      {res.expiryDate
                        ? new Date(res.expiryDate).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit' })
                        : '-'}
                    </td>
                    <td style={{ padding: '1rem 1.5rem' }}>
                      <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.25rem', color: action.color, fontSize: '0.85rem', fontWeight: 500 }}>
                        {action.icon} {action.label}
                      </span>
                    </td>
                    <td style={{ padding: '1rem 1.5rem' }}>
                      <span className={`badge ${badge.className}`} style={{ fontSize: '0.75rem' }}>
                        {badge.text}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        ) : (
          <div style={{ textAlign: 'center', padding: '4rem 2rem', color: 'var(--text-muted)' }}>
            <Bookmark size={40} style={{ opacity: 0.3, marginBottom: '1rem', color: 'var(--primary)' }} />
            <p style={{ fontWeight: 500, fontSize: '1rem', marginBottom: '0.25rem' }}>No reservations found</p>
            <p style={{ fontSize: '0.85rem' }}>You can reserve books directly from the Dashboard catalogue.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Reservations;
