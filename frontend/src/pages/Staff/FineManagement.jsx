import React, { useEffect, useState } from 'react';
import { DollarSign, TrendingUp, Edit2, Trash2, CheckCircle } from 'lucide-react';
import api from '../../services/api';

const FineManagement = () => {
  const [fines, setFines] = useState([]);
  const [stats, setStats] = useState({ totalUnpaid: 0, totalPaid: 0, totalCollected: 0 });
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [editingFine, setEditingFine] = useState(null);
  const [editData, setEditData] = useState({ paymentDate: '', status: '' });

  useEffect(() => {
    fetchFines();
    fetchStats();
  }, [filter]);

  const fetchFines = async () => {
    try {
      setLoading(true);
      const response = await api.get('/staff/fines', { 
        params: { status: filter } 
      });
      setFines(response.data);
    } catch (error) {
      console.error('Error fetching fines:', error);
      alert('Error fetching fines');
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    try {
      const response = await api.get('/staff/fines/stats');
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching fine stats:', error);
    }
  };

  const openEditModal = (fine) => {
    setEditingFine(fine);
    setEditData({
      paymentDate: fine.paymentDate || new Date().toISOString().split('T')[0],
      status: fine.status
    });
  };

  const handleUpdateFine = async (e) => {
    e.preventDefault();
    
    try {
      await api.put(`/staff/fines/${editingFine.id}`, null, {
        params: {
          paymentDate: editData.paymentDate,
          status: editData.status
        }
      });

      setEditingFine(null);
      fetchFines();
      fetchStats();
    } catch (error) {
      console.error('Error updating fine:', error);
      const message = error.response?.data?.message || 'Error updating fine';
      alert(message);
    }
  };

  const handleMarkAsPaid = async (fineId) => {
    if (!confirm('Mark this fine as paid?')) return;
    
    try {
      await api.put(`/staff/fines/${fineId}/pay`);
      fetchFines();
      fetchStats();
    } catch (error) {
      console.error('Error marking fine as paid:', error);
      const message = error.response?.data?.message || 'Error marking fine as paid';
      alert(message);
    }
  };

  const handleDeleteFine = async (fineId) => {
    if (!confirm('Are you sure you want to delete this fine? This action cannot be undone.')) return;
    
    try {
      await api.delete(`/staff/fines/${fineId}`);
      fetchFines();
      fetchStats();
    } catch (error) {
      console.error('Error deleting fine:', error);
      const message = error.response?.data?.message || 'Error deleting fine';
      alert(message);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'PAID': return 'badge-success';
      case 'UNPAID': return 'badge-danger';
      case 'NONE': return 'badge-info';
      default: return 'badge-secondary';
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Fine Management</h1>
        <p style={{ color: 'var(--text-muted)' }}>Track and manage library fines</p>
      </div>

      {/* Stats Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="premium-card" style={{ borderLeft: '4px solid var(--danger)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Unpaid Fines</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--danger)' }}>
                Rs. {stats.totalUnpaid?.toFixed(2)}
              </h3>
            </div>
            <DollarSign size={32} color="var(--danger)" />
          </div>
        </div>

        <div className="premium-card" style={{ borderLeft: '4px solid var(--success)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Collected</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--success)' }}>
                Rs. {stats.totalPaid?.toFixed(2)}
              </h3>
            </div>
            <TrendingUp size={32} color="var(--success)" />
          </div>
        </div>
      </div>

      {/* Filter */}
      <div className="premium-card" style={{ marginBottom: '1.5rem', padding: '1rem' }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button
            onClick={() => setFilter('ALL')}
            className={`btn ${filter === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
          >
            All
          </button>
          <button
            onClick={() => setFilter('UNPAID')}
            className={`btn ${filter === 'UNPAID' ? 'btn-primary' : 'btn-secondary'}`}
          >
            Unpaid
          </button>
          <button
            onClick={() => setFilter('PAID')}
            className={`btn ${filter === 'PAID' ? 'btn-primary' : 'btn-secondary'}`}
          >
            Paid
          </button>
          <button
            onClick={() => setFilter('NONE')}
            className={`btn ${filter === 'NONE' ? 'btn-primary' : 'btn-secondary'}`}
          >
            None
          </button>
        </div>
      </div>

      {/* Fines Table */}
      <div className="premium-card" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
              <th style={tableHeaderStyle}>Fine ID</th>
              <th style={tableHeaderStyle}>Member</th>
              <th style={tableHeaderStyle}>Book</th>
              <th style={tableHeaderStyle}>Issue Date</th>
              <th style={tableHeaderStyle}>Return Date</th>
              <th style={tableHeaderStyle}>Payment Date</th>
              <th style={tableHeaderStyle}>Amount</th>
              <th style={tableHeaderStyle}>Status</th>
              <th style={{ ...tableHeaderStyle, textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {fines.length > 0 ? (
              fines.map((fine) => (
                <tr key={fine.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <td style={tableCellStyle}>#{fine.id}</td>
                  <td style={{ ...tableCellStyle, fontWeight: 500 }}>{fine.memberName || '-'}</td>
                  <td style={{ ...tableCellStyle, fontWeight: 500 }}>{fine.bookTitle || '-'}</td>
                  <td style={tableCellStyle}>{fine.issueDate || '-'}</td>
                  <td style={tableCellStyle}>{fine.returnDate || '-'}</td>
                  <td style={tableCellStyle}>{fine.paymentDate || '-'}</td>
                  <td style={{ ...tableCellStyle, fontWeight: 600 }}>
                    Rs. {fine.amount?.toFixed(2) || '0.00'}
                  </td>
                  <td style={tableCellStyle}>
                    <span className={`badge ${getStatusBadgeClass(fine.status)}`}>
                      {fine.status}
                    </span>
                  </td>
                  <td style={{ ...tableCellStyle, textAlign: 'right' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                      <button
                        onClick={() => openEditModal(fine)}
                        style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          gap: '0.25rem',
                          background: 'var(--primary)', 
                          border: 'none', 
                          color: 'white', 
                          padding: '0.4rem 0.75rem', 
                          borderRadius: '0.4rem', 
                          cursor: 'pointer', 
                          fontSize: '0.75rem'
                        }}
                        title="Edit Fine"
                      >
                        <Edit2 size={14} /> Edit
                      </button>
                      {fine.status === 'UNPAID' && (
                        <button
                          onClick={() => handleMarkAsPaid(fine.id)}
                          style={{ 
                            display: 'flex', 
                            alignItems: 'center', 
                            gap: '0.25rem',
                            background: 'var(--success)', 
                            border: 'none', 
                            color: 'white', 
                            padding: '0.4rem 0.75rem', 
                            borderRadius: '0.4rem', 
                            cursor: 'pointer', 
                            fontSize: '0.75rem'
                          }}
                          title="Mark as Paid"
                        >
                          <CheckCircle size={14} /> Pay
                        </button>
                      )}
                      <button
                        onClick={() => handleDeleteFine(fine.id)}
                        style={{ 
                          display: 'flex', 
                          alignItems: 'center', 
                          gap: '0.25rem',
                          background: 'var(--danger)', 
                          border: 'none', 
                          color: 'white', 
                          padding: '0.4rem 0.75rem', 
                          borderRadius: '0.4rem', 
                          cursor: 'pointer', 
                          fontSize: '0.75rem'
                        }}
                        title="Delete Fine"
                      >
                        <Trash2 size={14} /> Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="9" style={{ ...tableCellStyle, textAlign: 'center', padding: '3rem' }}>
                  <p style={{ color: 'var(--text-muted)' }}>No fines found</p>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Edit Modal */}
      {editingFine && (
        <div style={{
          position: 'fixed',
          top: 0, left: 0, right: 0, bottom: 0,
          background: 'rgba(0,0,0,0.7)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
          padding: '1rem'
        }}>
          <div className="premium-card" style={{ width: '100%', maxWidth: '500px', position: 'relative' }}>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '1.5rem' }}>Edit Fine</h2>
            
            <div style={{ marginBottom: '1.5rem', padding: '1rem', background: 'rgba(255,255,255,0.05)', borderRadius: '0.5rem' }}>
              <p><strong>Fine ID:</strong> #{editingFine.id}</p>
              <p><strong>Member:</strong> {editingFine.memberName}</p>
              <p><strong>Book:</strong> {editingFine.bookTitle}</p>
              <p><strong>Current Amount:</strong> Rs. {editingFine.amount?.toFixed(2)}</p>
            </div>

            <form onSubmit={handleUpdateFine}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Payment Date</label>
                <input
                  type="date"
                  value={editData.paymentDate}
                  onChange={(e) => setEditData({ ...editData, paymentDate: e.target.value })}
                  style={{ 
                    width: '100%', 
                    padding: '0.75rem',
                    borderRadius: '0.5rem',
                    border: '1px solid var(--border-color)',
                    background: 'var(--bg-secondary)',
                    color: 'var(--text-primary)'
                  }}
                />
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Status</label>
                <select
                  value={editData.status}
                  onChange={(e) => setEditData({ ...editData, status: e.target.value })}
                  style={{ 
                    width: '100%', 
                    padding: '0.75rem',
                    borderRadius: '0.5rem',
                    border: '1px solid var(--border-color)',
                    background: 'var(--bg-secondary)',
                    color: 'var(--text-primary)'
                  }}
                >
                  <option value="UNPAID">Unpaid</option>
                  <option value="PAID">Paid</option>
                  <option value="NONE">None</option>
                </select>
              </div>

              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button 
                  type="button" 
                  className="btn-secondary" 
                  onClick={() => setEditingFine(null)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Update Fine
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
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

export default FineManagement;
