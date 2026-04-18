import React, { useEffect, useState } from 'react';
import { Plus, Edit2, Trash2 } from 'lucide-react';
import api from '../../services/api';

const AboutManagement = () => {
  const [statements, setStatements] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingStatement, setEditingStatement] = useState(null);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStatements();
  }, []);

  const fetchStatements = async () => {
    try {
      const response = await api.get('/admin/about');
      setStatements(response.data);
    } catch (error) {
      console.error('Error fetching about statements:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (statement = null) => {
    if (statement) {
      setEditingStatement(statement);
      setContent(statement.content);
    } else {
      setEditingStatement(null);
      setContent('');
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingStatement) {
        await api.put(`/admin/about/${editingStatement.id}`, { content });
      } else {
        await api.post('/admin/about', { content });
      }
      setShowModal(false);
      fetchStatements();
    } catch (error) {
      console.error('Error saving statement:', error);
      alert('Error saving statement');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this statement?')) return;
    try {
      await api.delete(`/admin/about/${id}`);
      fetchStatements();
    } catch (error) {
      console.error('Error deleting statement:', error);
      alert('Error deleting statement');
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>About Statements</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage library announcements and information</p>
        </div>
        <button
          onClick={() => handleOpenModal()}
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <Plus size={20} />
          Add Statement
        </button>
      </div>

      {/* Statements List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {statements.length > 0 ? (
          statements.map((statement) => (
            <div key={statement.id} className="premium-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ flex: 1 }}>
                  <p style={{ lineHeight: 1.6, marginBottom: '0.75rem' }}>{statement.content}</p>
                  <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    Last updated: {new Date(statement.updatedAt).toLocaleString()}
                  </p>
                </div>
                <div style={{ display: 'flex', gap: '0.5rem', marginLeft: '1rem' }}>
                  <button onClick={() => handleOpenModal(statement)} className="btn btn-sm btn-primary">
                    <Edit2 size={16} />
                  </button>
                  <button onClick={() => handleDelete(statement.id)} className="btn btn-sm btn-danger">
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="premium-card" style={{ textAlign: 'center', padding: '3rem' }}>
            <p style={{ color: 'var(--text-muted)' }}>No statements yet. Click "Add Statement" to create one.</p>
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div style={modalOverlayStyle} onClick={() => setShowModal(false)}>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginBottom: '1.5rem' }}>
              {editingStatement ? 'Edit Statement' : 'Add New Statement'}
            </h2>
            <form onSubmit={handleSubmit}>
              <textarea
                placeholder="Enter statement content..."
                value={content}
                onChange={(e) => setContent(e.target.value)}
                required
                rows={6}
                style={{
                  width: '100%',
                  padding: '1rem',
                  background: 'var(--bg-secondary)',
                  border: '1px solid var(--border-color)',
                  borderRadius: '0.5rem',
                  color: 'var(--text-primary)',
                  fontSize: '0.875rem',
                  resize: 'vertical',
                  fontFamily: 'inherit'
                }}
              />
              <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                <button type="submit" className="btn btn-primary">
                  {editingStatement ? 'Update' : 'Create'}
                </button>
                <button type="button" onClick={() => setShowModal(false)} className="btn btn-secondary">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
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
  maxWidth: '600px',
  width: '90%',
  maxHeight: '90vh',
  overflow: 'auto'
};

export default AboutManagement;
