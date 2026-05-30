import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import authService from '../../services/auth.service';

const BookHistory = () => {
  const [history, setHistory] = useState([]);
  const user = authService.getCurrentUser();

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    const res = await api.get(`/transactions/user/${user.userId}`);
    setHistory(res.data);
  };

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>My Borrowing History</h1>
      <div className="premium-card" style={{ padding: 0 }}>
        <table className="data-table">
          <thead>
            <tr>
              <th>Book</th>
              <th>Issue Date</th>
              <th>Return Date</th>
              <th>Fine</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.map(h => (
              <tr key={h.id}>
                <td>{h.book.title}</td>
                <td>{h.issueDate}</td>
                <td>{h.returnDate || '-'}</td>
                <td>Rs. {h.fineAmount}</td>
                <td><span className={`badge ${h.status === 'RETURNED' ? 'badge-success' : 'badge-danger'}`}>{h.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default BookHistory;
