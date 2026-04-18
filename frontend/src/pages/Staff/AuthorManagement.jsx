import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { Plus, Edit2, Trash2, X, Search, BookOpen } from 'lucide-react';

const AuthorManagement = () => {
  const [authors, setAuthors] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingAuthor, setEditingAuthor] = useState(null);
  const [loading, setLoading] = useState(false);
  
  const [formData, setFormData] = useState({
    name: '',
    bio: ''
  });

  useEffect(() => {
    fetchAuthors();
  }, []);

  const fetchAuthors = async (search = '') => {
    try {
      const params = search ? { search } : {};
      const res = await api.get('/staff/authors', { params });
      setAuthors(res.data);
    } catch (error) {
      console.error('Error fetching authors:', error);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchAuthors(searchTerm);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const openAddModal = () => {
    setEditingAuthor(null);
    setFormData({
      name: '',
      bio: ''
    });
    setShowModal(true);
  };

  const openEditModal = (author) => {
    setEditingAuthor(author);
    setFormData({
      name: author.name || '',
      bio: author.bio || ''
    });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (editingAuthor) {
        await api.put(`/staff/authors/${editingAuthor.id}`, formData);
      } else {
        await api.post('/staff/authors', formData);
      }

      setShowModal(false);
      fetchAuthors();
    } catch (error) {
      console.error('Error saving author:', error);
      const message = error.response?.data?.message || 'Failed to save author';
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  const deleteAuthor = async (id, name) => {
    if (window.confirm(`Are you sure you want to delete "${name}"? This action cannot be undone.`)) {
      try {
        await api.delete(`/staff/authors/${id}`);
        fetchAuthors();
      } catch (error) {
        console.error('Error deleting author:', error);
        const message = error.response?.data?.message || 'Failed to delete author';
        alert(message);
      }
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Author Management</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage book authors and their information</p>
        </div>
        <button 
          className="btn-primary" 
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
          onClick={openAddModal}
        >
          <Plus size={18} /> Add New Author
        </button>
      </div>

      {/* Search Bar */}
      <div className="premium-card" style={{ marginBottom: '1.5rem', padding: '1rem' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '1rem' }}>
          <div style={{ flex: 1, position: 'relative' }}>
            <Search size={20} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search authors by name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ 
                width: '100%', 
                paddingLeft: '2.5rem',
                padding: '0.75rem',
                borderRadius: '0.5rem',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-secondary)',
                color: 'var(--text-primary)'
              }}
            />
          </div>
          <button type="submit" className="btn-primary">Search</button>
          <button 
            type="button" 
            className="btn-secondary"
            onClick={() => { setSearchTerm(''); fetchAuthors(); }}
          >
            Clear
          </button>
        </form>
      </div>

      {/* Authors Table */}
      <div className="premium-card" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>ID</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Author Name</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Biography</th>
              <th style={{ textAlign: 'left', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Books</th>
              <th style={{ textAlign: 'right', padding: '1rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {authors.map((author) => (
              <tr key={author.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '1rem', fontWeight: 600 }}>#{author.id}</td>
                <td style={{ padding: '1rem', fontWeight: 500 }}>{author.name}</td>
                <td style={{ padding: '1rem', color: 'var(--text-muted)', maxWidth: '300px' }}>
                  {author.bio || <span style={{ fontStyle: 'italic' }}>No biography</span>}
                </td>
                <td style={{ padding: '1rem' }}>
                  {author.books && author.books.length > 0 ? (
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                      {author.books.map((book) => (
                        <span 
                          key={book.bookId} 
                          className="badge badge-info"
                          style={{ fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
                        >
                          <BookOpen size={12} />
                          {book.title}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>No books</span>
                  )}
                </td>
                <td style={{ padding: '1rem', textAlign: 'right' }}>
                  <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
                    <button 
                      onClick={() => openEditModal(author)}
                      style={{ 
                        background: 'transparent', 
                        border: 'none', 
                        color: 'var(--primary)', 
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.25rem',
                        fontSize: '0.875rem'
                      }}
                    >
                      <Edit2 size={16}/> Edit
                    </button>
                    <button 
                      onClick={() => deleteAuthor(author.id, author.name)}
                      style={{ 
                        background: 'transparent', 
                        border: 'none', 
                        color: 'var(--danger)', 
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.25rem',
                        fontSize: '0.875rem'
                      }}
                    >
                      <Trash2 size={16}/> Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {authors.length === 0 && (
        <div className="premium-card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ color: 'var(--text-muted)' }}>No authors found. Add your first author!</p>
        </div>
      )}

      {/* Add/Edit Modal */}
      {showModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.7)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
          padding: '1rem'
        }}>
          <div className="premium-card" style={{ 
            width: '100%', 
            maxWidth: '500px', 
            maxHeight: '90vh', 
            overflowY: 'auto',
            position: 'relative'
          }}>
            <button
              onClick={() => setShowModal(false)}
              style={{
                position: 'absolute',
                top: '1rem',
                right: '1rem',
                background: 'transparent',
                border: 'none',
                color: 'var(--text-muted)',
                cursor: 'pointer'
              }}
            >
              <X size={24} />
            </button>

            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '1.5rem' }}>
              {editingAuthor ? 'Edit Author' : 'Add New Author'}
            </h2>

            <form onSubmit={handleSubmit}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                  Author Name *
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                  placeholder="e.g., F. Scott Fitzgerald"
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
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                  Biography
                </label>
                <textarea
                  name="bio"
                  value={formData.bio}
                  onChange={handleInputChange}
                  rows="5"
                  placeholder="Brief biography of the author..."
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
                <button 
                  type="button"
                  className="btn-secondary"
                  onClick={() => setShowModal(false)}
                >
                  Cancel
                </button>
                <button 
                  type="submit"
                  className="btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Saving...' : (editingAuthor ? 'Update Author' : 'Save Author')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuthorManagement;
