import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import authService from '../../services/auth.service';
import { Search, Bookmark, ChevronRight } from 'lucide-react';

const MemberDashboard = () => {
  const [books, setBooks] = useState([]);
  const user = authService.getCurrentUser();

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    const res = await api.get('/books');
    setBooks(res.data);
  };

  const reserveBook = async (bookId) => {
    try {
      await api.post('/reservations', { userId: user.userId, bookId: bookId });
      alert('Reservation successful!');
    } catch (err) {
      alert('Failed to reserve book');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Welcome member {user.username}</h1>
        <p style={{ color: 'var(--text-muted)' }}>Explore our library collection</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1.5rem' }}>
        {books.map((book) => (
          <div key={book.id} className="premium-card" style={{ display: 'flex', flexDirection: 'column' }}>
            <div style={{ position: 'relative', height: '180px', background: 'rgba(255,255,255,0.02)', borderRadius: '0.75rem', marginBottom: '1rem', overflow: 'hidden' }}>
              {book.coverImage ? (
                <img src={`http://localhost:8081${book.coverImage}`} alt={book.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
              ) : (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'var(--text-muted)' }}>No Cover</div>
              )}
              <div style={{ position: 'absolute', top: '0.75rem', right: '0.75rem' }}>
                <span className="badge badge-success" style={{ fontSize: '0.7rem' }}>{book.category}</span>
              </div>
            </div>
            
            <h4 style={{ fontSize: '1.1rem', marginBottom: '0.25rem' }}>{book.title}</h4>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1rem' }}>By {book.author?.name || 'Unknown Author'}</p>
            
            <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
               <p style={{ fontSize: '0.875rem' }}>
                 Copies: <span style={{ fontWeight: 600 }}>{book.availableCopies}</span>
               </p>
               <button 
                 onClick={() => reserveBook(book.id)}
                 className="btn-primary" 
                 style={{ padding: '0.4rem 0.75rem', fontSize: '0.875rem', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
               >
                 Reserve <Bookmark size={14} />
               </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MemberDashboard;
