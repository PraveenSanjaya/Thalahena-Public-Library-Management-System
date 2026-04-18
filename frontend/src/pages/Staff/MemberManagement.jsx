import React, { useEffect, useState } from 'react';
import { Eye, Search } from 'lucide-react';
import api from '../../services/api';

const StaffMemberManagement = () => {
  const [members, setMembers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedMember, setSelectedMember] = useState(null);
  const [borrowHistory, setBorrowHistory] = useState([]);
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      const response = await api.get('/staff/members');
      setMembers(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error fetching members:', error);
      setMembers([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchMembers();
      return;
    }
    try {
      const response = await api.get(`/staff/members/search?query=${searchTerm}`);
      setMembers(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error searching members:', error);
      setMembers([]);
    }
  };

  const viewBorrowHistory = async (member) => {
    setSelectedMember(member);
    try {
      const response = await api.get(`/staff/members/${member.id}/borrow-history`);
      setBorrowHistory(response.data);
      setShowHistoryModal(true);
    } catch (error) {
      console.error('Error fetching borrow history:', error);
      alert('Error fetching borrow history');
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Member Management</h1>
        <p style={{ color: 'var(--text-muted)' }}>View member information and borrowing history</p>
      </div>

      {/* Search */}
      <div className="premium-card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <div style={{ flex: 1, position: 'relative' }}>
            <Search size={20} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search by name, username, or email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              style={{
                width: '100%',
                padding: '0.75rem 1rem 0.75rem 3rem',
                background: 'var(--bg-secondary)',
                border: '1px solid var(--border-color)',
                borderRadius: '0.5rem',
                color: 'var(--text-primary)'
              }}
            />
          </div>
          <button onClick={handleSearch} className="btn btn-primary">Search</button>
          <button onClick={() => { setSearchTerm(''); fetchMembers(); }} className="btn btn-secondary">Clear</button>
        </div>
      </div>

      {/* Members Table */}
      <div className="premium-card">
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                <th style={tableHeaderStyle}>ID</th>
                <th style={tableHeaderStyle}>Name</th>
                <th style={tableHeaderStyle}>Email</th>
                <th style={tableHeaderStyle}>Phone</th>
                <th style={tableHeaderStyle}>Membership Date</th>
                <th style={tableHeaderStyle}>Status</th>
                <th style={tableHeaderStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {Array.isArray(members) && members.map((member) => (
                <tr key={member.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <td style={tableCellStyle}>{member.id}</td>
                  <td style={tableCellStyle}>
                    {member.firstName} {member.lastName}
                  </td>
                  <td style={tableCellStyle}>{member.email}</td>
                  <td style={tableCellStyle}>{member.phone || '-'}</td>
                  <td style={tableCellStyle}>
                    {member.membershipDate ? new Date(member.membershipDate).toLocaleDateString() : '-'}
                  </td>
                  <td style={tableCellStyle}>
                    <span style={{
                      padding: '0.25rem 0.75rem',
                      borderRadius: '0.25rem',
                      background: member.isActive ? 'var(--success)' : 'var(--text-muted)',
                      color: 'white',
                      fontSize: '0.75rem',
                      fontWeight: 600
                    }}>
                      {member.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td style={tableCellStyle}>
                    <button 
                      onClick={() => viewBorrowHistory(member)} 
                      className="btn btn-sm btn-primary"
                      style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
                    >
                      <Eye size={16} />
                      View History
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Borrow History Modal */}
      {showHistoryModal && selectedMember && (
        <div style={modalOverlayStyle} onClick={() => setShowHistoryModal(false)}>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h2>Borrow History - {selectedMember.firstName} {selectedMember.lastName}</h2>
              <button onClick={() => setShowHistoryModal(false)} className="btn btn-secondary">Close</button>
            </div>
            
            {borrowHistory.length > 0 ? (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                      <th style={tableHeaderStyle}>Book Title</th>
                      <th style={tableHeaderStyle}>Issue Date</th>
                      <th style={tableHeaderStyle}>Due Date</th>
                      <th style={tableHeaderStyle}>Return Date</th>
                      <th style={tableHeaderStyle}>Status</th>
                      <th style={tableHeaderStyle}>Fine</th>
                    </tr>
                  </thead>
                  <tbody>
                    {borrowHistory.map((transaction) => (
                      <tr key={transaction.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                        <td style={tableCellStyle}>{transaction.book?.title || '-'}</td>
                        <td style={tableCellStyle}>
                          {transaction.issueDate ? new Date(transaction.issueDate).toLocaleDateString() : '-'}
                        </td>
                        <td style={tableCellStyle}>
                          {transaction.dueDate ? new Date(transaction.dueDate).toLocaleDateString() : '-'}
                        </td>
                        <td style={tableCellStyle}>
                          {transaction.returnDate ? new Date(transaction.returnDate).toLocaleDateString() : '-'}
                        </td>
                        <td style={tableCellStyle}>
                          <span style={{
                            padding: '0.25rem 0.75rem',
                            borderRadius: '0.25rem',
                            background: transaction.status === 'RETURNED' ? 'var(--success)' : 
                                       transaction.status === 'ISSUED' ? 'var(--warning)' : 'var(--danger)',
                            color: 'white',
                            fontSize: '0.75rem',
                            fontWeight: 600
                          }}>
                            {transaction.status}
                          </span>
                        </td>
                        <td style={tableCellStyle}>
                          {transaction.fineAmount > 0 ? `Rs. ${transaction.fineAmount.toFixed(2)}` : '-'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
                No borrowing history found
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const tableHeaderStyle = {
  textAlign: 'left',
  padding: '0.75rem',
  color: 'var(--text-muted)',
  fontSize: '0.75rem',
  fontWeight: 600
};

const tableCellStyle = {
  padding: '0.75rem',
  fontSize: '0.875rem'
};

const modalOverlayStyle = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  background: 'rgba(0,0,0,0.7)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000
};

const modalContentStyle = {
  background: 'var(--bg-primary)',
  padding: '2rem',
  borderRadius: '1rem',
  maxWidth: '900px',
  width: '90%',
  maxHeight: '90vh',
  overflow: 'auto'
};

export default StaffMemberManagement;
