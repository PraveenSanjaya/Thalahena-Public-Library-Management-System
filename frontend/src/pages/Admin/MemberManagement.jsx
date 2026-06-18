import React, { useEffect, useState } from 'react';
import { Plus, Edit2, Trash2, Search } from 'lucide-react';
import api from '../../services/api';

const MemberManagement = () => {
  const [members, setMembers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingMember, setEditingMember] = useState(null);
  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    password: '',
    birthDate: '',
    membershipDate: '',
    gender: '',
    phone: '',
    whatsapp: '',
    socialMedia: '',
    active: true
  });

  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      const response = await api.get('/admin/members');
      setMembers(response.data);
    } catch (error) {
      console.error('Error fetching members:', error);
    } finally {
      setLoading(false);
    }
  };

  const calculateAge = (birthDate) => {
    if (!birthDate) return '';
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    return age;
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchMembers();
      return;
    }
    try {
      const response = await api.get(`/admin/members/search?query=${searchTerm}`);
      setMembers(response.data);
    } catch (error) {
      console.error('Error searching members:', error);
    }
  };

  const handleOpenModal = (member = null) => {
    if (member) {
      setEditingMember(member);
      setFormData({
        firstName: member.firstName || '',
        lastName: member.lastName || '',
        username: member.username || '',
        email: member.email || '',
        password: '',
        birthDate: member.birthDate || '',
        membershipDate: member.membershipDate || '',
        gender: member.gender || '',
        phone: member.phone || '',
        whatsapp: member.whatsapp || '',
        socialMedia: member.socialMedia || '',
        active: member.isActive !== undefined ? member.isActive : true
      });
    } else {
      setEditingMember(null);
      setFormData({
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        password: '',
        birthDate: '',
        membershipDate: new Date().toISOString().split('T')[0],
        gender: '',
        phone: '',
        whatsapp: '',
        socialMedia: '',
        active: true
      });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Transform formData to match backend User entity structure
      // Convert empty strings to null for date fields (Jackson can't deserialize '' into LocalDate)
      const memberData = {
        ...formData,
        active: formData.active,
        birthDate: formData.birthDate || null,
        membershipDate: formData.membershipDate || null,
      };
      
      console.log('Sending member data:', memberData);
      
      if (editingMember) {
        await api.put(`/admin/members/${editingMember.id}`, memberData);
      } else {
        await api.post('/admin/members', memberData);
      }
      setShowModal(false);
      fetchMembers();
    } catch (error) {
      console.error('Error saving member:', error);
      alert('Error saving member: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this member?')) return;
    try {
      await api.delete(`/admin/members/${id}`);
      fetchMembers();
    } catch (error) {
      console.error('Error deleting member:', error);
      alert('Error deleting member');
    }
  };

  const handleToggleStatus = async (id) => {
    try {
      await api.put(`/admin/members/${id}/status`);
      fetchMembers();
    } catch (error) {
      console.error('Error toggling member status:', error);
      alert('Error updating member status');
    }
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Member Management</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage library members</p>
        </div>
        <button
          onClick={() => handleOpenModal()}
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <Plus size={20} />
          Add Member
        </button>
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
                <th style={tableHeaderStyle}>First Name</th>
                <th style={tableHeaderStyle}>Last Name</th>
                <th style={tableHeaderStyle}>Username</th>
                <th style={tableHeaderStyle}>Birth Date</th>
                <th style={tableHeaderStyle}>Age</th>
                <th style={tableHeaderStyle}>Gender</th>
                <th style={tableHeaderStyle}>Email</th>
                <th style={tableHeaderStyle}>WhatsApp</th>
                <th style={tableHeaderStyle}>Phone</th>
                <th style={tableHeaderStyle}>Home Address</th>
                <th style={tableHeaderStyle}>Status</th>
                <th style={tableHeaderStyle}>Membership Date</th>
                <th style={tableHeaderStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {members.map((member) => (
                <tr key={member.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <td style={tableCellStyle}>{member.id}</td>
                  <td style={tableCellStyle}>{member.firstName || '-'}</td>
                  <td style={tableCellStyle}>{member.lastName || '-'}</td>
                  <td style={{ ...tableCellStyle, fontWeight: 600, color: 'var(--primary)' }}>{member.username || '-'}</td>
                  <td style={tableCellStyle}>{member.birthDate || '-'}</td>
                  <td style={tableCellStyle}>{calculateAge(member.birthDate)}</td>
                  <td style={tableCellStyle}>{member.gender || '-'}</td>
                  <td style={tableCellStyle}>{member.email}</td>
                  <td style={tableCellStyle}>{member.whatsapp || '-'}</td>
                  <td style={tableCellStyle}>{member.phone || '-'}</td>
                  <td style={tableCellStyle}>{member.socialMedia || '-'}</td>
                  <td style={tableCellStyle}>
                    <button
                      onClick={() => handleToggleStatus(member.id)}
                      style={{
                        padding: '0.35rem 0.75rem',
                        borderRadius: '0.375rem',
                        border: 'none',
                        cursor: 'pointer',
                        fontSize: '0.75rem',
                        fontWeight: 600,
                        background: member.isActive ? '#10b981' : '#f43f5e',
                        color: 'white'
                      }}
                    >
                      {member.isActive ? 'Active' : 'Inactive'}
                    </button>
                  </td>
                  <td style={tableCellStyle}>{member.membershipDate || '-'}</td>
                  <td style={tableCellStyle}>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button onClick={() => handleOpenModal(member)} className="btn btn-sm btn-primary">
                        <Edit2 size={16} />
                      </button>
                      <button onClick={() => handleDelete(member.id)} className="btn btn-sm btn-danger">
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div style={modalOverlayStyle} onClick={() => setShowModal(false)}>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginBottom: '1.5rem' }}>{editingMember ? 'Edit Member' : 'Add New Member'}</h2>
            <form onSubmit={handleSubmit}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>First Name *</label>
                  <input
                    type="text"
                    placeholder="Enter first name"
                    required
                    value={formData.firstName}
                    onChange={(e) => {
                      const val = e.target.value;
                      const currentLastName = formData.lastName;
                      const suggestedUser = editingMember 
                        ? formData.username 
                        : `${val.trim().toLowerCase()}.${currentLastName.trim().toLowerCase()}`.replace(/\s+/g, '');
                      setFormData({ ...formData, firstName: val, username: suggestedUser });
                    }}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Last Name *</label>
                  <input
                    type="text"
                    placeholder="Enter last name"
                    required
                    value={formData.lastName}
                    onChange={(e) => {
                      const val = e.target.value;
                      const currentFirstName = formData.firstName;
                      const suggestedUser = editingMember 
                        ? formData.username 
                        : `${currentFirstName.trim().toLowerCase()}.${val.trim().toLowerCase()}`.replace(/\s+/g, '');
                      setFormData({ ...formData, lastName: val, username: suggestedUser });
                    }}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Username *</label>
                  <input
                    type="text"
                    placeholder="Enter username"
                    required
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Email *</label>
                  <input
                    type="email"
                    placeholder="Enter email address"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                {!editingMember && (
                  <div style={formGroupStyle}>
                    <label style={labelStyle}>Password *</label>
                    <input
                      type="password"
                      placeholder="Enter password"
                      required
                      value={formData.password}
                      onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                      style={{ ...inputStyle, width: '100%' }}
                    />
                  </div>
                )}
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Birth Date</label>
                  <input
                    type="date"
                    value={formData.birthDate}
                    onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Membership Date</label>
                  <input
                    type="date"
                    value={formData.membershipDate}
                    onChange={(e) => setFormData({ ...formData, membershipDate: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Gender</label>
                  <select
                    value={formData.gender}
                    onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  >
                    <option value="">Select Gender</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Status</label>
                  <select
                    value={formData.active ? 'true' : 'false'}
                    onChange={(e) => setFormData({ ...formData, active: e.target.value === 'true' })}
                    style={{ ...inputStyle, width: '100%' }}
                  >
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Phone</label>
                  <input
                    type="text"
                    placeholder="Enter phone number"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>WhatsApp</label>
                  <input
                    type="text"
                    placeholder="Enter WhatsApp number"
                    value={formData.whatsapp}
                    onChange={(e) => setFormData({ ...formData, whatsapp: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Home Address</label>
                  <input
                    type="text"
                    placeholder="Enter home address"
                    value={formData.socialMedia}
                    onChange={(e) => setFormData({ ...formData, socialMedia: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                  />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
                <button type="submit" className="btn btn-primary">
                  {editingMember ? 'Update' : 'Create'}
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
  maxWidth: '700px',
  width: '90%',
  maxHeight: '90vh',
  overflow: 'auto'
};

const inputStyle = {
  padding: '0.75rem',
  background: 'var(--bg-secondary)',
  border: '1px solid var(--border-color)',
  borderRadius: '0.5rem',
  color: 'var(--text-primary)',
  fontSize: '0.875rem'
};

const formGroupStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '0.35rem'
};

const labelStyle = {
  fontSize: '0.8rem',
  fontWeight: 600,
  color: 'var(--text-muted)',
  paddingLeft: '0.1rem',
  textAlign: 'left'
};

export default MemberManagement;
