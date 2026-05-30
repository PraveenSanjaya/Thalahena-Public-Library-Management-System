import React, { useEffect, useState } from 'react';
import { MessageSquare, Plus, Edit3, Trash2, Send, Clock, User, ShieldAlert } from 'lucide-react';
import api from '../../services/api';
import authService from '../../services/auth.service';

const Feedback = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [message, setMessage] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editText, setEditText] = useState('');
  const [loading, setLoading] = useState(true);
  
  const currentUser = authService.getCurrentUser();
  const isMember = currentUser?.role === 'MEMBER';
  const isStaffOrAdmin = currentUser?.role === 'STAFF' || currentUser?.role === 'ADMIN';

  useEffect(() => {
    fetchFeedbacks();
  }, []);

  const fetchFeedbacks = async () => {
    try {
      const response = await api.get('/feedback');
      setFeedbacks(response.data);
    } catch (error) {
      console.error('Error fetching feedback:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!message.trim()) return;

    try {
      await api.post('/feedback', { message });
      setMessage('');
      fetchFeedbacks();
      alert('Feedback submitted successfully!');
    } catch (error) {
      console.error('Error submitting feedback:', error);
      alert('Failed to submit feedback');
    }
  };

  const handleEditInit = (fb) => {
    setEditingId(fb.id);
    setEditText(fb.message);
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!editText.trim()) return;

    try {
      await api.put(`/feedback/${editingId}`, { message: editText });
      setEditingId(null);
      setEditText('');
      fetchFeedbacks();
      alert('Feedback updated successfully!');
    } catch (error) {
      console.error('Error updating feedback:', error);
      alert('Failed to update feedback');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this feedback?')) return;

    try {
      await api.delete(`/feedback/${id}`);
      fetchFeedbacks();
      alert('Feedback deleted successfully!');
    } catch (error) {
      console.error('Error deleting feedback:', error);
      alert('Failed to delete feedback');
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <div style={{ color: 'var(--primary)', fontWeight: 600 }}>Loading feedback stream...</div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', paddingBottom: '3rem' }}>
      {/* Header Section */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <MessageSquare className="text-primary" size={24} />
          {isMember ? 'Feedback & Suggestions' : 'Member Feedback Directory'}
        </h1>
        <p style={{ color: 'var(--text-muted)' }}>
          {isMember 
            ? 'Submit suggestions or feedback on how we can improve our services and library facilities.' 
            : 'Explore, review, and evaluate feedback submitted by library members.'}
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: isMember ? '400px 1fr' : '1fr', gap: '2rem' }}>
        {/* Left Column: Create Form (Members Only) */}
        {isMember && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            <div className="premium-card" style={{ position: 'sticky', top: '2rem' }}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Plus size={18} style={{ color: 'var(--primary)' }} />
                Submit New Feedback
              </h3>
              <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.5rem', display: 'block', fontWeight: 500 }}>
                    What can we improve?
                  </label>
                  <textarea
                    placeholder="Describe your suggestions, concerns, or appreciation..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    required
                    rows={6}
                    style={{ resize: 'none', fontSize: '0.875rem' }}
                  />
                </div>
                <button type="submit" className="btn-primary" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}>
                  Send Feedback
                  <Send size={14} />
                </button>
              </form>
            </div>
          </div>
        )}

        {/* Right Column: Feedback List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <h3 style={{ fontSize: '1.15rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-main)' }}>
            <MessageSquare size={18} className="text-primary" />
            {isMember ? 'My Submitted Feedbacks' : 'All Member Feedbacks'}
          </h3>

          {feedbacks.length > 0 ? (
            feedbacks.map((fb) => (
              <div key={fb.id} className="premium-card" style={{ padding: '1.5rem', borderLeft: '4px solid var(--primary)' }}>
                {editingId === fb.id ? (
                  /* Inline Edit Form */
                  <form onSubmit={handleUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <textarea
                      value={editText}
                      onChange={(e) => setEditText(e.target.value)}
                      required
                      rows={4}
                      style={{ fontSize: '0.875rem', resize: 'none' }}
                    />
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button type="submit" className="btn btn-sm btn-primary" style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem' }}>Save</button>
                      <button type="button" onClick={() => setEditingId(null)} className="btn btn-sm btn-secondary" style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem' }}>Cancel</button>
                    </div>
                  </form>
                ) : (
                  /* View Mode */
                  <div>
                    {/* Header info */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <div style={{
                          width: '32px',
                          height: '32px',
                          borderRadius: '50%',
                          background: 'rgba(99, 102, 241, 0.1)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: 'var(--primary)'
                        }}>
                          <User size={16} />
                        </div>
                        <div>
                          <p style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--text-main)' }}>
                            {fb.user?.username || 'Unknown Member'}
                          </p>
                          {isStaffOrAdmin && fb.user?.email && (
                            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                              {fb.user.email}
                            </p>
                          )}
                        </div>
                      </div>

                      {/* Action buttons (Members only, and only if it belongs to them) */}
                      {isMember && (
                        <div style={{ display: 'flex', gap: '0.25rem' }}>
                          <button 
                            onClick={() => handleEditInit(fb)} 
                            style={{ background: 'transparent', border: 'none', color: 'var(--primary)', cursor: 'pointer', padding: '0.25rem' }}
                            title="Edit Feedback"
                          >
                            <Edit3 size={16} />
                          </button>
                          <button 
                            onClick={() => handleDelete(fb.id)} 
                            style={{ background: 'transparent', border: 'none', color: 'var(--danger)', cursor: 'pointer', padding: '0.25rem' }}
                            title="Delete Feedback"
                          >
                            <Trash2 size={16} />
                          </button>
                        </div>
                      )}
                    </div>

                    {/* Message content */}
                    <p style={{ fontSize: '0.925rem', lineHeight: 1.6, color: 'var(--text-main)', marginBottom: '0.75rem', whiteSpace: 'pre-wrap' }}>
                      {fb.message}
                    </p>

                    {/* Footer info */}
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      <Clock size={12} />
                      <span>Submitted: {new Date(fb.createdAt).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' })}</span>
                    </div>
                  </div>
                )}
              </div>
            ))
          ) : (
            <div className="premium-card" style={{ textAlign: 'center', padding: '3.5rem 2rem', color: 'var(--text-muted)' }}>
              <MessageSquare size={40} style={{ opacity: 0.3, marginBottom: '1rem', color: 'var(--primary)' }} />
              <p style={{ fontWeight: 500 }}>No feedback entries found</p>
              <p style={{ fontSize: '0.85rem', marginTop: '0.25rem' }}>
                {isMember ? 'You have not submitted any feedback yet.' : 'Members have not submitted any feedback entries yet.'}
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Feedback;
