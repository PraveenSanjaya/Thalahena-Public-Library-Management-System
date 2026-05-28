import React, { useEffect, useState } from 'react';
import { Plus, Edit2, Trash2 } from 'lucide-react';
import api from '../../services/api';

const RegistrationManagement = () => {
  const [users, setUsers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    phone: '',
    whatsapp: '',
    password: '',
    role: 'STAFF',
    isActive: true
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await api.get('/admin/registrations');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (user = null) => {
    if (user) {
      setEditingUser(user);
      const userActive = user.isActive !== undefined ? user.isActive : (user.active !== undefined ? user.active : true);
      setFormData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        username: user.username || '',
        email: user.email || '',
        phone: user.phone || '',
        whatsapp: user.whatsapp || '',
        password: '',
        role: user.role || 'STAFF',
        isActive: userActive
      });
    } else {
      setEditingUser(null);
      setFormData({
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        phone: '',
        whatsapp: '',
        password: '',
        role: 'STAFF',
        isActive: true
      });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      active: formData.isActive
    };
    try {
      if (editingUser) {
        await api.put(`/admin/registrations/${editingUser.id}`, payload);
      } else {
        await api.post('/admin/registrations', payload);
      }
      setShowModal(false);
      fetchUsers();
    } catch (error) {
      console.error('Error saving user:', error);
      alert('Error saving user: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this user?')) return;
    try {
      await api.delete(`/admin/registrations/${id}`);
      fetchUsers();
    } catch (error) {
      console.error('Error deleting user:', error);
      alert('Error deleting user');
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Registration Management</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage staff and admin accounts</p>
        </div>
        <button
          onClick={() => handleOpenModal()}
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <Plus size={20} />
          Add User
        </button>
      </div>

      {/* Users Table */}
      <div className="premium-card">
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                <th style={tableHeaderStyle}>ID</th>
                <th style={tableHeaderStyle}>First Name</th>
                <th style={tableHeaderStyle}>Last Name</th>
                <th style={tableHeaderStyle}>Username</th>
                <th style={tableHeaderStyle}>Email</th>
                <th style={tableHeaderStyle}>Phone</th>
                <th style={tableHeaderStyle}>Role</th>
                <th style={tableHeaderStyle}>Status</th>
                <th style={tableHeaderStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => {
                const isActive = user.isActive !== undefined ? user.isActive : user.active;
                return (
                  <tr key={user.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td style={tableCellStyle}>{user.id}</td>
                    <td style={tableCellStyle}>{user.firstName || '-'}</td>
                    <td style={tableCellStyle}>{user.lastName || '-'}</td>
                    <td style={tableCellStyle}>{user.username}</td>
                    <td style={tableCellStyle}>{user.email}</td>
                    <td style={tableCellStyle}>{user.phone || '-'}</td>
                    <td style={tableCellStyle}>
                      <span style={{
                        display: 'inline-block',
                        padding: '0.25rem 0.75rem',
                        borderRadius: '50px',
                        background: user.role === 'ADMIN' ? '#ede9fe' : '#e0f2fe',
                        color: user.role === 'ADMIN' ? '#6d28d9' : '#0369a1',
                        border: user.role === 'ADMIN' ? '1px solid #ddd6fe' : '1px solid #bae6fd',
                        fontSize: '0.75rem',
                        fontWeight: 600
                      }}>
                        {user.role}
                      </span>
                    </td>
                    <td style={tableCellStyle}>
                      <span style={{
                        display: 'inline-block',
                        padding: '0.25rem 0.75rem',
                        borderRadius: '50px',
                        background: isActive ? '#d1fae5' : '#fee2e2',
                        color: isActive ? '#065f46' : '#991b1b',
                        border: isActive ? '1px solid #a7f3d0' : '1px solid #fecaca',
                        fontSize: '0.75rem',
                        fontWeight: 600
                      }}>
                        {isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td style={tableCellStyle}>
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button onClick={() => handleOpenModal(user)} className="btn btn-sm btn-primary">
                          <Edit2 size={16} />
                        </button>
                        <button onClick={() => handleDelete(user.id)} className="btn btn-sm btn-danger">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div style={modalOverlayStyle} onClick={() => setShowModal(false)}>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginBottom: '1.5rem', color: '#0f172a', fontWeight: 700 }}>
              {editingUser ? 'Edit User' : 'Create New User'}
            </h2>
            <form onSubmit={handleSubmit}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem' }}>
                
                {/* ID field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>ID</label>
                  <input
                    type="text"
                    disabled
                    value={editingUser ? editingUser.id : 'Auto-generated'}
                    style={{ ...inputStyle, background: '#f1f5f9', cursor: 'not-allowed', color: '#64748b' }}
                  />
                </div>

                {/* Username field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Username *</label>
                  <input
                    type="text"
                    placeholder="Enter username"
                    required
                    disabled={!!editingUser}
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    style={{ 
                      ...inputStyle, 
                      background: editingUser ? '#f1f5f9' : '#f8fafc',
                      cursor: editingUser ? 'not-allowed' : 'text',
                      color: editingUser ? '#64748b' : '#0f172a'
                    }}
                  />
                </div>

                {/* First Name field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>First Name *</label>
                  <input
                    type="text"
                    placeholder="Enter first name"
                    required
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                    style={inputStyle}
                  />
                </div>

                {/* Last Name field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Last Name *</label>
                  <input
                    type="text"
                    placeholder="Enter last name"
                    required
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                    style={inputStyle}
                  />
                </div>

                {/* Email field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Email *</label>
                  <input
                    type="email"
                    placeholder="Enter email address"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    style={inputStyle}
                  />
                </div>

                {/* Phone field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Phone</label>
                  <input
                    type="text"
                    placeholder="Enter phone number"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    style={inputStyle}
                  />
                </div>

                {/* WhatsApp field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>WhatsApp</label>
                  <input
                    type="text"
                    placeholder="Enter WhatsApp number"
                    value={formData.whatsapp}
                    onChange={(e) => setFormData({ ...formData, whatsapp: e.target.value })}
                    style={inputStyle}
                  />
                </div>

                {/* Password field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>
                    {editingUser ? 'Password (leave blank to keep current)' : 'Password *'}
                  </label>
                  <input
                    type="password"
                    placeholder={editingUser ? "Leave blank to keep current" : "Enter password (min 8 chars)"}
                    required={!editingUser}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    style={inputStyle}
                  />
                </div>

                {/* Role field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Role *</label>
                  <select
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                    style={inputStyle}
                    required
                  >
                    <option value="STAFF">Staff</option>
                    <option value="ADMIN">Admin</option>
                  </select>
                </div>

                {/* Status field */}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Status *</label>
                  <select
                    value={formData.isActive ? 'true' : 'false'}
                    onChange={(e) => setFormData({ ...formData, isActive: e.target.value === 'true' })}
                    style={inputStyle}
                    required
                  >
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </div>

              </div>

              {/* Actions Section */}
              <div style={{ ...formGroupStyle, marginTop: '1.5rem', borderTop: '1px solid #e2e8f0', paddingTop: '1.25rem' }}>
                <label style={{ ...labelStyle, fontSize: '0.85rem', color: '#1e293b', marginBottom: '0.25rem' }}>Actions</label>
                <div style={{ display: 'flex', gap: '1rem' }}>
                  <button type="submit" className="btn btn-primary" style={{ padding: '0.6rem 1.5rem', fontSize: '0.875rem' }}>
                    {editingUser ? 'Update' : 'Create'}
                  </button>
                  <button type="button" onClick={() => setShowModal(false)} className="btn btn-secondary" style={{ padding: '0.6rem 1.5rem', fontSize: '0.875rem' }}>
                    Cancel
                  </button>
                </div>
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
  background: 'rgba(15, 23, 42, 0.65)',
  backdropFilter: 'none',
  WebkitBackdropFilter: 'none',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000
};

const modalContentStyle = {
  background: '#ffffff',
  color: '#0f172a',
  padding: '2rem',
  borderRadius: '1rem',
  maxWidth: '700px',
  width: '95%',
  maxHeight: '90vh',
  overflow: 'auto',
  boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
  border: '1px solid #e2e8f0'
};

const inputStyle = {
  padding: '0.75rem',
  background: '#f8fafc',
  border: '1px solid #cbd5e1',
  borderRadius: '0.5rem',
  color: '#0f172a',
  fontSize: '0.875rem',
  width: '100%',
  outline: 'none',
  boxSizing: 'border-box'
};

const formGroupStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '0.35rem'
};

const labelStyle = {
  fontSize: '0.8rem',
  fontWeight: 600,
  color: '#475569',
  paddingLeft: '0.1rem',
  textAlign: 'left'
};

export default RegistrationManagement;
