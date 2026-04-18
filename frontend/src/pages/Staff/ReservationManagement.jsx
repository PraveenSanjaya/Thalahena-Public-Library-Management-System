import React, { useEffect, useState } from 'react';
import { Bookmark, CheckCircle } from 'lucide-react';
import api from '../../services/api';

const ReservationManagement = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState({});

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
    // Prevent double-clicking
    if (processing[reservationId]) return;

    setProcessing({ ...processing, [reservationId]: true });

    try {
      await api.patch(`/staff/reservations/${reservationId}/acknowledge`);
      
      // Update local state immediately
      setReservations(reservations.map(r => 
        r.id === reservationId ? { ...r, processed: true } : r
      ));
    } catch (error) {
      console.error('Error acknowledging reservation:', error);
      const message = error.response?.data?.message || 'Error acknowledging reservation';
      alert(message);
      
      // Reset processing state on error
      setProcessing({ ...processing, [reservationId]: false });
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'AVAILABLE': return 'badge-success';
      case 'UNAVAILABLE': return 'badge-danger';
      case 'PENDING': return 'badge-warning';
      case 'COMPLETED': return 'badge-info';
      case 'CANCELLED': return 'badge-secondary';
      default: return 'badge-secondary';
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Reservation Management</h1>
        <p style={{ color: 'var(--text-muted)' }}>View and acknowledge member reservations</p>
      </div>

      {/* Info Card */}
      <div className="premium-card" style={{ marginBottom: '1.5rem', padding: '1rem', background: 'rgba(59, 130, 246, 0.1)', borderLeft: '4px solid var(--primary)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Bookmark size={20} color="var(--primary)" />
          <div>
            <p style={{ margin: 0, fontWeight: 600 }}>Read-Only View</p>
            <p style={{ margin: 0, fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              Check the box to acknowledge and mark reservations as processed
            </p>
          </div>
        </div>
      </div>

      {/* Reservations Table */}
      <div className="premium-card" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
              <th style={{ ...tableHeaderStyle, width: '60px' }}>Ack</th>
              <th style={tableHeaderStyle}>ID</th>
              <th style={tableHeaderStyle}>Member</th>
              <th style={tableHeaderStyle}>Book</th>
              <th style={tableHeaderStyle}>Reservation Date</th>
              <th style={tableHeaderStyle}>Status</th>
              <th style={tableHeaderStyle}>Processed</th>
            </tr>
          </thead>
          <tbody>
            {reservations.length > 0 ? (
              reservations.map((reservation) => (
                <tr 
                  key={reservation.id} 
                  style={{ 
                    borderBottom: '1px solid var(--border-color)',
                    opacity: reservation.processed ? 0.6 : 1,
                    background: reservation.processed ? 'rgba(0,0,0,0.02)' : 'transparent'
                  }}
                >
                  <td style={{ ...tableCellStyle, textAlign: 'center' }}>
                    <input
                      type="checkbox"
                      checked={reservation.processed || false}
                      onChange={() => handleAcknowledge(reservation.id)}
                      disabled={reservation.processed || processing[reservation.id]}
                      style={{
                        width: '20px',
                        height: '20px',
                        cursor: reservation.processed ? 'not-allowed' : 'pointer',
                        accentColor: 'var(--success)'
                      }}
                    />
                  </td>
                  <td style={{ ...tableCellStyle, fontWeight: 600 }}>#{reservation.id}</td>
                  <td style={tableCellStyle}>
                    <div style={{ fontWeight: 500 }}>{reservation.memberName || '-'}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{reservation.memberEmail || ''}</div>
                  </td>
                  <td style={tableCellStyle}>
                    <div style={{ fontWeight: 500 }}>{reservation.bookTitle || '-'}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>ISBN: {reservation.bookIsbn || 'N/A'}</div>
                  </td>
                  <td style={tableCellStyle}>
                    {reservation.reservationDate ? new Date(reservation.reservationDate).toLocaleDateString() : '-'}
                  </td>
                  <td style={tableCellStyle}>
                    <span className={`badge ${getStatusBadgeClass(reservation.status)}`}>
                      {reservation.status || 'PENDING'}
                    </span>
                  </td>
                  <td style={tableCellStyle}>
                    {reservation.processed ? (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--success)', fontWeight: 600 }}>
                        <CheckCircle size={16} />
                        Yes
                      </span>
                    ) : (
                      <span style={{ color: 'var(--text-muted)' }}>No</span>
                    )}
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7" style={{ ...tableCellStyle, textAlign: 'center', padding: '3rem' }}>
                  <p style={{ color: 'var(--text-muted)' }}>No reservations found</p>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Summary */}
      <div style={{ marginTop: '1.5rem', padding: '1rem', background: 'var(--bg-secondary)', borderRadius: '0.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-around', textAlign: 'center' }}>
          <div>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0 }}>{reservations.length}</h3>
          </div>
          <div>
            <p style={{ color: 'var(--success)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Processed</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0, color: 'var(--success)' }}>
              {reservations.filter(r => r.processed).length}
            </h3>
          </div>
          <div>
            <p style={{ color: 'var(--warning)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Pending</p>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 700, margin: 0, color: 'var(--warning)' }}>
              {reservations.filter(r => !r.processed).length}
            </h3>
          </div>
        </div>
      </div>
    </div>
  );
};

const tableHeaderStyle = {
  textAlign: 'left',
  padding: '1rem',
  color: 'var(--text-muted)',
  fontSize: '0.75rem',
  fontWeight: 600
};

const tableCellStyle = {
  padding: '1rem',
  fontSize: '0.875rem'
};

export default ReservationManagement;
