import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import authService from '../../services/auth.service';
import { 
  Search, 
  BookOpen, 
  AlertCircle, 
  AlertTriangle, 
  Coins, 
  ArrowUpDown, 
  ChevronLeft, 
  ChevronRight, 
  Loader 
} from 'lucide-react';

const BookHistory = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortOrder, setSortOrder] = useState('desc'); // desc = latest first, asc = oldest first
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const user = authService.getCurrentUser();

  useEffect(() => {
    if (user && user.userId) {
      fetchHistory();
    } else {
      setLoading(false);
      setError('User not found. Please log in.');
    }
  }, []);

  // Reset pagination when search or sort changes
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, sortOrder]);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await api.get(`/transactions/user/${user.userId}`);
      setHistory(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error('Error fetching borrowing history:', err);
      setError(err.response?.data?.message || 'Failed to load borrowing history. Please try again.');
      setHistory([]);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    let label = status;
    let className = 'badge-secondary';
    
    if (status === 'RETURNED') {
      label = 'Returned';
      className = 'badge-success'; // Green
    } else if (status === 'ISSUED' || status === 'BORROWED') {
      label = 'Borrowed';
      className = 'badge-info'; // Blue
    } else if (status === 'OVERDUE') {
      label = 'Overdue';
      className = 'badge-danger'; // Red
    }
    
    return (
      <span className={`badge ${className}`}>
        {label}
      </span>
    );
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return 'Not Returned';
    try {
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return dateStr;
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    } catch (e) {
      return dateStr;
    }
  };

  // Calculations for Stats
  const totalBorrowed = history.length;
  const totalOverdue = history.filter(h => h.status === 'OVERDUE').length;
  const totalFines = history.reduce((sum, h) => sum + (h.fineAmount || 0), 0);

  // Filter and Sort Logic
  const filteredHistory = history.filter(h => 
    (h.bookTitle || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  const sortedHistory = [...filteredHistory].sort((a, b) => {
    const dateA = new Date(a.issueDate || 0);
    const dateB = new Date(b.issueDate || 0);
    return sortOrder === 'desc' ? dateB - dateA : dateA - dateB;
  });

  // Pagination Logic
  const totalPages = Math.ceil(sortedHistory.length / itemsPerPage);
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = sortedHistory.slice(indexOfFirstItem, indexOfLastItem);

  if (loading) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem' }}>
        <Loader size={40} className="animate-spin" style={{ color: 'var(--primary)' }} />
        <p style={{ color: 'var(--text-muted)' }}>Loading borrowing history...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>My Borrowing History</h1>
          <p style={{ color: 'var(--text-muted)' }}>View all your book borrowing and return records</p>
        </div>
        <div className="premium-card" style={{ textAlign: 'center', padding: '3rem' }}>
          <AlertCircle size={48} color="var(--danger)" style={{ marginBottom: '1rem', margin: '0 auto' }} />
          <p style={{ color: '#ef4444', fontWeight: 600, marginBottom: '1.5rem' }}>{error}</p>
          <button onClick={fetchHistory} className="btn btn-primary" style={{ padding: '0.5rem 1.5rem' }}>
            Retry Fetching
          </button>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Title Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>My Borrowing History</h1>
        <p style={{ color: 'var(--text-muted)' }}>View all your book borrowing and return records</p>
      </div>

      {/* Stats Cards */}
      <div className="stat-grid">
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Borrowed</p>
              <h3 style={{ fontSize: '1.75rem', fontWeight: 700, margin: 0 }}>{totalBorrowed}</h3>
            </div>
            <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '0.75rem', borderRadius: '0.5rem' }}>
              <BookOpen size={24} color="var(--primary)" />
            </div>
          </div>
        </div>

        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Overdue Books</p>
              <h3 style={{ fontSize: '1.75rem', fontWeight: 700, margin: 0, color: 'var(--danger)' }}>{totalOverdue}</h3>
            </div>
            <div style={{ background: 'rgba(239, 68, 68, 0.1)', padding: '0.75rem', borderRadius: '0.5rem' }}>
              <AlertTriangle size={24} color="var(--danger)" />
            </div>
          </div>
        </div>

        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Fines</p>
              <h3 style={{ fontSize: '1.75rem', fontWeight: 700, margin: 0, color: '#f59e0b' }}>
                Rs. {totalFines.toFixed(2)}
              </h3>
            </div>
            <div style={{ background: 'rgba(245, 158, 11, 0.1)', padding: '0.75rem', borderRadius: '0.5rem' }}>
              <Coins size={24} color="#f59e0b" />
            </div>
          </div>
        </div>
      </div>

      {/* Filter and Search Controls */}
      <div className="premium-card" style={{ marginBottom: '1.5rem', padding: '1.25rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', justifyContent: 'space-between', alignItems: 'center' }}>
          {/* Search Box */}
          <div style={{ flex: '1 1 300px', position: 'relative' }}>
            <Search size={18} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search by book title..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                width: '100%',
                padding: '0.65rem',
                paddingLeft: '2.5rem',
                borderRadius: '0.5rem',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-secondary)',
                color: 'var(--text-primary)',
                outline: 'none'
              }}
            />
          </div>

          {/* Sort Control */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>Sort by Issue Date:</span>
            <button
              onClick={() => setSortOrder(prev => prev === 'desc' ? 'asc' : 'desc')}
              className="btn btn-secondary"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                padding: '0.65rem 1rem',
                borderRadius: '0.5rem',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-secondary)',
                fontSize: '0.875rem',
                cursor: 'pointer',
                color: 'var(--text-primary)'
              }}
            >
              <ArrowUpDown size={16} />
              {sortOrder === 'desc' ? 'Latest First' : 'Oldest First'}
            </button>
          </div>
        </div>
      </div>

      {/* Borrowing History Table */}
      {sortedHistory.length === 0 ? (
        <div className="premium-card" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
          <BookOpen size={48} style={{ color: 'var(--text-muted)', strokeWidth: 1.2, marginBottom: '1rem', margin: '0 auto' }} />
          <p style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--text-primary)', margin: 0 }}>
            {searchTerm ? 'No matching books found' : 'No borrowing history found.'}
          </p>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
            {searchTerm ? 'Try checking your spelling or search for another title.' : 'Books you borrow from the library will appear here.'}
          </p>
        </div>
      ) : (
        <div className="premium-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ overflowX: 'auto' }}>
            <table className="data-table" style={{ margin: 0, width: '100%' }}>
              <thead>
                <tr>
                  <th style={{ padding: '1rem 1.25rem' }}>Book Title</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Issue Date</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Due Date</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Return Date</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Status</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Fine</th>
                </tr>
              </thead>
              <tbody>
                {currentItems.map((h) => (
                  <tr key={h.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td style={{ padding: '1rem 1.25rem', fontWeight: 500, color: 'var(--text-primary)' }}>
                      {h.bookTitle || 'Unknown Book'}
                    </td>
                    <td style={{ padding: '1rem 1.25rem' }}>{formatDate(h.issueDate)}</td>
                    <td style={{ padding: '1rem 1.25rem' }}>{formatDate(h.dueDate)}</td>
                    <td style={{ padding: '1rem 1.25rem' }}>
                      {h.returnDate ? (
                        formatDate(h.returnDate)
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>Not Returned</span>
                      )}
                    </td>
                    <td style={{ padding: '1rem 1.25rem' }}>{getStatusBadge(h.status)}</td>
                    <td style={{ padding: '1rem 1.25rem', fontWeight: h.fineAmount > 0 ? 600 : 400, color: h.fineAmount > 0 ? 'var(--danger)' : 'var(--text-primary)' }}>
                      Rs. {h.fineAmount !== null && h.fineAmount !== undefined ? h.fineAmount.toFixed(2) : '0.00'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between', 
              alignItems: 'center', 
              padding: '1.25rem', 
              borderTop: '1px solid var(--border-color)',
              flexWrap: 'wrap',
              gap: '1rem'
            }}>
              <span style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                Showing {indexOfFirstItem + 1} to {Math.min(indexOfLastItem, sortedHistory.length)} of {sortedHistory.length} records
              </span>
              <div style={{ display: 'flex', gap: '0.25rem' }}>
                <button 
                  onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                  disabled={currentPage === 1}
                  className="btn btn-secondary"
                  style={{ padding: '0.4rem 0.8rem', display: 'inline-flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.875rem' }}
                >
                  <ChevronLeft size={16} /> Prev
                </button>
                {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                  <button
                    key={page}
                    onClick={() => setCurrentPage(page)}
                    className={`btn ${currentPage === page ? 'btn-primary' : 'btn-secondary'}`}
                    style={{ padding: '0.4rem 0.8rem', fontSize: '0.875rem', minWidth: '2.25rem' }}
                  >
                    {page}
                  </button>
                ))}
                <button 
                  onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                  disabled={currentPage === totalPages}
                  className="btn btn-secondary"
                  style={{ padding: '0.4rem 0.8rem', display: 'inline-flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.875rem' }}
                >
                  Next <ChevronRight size={16} />
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default BookHistory;
