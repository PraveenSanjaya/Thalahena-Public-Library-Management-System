import React, { useEffect, useState } from 'react';
import { Bookmark, CheckCircle, Clock, AlertTriangle, XCircle, RefreshCw } from 'lucide-react';
import api from '../../services/api';

const ReservationManagement = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState({});
  const [statusUpdating, setStatusUpdating] = useState({});

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      setLoading(true);
      const response = await api.get('/staff/reservations');
      setReservations(response.data);
    } catch (error) {
      console.error('Error fetching reservations:', error);
      alert('Error fetching reservations');
    } finally {
      setLoading(false);
    }
  };

  const handleAcknowledge = async (reservationId) => {
    if (processing[reservationId]) return;
    setProcessing({ ...processing, [reservationId]: true });

    try {
      await api.patch(`/staff/reservations/${reservationId}/acknowledge`);
      setReservations(reservations.map(r =>
        r.id === reservationId ? { ...r, processed: true } : r
      ));
    } catch (error) {
      console.error('Error acknowledging reservation:', error);
      const message = error.response?.data?.message || 'Error acknowledging reservation';
      alert(message);
      setProcessing({ ...processing, [reservationId]: false });
    }
  };

  const handleStatusChange = async (reservationId, newStatus) => {
    if (statusUpdating[reservationId]) return;
    setStatusUpdating({ ...statusUpdating, [reservationId]: true });

    try {
      const response = await api.patch(`/staff/reservations/${reservationId}/status`, {
        status: newStatus
      });
      // Update the row with the response from server (includes updated status/processed)
      setReservations(reservations.map(r =>
        r.id === reservationId ? { ...r, ...response.data } : r
      ));
    } catch (error) {
      console.error('Error updating reservation status:', error);
      const message = error.response?.data?.message || `Failed to update status to ${newStatus}`;
      alert(message);
    } finally {
      setStatusUpdating({ ...statusUpdating, [reservationId]: false });
    }
  };

  const isExpired = (reservation) => {
    if (!reservation.expiryDate) return false;
    return new Date(reservation.expiryDate) < new Date();
  };

  const isFinal = (reservation) => {
    return reservation.status === 'COMPLETED' ||
           reservation.status === 'CANCELLED' ||
           reservation.status === 'REJECTED' ||
           reservation.processed === true;
  };

  const getStatusBadge = (reservation) => {
    // Expired takes priority
    if (isExpired(reservation) && reservation.status === 'PENDING') {
      return <span className="badge badge-danger" style={{ fontSize: '0.75rem' }}>Expired</span>;
    }

    const colorMap = {
      PENDING: 'badge-warning',
      APPROVED: 'badge-success',
      AVAILABLE: 'badge-info',
      UNAVAILABLE: 'badge-danger',
      REJECTED: 'badge-secondary',
      COMPLETED: 'badge-success',
      CANCELLED: 'badge-secondary'
    };

    return (
      <span className={`badge ${colorMap[reservation.status] || 'badge-secondary'}`} style={{ fontSize: '0.75rem' }}>
        {reservation.status || 'PENDING'}
      </span>
    );
  };

  const getActionOptions = () => {
    return [
      { value: 'APPROVED', label: 'Approve (Auto-Issue)', icon: '✅' },
      { value: 'REJECTED', label: 'Reject', icon: '❌' },
      { value: 'AVAILABLE', label: 'Mark Available', icon: '📗' },
      { value: 'UNAVAILABLE', label: 'Mark Unavailable', icon: '📕' },
    ];
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading reservations...</div>;
  }

  const total = reservations.length;
  const processed = reservations.filter(r => r.processed).length;
  const pending = reservations.filter(r => !r.processed).length;
  const expired = reservations.filter(r => isExpired(r) && r.status === 'PENDING').length;

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Reservation Management</h1>
          <p style={{ color: 'var(--text-muted)', margin: 0 }}>
            Review, approve, or reject member reservations
          </p>
        </div>
        <button
          className="btn-primary"
          onClick={fetchReservations}
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <RefreshCw size={16} /> Refresh
        </button>
      </div>

      {/* Info Card */}
      <div
        className="premium-card"
        style={{
          marginBottom: '1.5rem',
          padding: '1rem',
          background: 'rgba(59, 130, 246, 0.08)',
          borderLeft: '4px solid var(--primary)',
          display: 'flex',
          alignItems: 'flex-start',
          gap: '0.75rem'
        }}
      >
        <Bookmark size={20} color="var(--primary)" style={{ marginTop: 2 }} />
        <div>
          <p style={{ margin: 0, fontWeight: 600, marginBottom: '0.25rem' }}>Workflow Actions</p>
          <p style={{ margin: 0, fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
            <strong>Approve</strong> = auto-issues book (creates borrow transaction, marks COMPLETED).{' '}
            <strong>Reject</strong> = cancels reservation.{' '}
            Reservations pending for more than <strong>3 days</strong> auto-expire.
          </p>
        </div>
      </div>

      {/* Table */}
      <div className="premium-card" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
              <th style={{ ...thStyle, width: '50px' }}>Ack</th>
              <th style={thStyle}>ID</th>
              <th style={thStyle}>Member</th>
              <th style={thStyle}>Book</th>
              <th style={thStyle}>Reserved</th>
              <th style={thStyle}>Expiry</th>
              <th style={thStyle}>Status</th>
              <th style={thStyle}>Processed</th>
              <th style={{ ...thStyle, minWidth: '180px' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {reservations.length > 0 ? (
              reservations.map((r) => {
                const expired = isExpired(r) && r.status === 'PENDING';
                const final = isFinal(r);
                const updating = statusUpdating[r.id] || false;

                return (
                  <tr
                    key={r.id}
                    style={{
                      borderBottom: '1px solid var(--border-color)',
                      opacity: final ? 0.55 : 1,
                      background: final ? 'rgba(0,0,0,0.02)' : 'transparent'
                    }}
                  >
                    {/* Acknowledge checkbox */}
                    <td style={{ ...tdStyle, textAlign: 'center' }}>
                      <input
                        type="checkbox"
                        checked={r.processed || false}
                        onChange={() => handleAcknowledge(r.id)}
                        disabled={r.processed || processing[r.id]}
                        style={{ width: 18, height: 18, cursor: r.processed ? 'not-allowed' : 'pointer', accentColor: 'var(--success)' }}
                      />
                    </td>

                    {/* ID */}
                    <td style={{ ...tdStyle, fontWeight: 600 }}>#{r.id}</td>

                    {/* Member */}
                    <td style={tdStyle}>
                      <div style={{ fontWeight: 500 }}>{r.memberName || '-'}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{r.memberEmail || ''}</div>
                    </td>

                    {/* Book */}
                    <td style={tdStyle}>
                      <div style={{ fontWeight: 500 }}>{r.bookTitle || '-'}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>ISBN: {r.bookIsbn || 'N/A'}</div>
                    </td>

                    {/* Reserved date */}
                    <td style={tdStyle}>
                      {r.reservationDate
                        ? new Date(r.reservationDate).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })
                        : '-'}
                    </td>

                    {/* Expiry date */}
                    <td style={tdStyle}>
                      {r.expiryDate ? (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                          <span style={{ fontSize: '0.85rem' }}>
                            {new Date(r.expiryDate).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                          </span>
                          {expired && (
                            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, fontSize: '0.75rem', color: 'var(--danger, #dc3545)', fontWeight: 600 }}>
                              <AlertTriangle size={12} /> Expired
                            </span>
                          )}
                        </div>
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>-</span>
                      )}
                    </td>

                    {/* Status badge */}
                    <td style={tdStyle}>{getStatusBadge(r)}</td>

                    {/* Processed */}
                    <td style={tdStyle}>
                      {r.processed ? (
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--success)', fontWeight: 600 }}>
                          <CheckCircle size={15} /> Yes
                        </span>
                      ) : (
                        <span style={{ color: 'var(--text-muted)' }}>No</span>
                      )}
                    </td>

                    {/* Actions dropdown */}
                    <td style={tdStyle}>
                      <select
                        value=""
                        onChange={(e) => handleStatusChange(r.id, e.target.value)}
                        disabled={final || expired || updating}
                        style={{
                          padding: '0.4rem 0.6rem',
                          borderRadius: '0.375rem',
                          border: '1px solid var(--border-color)',
                          background: 'var(--bg-secondary)',
                          color: 'var(--text-primary)',
                          fontSize: '0.8rem',
                          cursor: final || expired || updating ? 'not-allowed' : 'pointer',
                          opacity: final || expired ? 0.5 : 1,
                          width: '100%'
                        }}
                      >
                        <option value="" disabled>
                          {updating ? 'Updating...' : 'Select action...'}
                        </option>
                        {getActionOptions().map((opt) => (
                          <option key={opt.value} value={opt.value}>
                            {opt.icon} {opt.label}
                          </option>
                        ))}
                      </select>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan="9" style={{ ...tdStyle, textAlign: 'center', padding: '3rem' }}>
                  <p style={{ color: 'var(--text-muted)' }}>No reservations found</p>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Summary counters */}
      <div
        style={{
          marginTop: '1.5rem',
          padding: '1rem',
          background: 'var(--bg-secondary)',
          borderRadius: '0.5rem'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-around', textAlign: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.25rem' }}>Total</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0 }}>{total}</h3>
          </div>
          <div>
            <p style={{ color: 'var(--success)', fontSize: '0.875rem', marginBottom: '0.25rem' }}>Processed</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0, color: 'var(--success)' }}>{processed}</h3>
          </div>
          <div>
            <p style={{ color: 'var(--warning)', fontSize: '0.875rem', marginBottom: '0.25rem' }}>Pending</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0, color: 'var(--warning)' }}>{pending}</h3>
          </div>
          <div>
            <p style={{ color: 'var(--danger, #dc3545)', fontSize: '0.875rem', marginBottom: '0.25rem' }}>Expired</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0, color: 'var(--danger, #dc3545)' }}>{expired}</h3>
          </div>
        </div>
      </div>
    </div>
  );
};

const thStyle = {
  textAlign: 'left',
  padding: '0.75rem 1rem',
  color: 'var(--text-muted)',
  fontSize: '0.75rem',
  fontWeight: 600,
  whiteSpace: 'nowrap'
};

const tdStyle = {
  padding: '0.75rem 1rem',
  fontSize: '0.875rem'
};

export default ReservationManagement;
