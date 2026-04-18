import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { BookPlus, RotateCcw, AlertTriangle, CheckCircle, Clock, TrendingUp } from 'lucide-react';

const TransactionManagement = () => {
  const [transactions, setTransactions] = useState([]);
  const [counters, setCounters] = useState({ totalBorrows: 0, totalOverdue: 0, totalReturned: 0 });
  const [statusFilter, setStatusFilter] = useState('');
  const [showIssueModal, setShowIssueModal] = useState(false);
  const [showReturnModal, setShowReturnModal] = useState(false);
  const [selectedTransaction, setSelectedTransaction] = useState(null);
  const [loading, setLoading] = useState(false);
  const [users, setUsers] = useState([]);
  const [books, setBooks] = useState([]);
  
  const [issueData, setIssueData] = useState({ userId: '', bookId: '' });
  const [returnData, setReturnData] = useState({ 
    returnDate: '', 
    bookCondition: 'GOOD', 
    conditionNotes: '' 
  });
  const [calculatedFine, setCalculatedFine] = useState({ amount: 0, daysLate: 0, status: 'None' });

  useEffect(() => {
    fetchTransactions();
    fetchCounters();
    fetchUsers();
    fetchBooks();
  }, [statusFilter]);

  const fetchTransactions = async () => {
    try {
      const params = statusFilter ? { status: statusFilter } : {};
      const res = await api.get('/staff/transactions', { params });
      setTransactions(res.data);
    } catch (error) {
      console.error('Error fetching transactions:', error);
    }
  };

  const fetchCounters = async () => {
    try {
      const res = await api.get('/staff/transactions/counters');
      setCounters(res.data);
    } catch (error) {
      console.error('Error fetching counters:', error);
    }
  };

  const fetchUsers = async () => {
    try {
      const res = await api.get('/staff/members');
      setUsers(res.data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const fetchBooks = async () => {
    try {
      const res = await api.get('/staff/books');
      setBooks(res.data);
    } catch (error) {
      console.error('Error fetching books:', error);
    }
  };

  const handleIssueBook = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await api.post('/staff/transactions/issue', null, {
        params: {
          userId: issueData.userId,
          bookId: issueData.bookId
        }
      });

      setShowIssueModal(false);
      setIssueData({ userId: '', bookId: '' });
      fetchTransactions();
      fetchCounters();
    } catch (error) {
      console.error('Error issuing book:', error);
      const message = error.response?.data?.message || 'Failed to issue book';
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  const openReturnModal = (transaction) => {
    setSelectedTransaction(transaction);
    const today = new Date().toISOString().split('T')[0];
    setReturnData({
      returnDate: today,
      bookCondition: 'GOOD',
      conditionNotes: ''
    });
    
    // Calculate fine preview
    calculateFinePreview(transaction.dueDate, today);
    
    setShowReturnModal(true);
  };

  const calculateFinePreview = (dueDate, returnDate) => {
    if (!dueDate || !returnDate) {
      setCalculatedFine({ amount: 0, daysLate: 0, status: 'None' });
      return;
    }

    const due = new Date(dueDate);
    const returned = new Date(returnDate);
    const timeDiff = returned.getTime() - due.getTime();
    const daysLate = Math.ceil(timeDiff / (1000 * 3600 * 24));
    
    if (daysLate > 0) {
      const amount = daysLate * 5; // Rs. 5 per day
      setCalculatedFine({
        amount: amount,
        daysLate: daysLate,
        status: 'Unpaid'
      });
    } else {
      setCalculatedFine({ amount: 0, daysLate: 0, status: 'None' });
    }
  };

  const handleReturnDateChange = (date) => {
    setReturnData({ ...returnData, returnDate: date });
    if (selectedTransaction) {
      calculateFinePreview(selectedTransaction.dueDate, date);
    }
  };

  const handleReturnBook = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await api.put(`/staff/transactions/return/${selectedTransaction.id}`, null, {
        params: {
          returnDate: returnData.returnDate,
          bookCondition: returnData.bookCondition,
          conditionNotes: returnData.conditionNotes
        }
      });

      setShowReturnModal(false);
      setSelectedTransaction(null);
      fetchTransactions();
      fetchCounters();
    } catch (error) {
      console.error('Error returning book:', error);
      const message = error.response?.data?.message || 'Failed to return book';
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'ISSUED': return 'badge-warning';
      case 'OVERDUE': return 'badge-danger';
      case 'RETURNED': return 'badge-success';
      default: return 'badge-info';
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Circulation Management</h1>
          <p style={{ color: 'var(--text-muted)' }}>Issue and return books</p>
        </div>
        <button 
          className="btn-primary" 
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
          onClick={() => setShowIssueModal(true)}
        >
          <BookPlus size={18} /> Issue Book
        </button>
      </div>

      {/* Counter Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
        <div className="premium-card" style={{ borderLeft: '4px solid var(--warning)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Borrows</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0 }}>{counters.totalBorrows}</h3>
            </div>
            <TrendingUp size={32} color="var(--warning)" />
          </div>
        </div>

        <div className="premium-card" style={{ borderLeft: '4px solid var(--danger)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Overdue</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--danger)' }}>{counters.totalOverdue}</h3>
            </div>
            <AlertTriangle size={32} color="var(--danger)" />
          </div>
        </div>

        <div className="premium-card" style={{ borderLeft: '4px solid var(--success)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Returned</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--success)' }}>{counters.totalReturned}</h3>
            </div>
            <CheckCircle size={32} color="var(--success)" />
          </div>
        </div>
      </div>

      {/* Filter */}
      <div className="premium-card" style={{ marginBottom: '1.5rem', padding: '1rem' }}>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <label style={{ fontWeight: 500 }}>Filter by Status:</label>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={{ 
              padding: '0.5rem 1rem',
              borderRadius: '0.5rem',
              border: '1px solid var(--border-color)',
              background: 'var(--bg-secondary)',
              color: 'var(--text-primary)',
              cursor: 'pointer'
            }}
          >
            <option value="">All</option>
            <option value="ISSUED">Issue</option>
            <option value="OVERDUE">Overdue</option>
            <option value="RETURNED">Return</option>
          </select>
        </div>
      </div>

      {/* Transactions Table */}
      <div className="premium-card" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>ID</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Member</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Book</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Issue Date</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Due Date</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Return Date</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Condition</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Status</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Fine</th>
              <th style={{ textAlign: 'right', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((t) => (
              <tr key={t.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '1rem', fontWeight: 600 }}>#{t.id}</td>
                <td style={{ padding: '1rem' }}>
                  <div style={{ fontWeight: 500 }}>{t.memberName}</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{t.memberEmail}</div>
                </td>
                <td style={{ padding: '1rem' }}>
                  <div style={{ fontWeight: 500 }}>{t.bookTitle}</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>ISBN: {t.bookIsbn || 'N/A'}</div>
                </td>
                <td style={{ padding: '1rem' }}>{t.issueDate}</td>
                <td style={{ padding: '1rem' }}>{t.dueDate}</td>
                <td style={{ padding: '1rem' }}>{t.returnDate || '-'}</td>
                <td style={{ padding: '1rem' }}>
                  {t.bookCondition ? (
                    <span style={{ 
                      fontWeight: 600,
                      color: t.bookCondition === 'GOOD' ? 'var(--success)' : 
                             t.bookCondition === 'POOR' ? 'var(--warning)' : 'var(--danger)'
                    }}>
                      {t.bookCondition}
                    </span>
                  ) : (
                    <span style={{ color: 'var(--text-muted)' }}>-</span>
                  )}
                </td>
                <td style={{ padding: '1rem' }}>
                  <span className={`badge ${getStatusBadgeClass(t.status)}`}>
                    {t.status}
                  </span>
                </td>
                <td style={{ padding: '1rem' }}>
                  {t.fineAmount > 0 ? (
                    <span style={{ color: 'var(--danger)', fontWeight: 600 }}>Rs. {t.fineAmount.toFixed(2)}</span>
                  ) : (
                    <span style={{ color: 'var(--text-muted)' }}>None</span>
                  )}
                </td>
                <td style={{ padding: '1rem', textAlign: 'right' }}>
                  {(t.status === 'ISSUED' || t.status === 'OVERDUE') && (
                    <button 
                      onClick={() => openReturnModal(t)}
                      disabled={loading}
                      style={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        gap: '0.25rem', 
                        background: 'var(--primary)', 
                        border: 'none', 
                        color: 'white', 
                        padding: '0.5rem 1rem', 
                        borderRadius: '0.4rem', 
                        cursor: 'pointer', 
                        fontSize: '0.875rem',
                        marginLeft: 'auto'
                      }}
                    >
                      <RotateCcw size={14} /> Return
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {transactions.length === 0 && (
        <div className="premium-card" style={{ textAlign: 'center', padding: '3rem' }}>
          <Clock size={48} color="var(--text-muted)" style={{ marginBottom: '1rem' }} />
          <p style={{ color: 'var(--text-muted)' }}>No transactions found</p>
        </div>
      )}

      {/* Issue Book Modal */}
      {showIssueModal && (
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
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '1.5rem' }}>Issue Book</h2>
            
            <form onSubmit={handleIssueBook}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Select Member *</label>
                <select
                  value={issueData.userId}
                  onChange={(e) => setIssueData({ ...issueData, userId: e.target.value })}
                  required
                  style={{ 
                    width: '100%', 
                    padding: '0.75rem',
                    borderRadius: '0.5rem',
                    border: '1px solid var(--border-color)',
                    background: 'var(--bg-secondary)',
                    color: 'var(--text-primary)'
                  }}
                >
                  <option value="">Choose a member</option>
                  {users.filter(u => u.role === 'MEMBER').map(user => (
                    <option key={user.id} value={user.id}>{user.username} ({user.email})</option>
                  ))}
                </select>
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Select Book *</label>
                <select
                  value={issueData.bookId}
                  onChange={(e) => setIssueData({ ...issueData, bookId: e.target.value })}
                  required
                  style={{ 
                    width: '100%', 
                    padding: '0.75rem',
                    borderRadius: '0.5rem',
                    border: '1px solid var(--border-color)',
                    background: 'var(--bg-secondary)',
                    color: 'var(--text-primary)'
                  }}
                >
                  <option value="">Choose a book</option>
                  {books.filter(b => b.availableCopies > 0).map(book => (
                    <option key={book.id} value={book.id}>
                      {book.title} ({book.availableCopies} available)
                    </option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn-secondary" onClick={() => setShowIssueModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary" disabled={loading}>
                  {loading ? 'Issuing...' : 'Issue Book'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Return Book Modal */}
      {showReturnModal && selectedTransaction && (
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
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '1.5rem' }}>Return Book</h2>
            
            <div style={{ marginBottom: '1.5rem', padding: '1rem', background: 'rgba(255,255,255,0.05)', borderRadius: '0.5rem' }}>
              <p><strong>Book:</strong> {selectedTransaction.bookTitle}</p>
              <p><strong>Member:</strong> {selectedTransaction.memberName}</p>
              <p><strong>Due Date:</strong> {selectedTransaction.dueDate}</p>
            </div>

            {/* Fine Calculation Display */}
            <div style={{ 
              marginBottom: '1.5rem', 
              padding: '1rem', 
              background: calculatedFine.amount > 0 ? 'rgba(239, 68, 68, 0.1)' : 'rgba(16, 185, 129, 0.1)',
              border: `1px solid ${calculatedFine.amount > 0 ? 'var(--danger)' : 'var(--success)'}`,
              borderRadius: '0.5rem'
            }}>
              <h4 style={{ margin: '0 0 0.75rem 0', fontSize: '1rem', fontWeight: 600 }}>Fine Calculation</h4>
              <div style={{ display: 'grid', gap: '0.5rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Days Late:</span>
                  <span style={{ 
                    fontWeight: 600, 
                    color: calculatedFine.daysLate > 0 ? 'var(--danger)' : 'var(--success)' 
                  }}>
                    {calculatedFine.daysLate} days
                  </span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Fine Rate:</span>
                  <span style={{ fontWeight: 600 }}>Rs. 5.00 / day</span>
                </div>
                <div style={{ 
                  display: 'flex', 
                  justifyContent: 'space-between', 
                  paddingTop: '0.5rem',
                  borderTop: '1px solid rgba(255,255,255,0.1)'
                }}>
                  <span style={{ fontWeight: 600, fontSize: '1.1rem' }}>Total Fine:</span>
                  <span style={{ 
                    fontWeight: 700, 
                    fontSize: '1.25rem',
                    color: calculatedFine.amount > 0 ? 'var(--danger)' : 'var(--success)' 
                  }}>
                    Rs. {calculatedFine.amount.toFixed(2)}
                  </span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Status:</span>
                  <span className={`badge ${calculatedFine.status === 'Unpaid' ? 'badge-danger' : 'badge-success'}`}>
                    {calculatedFine.status}
                  </span>
                </div>
              </div>
            </div>

            <form onSubmit={handleReturnBook}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Return Date *</label>
                <input
                  type="date"
                  value={returnData.returnDate}
                  onChange={(e) => handleReturnDateChange(e.target.value)}
                  required
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
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Book Condition *</label>
                <select
                  value={returnData.bookCondition}
                  onChange={(e) => setReturnData({ ...returnData, bookCondition: e.target.value })}
                  required
                  style={{ 
                    width: '100%', 
                    padding: '0.75rem',
                    borderRadius: '0.5rem',
                    border: '1px solid var(--border-color)',
                    background: 'var(--bg-secondary)',
                    color: 'var(--text-primary)'
                  }}
                >
                  <option value="">Select condition</option>
                  <option value="GOOD">Good (No damage)</option>
                  <option value="POOR">Poor (Minor wear)</option>
                  <option value="DAMAGED">Damaged (Significant damage)</option>
                </select>
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Condition Notes</label>
                <textarea
                  value={returnData.conditionNotes}
                  onChange={(e) => setReturnData({ ...returnData, conditionNotes: e.target.value })}
                  rows="3"
                  placeholder="Optional notes about book condition..."
                  style={{ 
                    width: '100%', 
                    padding: '0.75rem',
                    borderRadius: '0.5rem',
                    border: '1px solid var(--border-color)',
                    background: 'var(--bg-secondary)',
                    color: 'var(--text-primary)',
                    resize: 'vertical'
                  }}
                />
              </div>

              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn-secondary" onClick={() => setShowReturnModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary" disabled={loading}>
                  {loading ? 'Processing...' : 'Return Book'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default TransactionManagement;
