import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { Plus, Edit2, Trash2, Image as ImageIcon, X, Search } from 'lucide-react';

const BookManagement = () => {
  const [books, setBooks] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [loading, setLoading] = useState(false);
  const [coverPreview, setCoverPreview] = useState(null);
  const [coverFile, setCoverFile] = useState(null);
  
  const categories = [
    { code: '000', name: 'Generalities', icon: '📚' },
    { code: '100', name: 'Philosophy', icon: '💭' },
    { code: '200', name: 'Religion', icon: '🙏' },
    { code: '300', name: 'Social Studies', icon: '👥' },
    { code: '400', name: 'Language', icon: '💬' },
    { code: '500', name: 'Natural Science and Mathematics', icon: '🔬' },
    { code: '600', name: 'Technology', icon: '⚙️' },
    { code: '700', name: 'Arts', icon: '🎨' },
    { code: '800', name: 'Literature', icon: '📖' },
    { code: '900', name: 'Geology and History', icon: '🌍' }
  ];
  
  const [formData, setFormData] = useState({
    title: '',
    authorId: '',
    isbn: '',
    category: '',
    totalCopies: 1,
    publisher: '',
    dateReceived: '',
    description: ''
  });

  useEffect(() => {
    fetchBooks();
    fetchAuthors();
  }, []);

  const fetchBooks = async (search = '') => {
    try {
      const params = search ? { search } : {};
      const res = await api.get('/staff/books', { params });
      // Ensure res.data is an array before setting state
      if (res.data && Array.isArray(res.data)) {
        setBooks(res.data);
      } else {
        console.error('Expected array from /staff/books, got:', res.data);
        setBooks([]);
      }
    } catch (error) {
      console.error('Error fetching books:', error);
      setBooks([]);
    }
  };

  const fetchAuthors = async () => {
    try {
      const res = await api.get('/staff/authors');
      // Ensure res.data is an array before setting state
      if (res.data && Array.isArray(res.data)) {
        setAuthors(res.data);
      } else {
        console.error('Expected array from /staff/authors, got:', res.data);
        setAuthors([]);
      }
    } catch (error) {
      console.error('Error fetching authors:', error);
      setAuthors([]);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchBooks(searchTerm);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setCoverFile(file);
      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setCoverPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const openAddModal = () => {
    setEditingBook(null);
    setFormData({
      title: '',
      authorId: '',
      isbn: '',
      category: '',
      totalCopies: 1,
      publisher: '',
      dateReceived: '',
      description: ''
    });
    setCoverPreview(null);
    setCoverFile(null);
    setShowModal(true);
  };

  const openEditModal = (book) => {
    setEditingBook(book);
    setFormData({
      title: book.title || '',
      authorId: book.author?.id || '',
      isbn: book.isbn || '',
      category: book.category || '',
      totalCopies: book.totalCopies || 1,
      publisher: book.publisher || '',
      dateReceived: book.dateReceived || '',
      description: book.description || ''
    });
    setCoverPreview(book.coverImage ? `http://localhost:8081${book.coverImage}` : null);
    setCoverFile(null);
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const data = new FormData();
      data.append('title', formData.title);
      data.append('authorId', formData.authorId);
      data.append('isbn', formData.isbn);
      data.append('category', formData.category);
      data.append('totalCopies', parseInt(formData.totalCopies));
      data.append('publisher', formData.publisher);
      data.append('dateReceived', formData.dateReceived);
      data.append('description', formData.description);
      
      if (coverFile) {
        data.append('file', coverFile);
      }

      if (editingBook) {
        await api.put(`/staff/books/${editingBook.id}`, data, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
      } else {
        await api.post('/staff/books', data, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
      }

      setShowModal(false);
      fetchBooks();
    } catch (error) {
      console.error('Error saving book:', error);
      alert('Failed to save book. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const deleteBook = async (id) => {
    if (window.confirm('Are you sure you want to delete this book? This action cannot be undone.')) {
      try {
        await api.delete(`/staff/books/${id}`);
        fetchBooks();
      } catch (error) {
        console.error('Error deleting book:', error);
        alert('Failed to delete book.');
      }
    }
  };

  const getAvailableCopies = (book) => {
    return book.availableCopies || 0;
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Book Inventory</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage your library collections</p>
        </div>
        <button 
          className="btn-primary" 
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
          onClick={openAddModal}
        >
          <Plus size={18} /> Add New Book
        </button>
      </div>

      {/* Search Bar */}
      <div className="premium-card" style={{ marginBottom: '1.5rem', padding: '1rem' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '1rem' }}>
          <div style={{ flex: 1, position: 'relative' }}>
            <Search size={20} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search by title or ISBN..."
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
            onClick={() => { setSearchTerm(''); fetchBooks(); }}
          >
            Clear
          </button>
        </form>
      </div>

      {/* Books Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
        {Array.isArray(books) && books.map((book) => (
          <div key={book.id} className="premium-card" style={{ display: 'flex', gap: '1rem', padding: '1rem' }}>
            <div style={{ 
              width: '100px', 
              height: '150px', 
              background: 'rgba(255,255,255,0.05)', 
              borderRadius: '0.5rem', 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center', 
              overflow: 'hidden',
              flexShrink: 0
            }}>
              {book.coverImage ? (
                <img 
                  src={`http://localhost:8081${book.coverImage}`} 
                  alt={book.title} 
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
                  onError={(e) => { e.target.style.display = 'none'; e.target.nextSibling.style.display = 'flex'; }}
                />
              ) : null}
              <ImageIcon size={32} color="var(--text-muted)" style={{ display: book.coverImage ? 'none' : 'flex' }} />
            </div>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <h4 style={{ fontSize: '1.1rem', marginBottom: '0.25rem', fontWeight: 600 }}>{book.title}</h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.25rem' }}>
                  Author: {book.author?.name || 'Unknown'}
                </p>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', marginBottom: '0.5rem' }}>
                  ISBN: {book.isbn || 'N/A'}
                </p>
                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                  <span className="badge badge-success" style={{ fontSize: '0.75rem' }}>
                    {getAvailableCopies(book)} available
                  </span>
                  <span className="badge badge-info" style={{ fontSize: '0.75rem' }}>
                    {book.totalCopies} total
                  </span>
                  {book.category && (
                    <span className="badge badge-warning" style={{ fontSize: '0.75rem' }}>
                      {book.category}
                    </span>
                  )}
                </div>
                {book.publisher && (
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', marginTop: '0.5rem' }}>
                    Publisher: {book.publisher}
                  </p>
                )}
              </div>
              <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.75rem' }}>
                <button 
                  onClick={() => openEditModal(book)}
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
                  onClick={() => deleteBook(book.id)}
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
            </div>
          </div>
        ))}
      </div>

      {(!books || books.length === 0) && (
        <div className="premium-card" style={{ textAlign: 'center', padding: '3rem' }}>
          <ImageIcon size={48} color="var(--text-muted)" style={{ marginBottom: '1rem' }} />
          <p style={{ color: 'var(--text-muted)' }}>No books found. Add your first book!</p>
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
            maxWidth: '600px', 
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
              {editingBook ? 'Edit Book' : 'Add New Book'}
            </h2>

            <form onSubmit={handleSubmit}>
              {/* Cover Image Upload */}
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                  Book Cover
                </label>
                <div style={{ 
                  width: '150px', 
                  height: '200px', 
                  border: '2px dashed var(--border-color)', 
                  borderRadius: '0.5rem', 
                  display: 'flex', 
                  flexDirection: 'column',
                  alignItems: 'center', 
                  justifyContent: 'center',
                  cursor: 'pointer',
                  overflow: 'hidden',
                  marginBottom: '0.5rem',
                  background: coverPreview ? 'transparent' : 'rgba(255,255,255,0.05)'
                }}>
                  {coverPreview ? (
                    <img 
                      src={coverPreview} 
                      alt="Cover preview" 
                      style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                    />
                  ) : (
                    <>
                      <ImageIcon size={32} color="var(--text-muted)" />
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
                        Select picture
                      </p>
                    </>
                  )}
                </div>
                <input
                  type="file"
                  accept="image/jpeg,image/png,image/jpg"
                  onChange={handleFileChange}
                  style={{ display: 'none' }}
                  id="cover-upload"
                />
                <label 
                  htmlFor="cover-upload"
                  className="btn-secondary"
                  style={{ display: 'inline-block', cursor: 'pointer' }}
                >
                  {coverPreview ? 'Change Picture' : 'Select Picture'}
                </label>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                    Title *
                  </label>
                  <input
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
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

                <div>
                  <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                    Author *
                  </label>
                  <select
                    name="authorId"
                    value={formData.authorId}
                    onChange={handleInputChange}
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
                    <option value="">Select Author</option>
                    {Array.isArray(authors) && authors.map(author => (
                      <option key={author.id} value={author.id}>{author.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                    ISBN
                  </label>
                  <input
                    type="text"
                    name="isbn"
                    value={formData.isbn}
                    onChange={handleInputChange}
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

              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', marginBottom: '0.75rem', fontWeight: 500 }}>
                  Category *
                </label>
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
                  gap: '0.75rem'
                }}>
                  {categories.map((cat) => (
                    <div
                      key={cat.code}
                      onClick={() => setFormData({ ...formData, category: `${cat.code} - ${cat.name}` })}
                      style={{
                        padding: '1rem',
                        borderRadius: '0.75rem',
                        border: formData.category === `${cat.code} - ${cat.name}` ? '2px solid var(--primary-color)' : '2px solid var(--border-color)',
                        background: formData.category === `${cat.code} - ${cat.name}` ? 'rgba(99, 102, 241, 0.1)' : 'var(--bg-secondary)',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                        textAlign: 'center',
                        boxShadow: formData.category === `${cat.code} - ${cat.name}` ? '0 4px 12px rgba(99, 102, 241, 0.3)' : 'none'
                      }}
                      onMouseEnter={(e) => {
                        if (formData.category !== `${cat.code} - ${cat.name}`) {
                          e.currentTarget.style.borderColor = 'var(--primary-color)';
                          e.currentTarget.style.transform = 'translateY(-2px)';
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (formData.category !== `${cat.code} - ${cat.name}`) {
                          e.currentTarget.style.borderColor = 'var(--border-color)';
                          e.currentTarget.style.transform = 'translateY(0)';
                        }
                      }}
                    >
                      <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
                        {cat.icon}
                      </div>
                      <div style={{ fontWeight: 600, fontSize: '0.875rem', color: 'var(--text-primary)' }}>
                        {cat.code}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                        {cat.name}
                      </div>
                    </div>
                  ))}
                </div>
                {formData.category && (
                  <div style={{ marginTop: '0.75rem', padding: '0.5rem 0.75rem', background: 'rgba(99, 102, 241, 0.1)', borderRadius: '0.5rem', fontSize: '0.875rem', color: 'var(--primary-color)' }}>
                    Selected: <strong>{formData.category}</strong>
                  </div>
                )}
              </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                    Total Copies *
                  </label>
                  <input
                    type="number"
                    name="totalCopies"
                    value={formData.totalCopies}
                    onChange={handleInputChange}
                    min="1"
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

                <div>
                  <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                    Publisher
                  </label>
                  <input
                    type="text"
                    name="publisher"
                    value={formData.publisher}
                    onChange={handleInputChange}
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
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>
                  Date Received
                </label>
                <input
                  type="date"
                  name="dateReceived"
                  value={formData.dateReceived}
                  onChange={handleInputChange}
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
                  Description
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  rows="3"
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
                  {loading ? 'Saving...' : (editingBook ? 'Update Book' : 'Save Book')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default BookManagement;
