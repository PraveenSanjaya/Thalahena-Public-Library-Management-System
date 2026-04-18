import React, { useEffect, useState } from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';
import { Users, UserCheck, UserX, Shield, MessageSquare } from 'lucide-react';
import api from '../../services/api';
import authService from '../../services/auth.service';

ChartJS.register(ArcElement, Tooltip, Legend);

const AdminDashboard = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    staffCount: 0,
    adminCount: 0,
    activeMembers: 0,
    inactiveMembers: 0,
    genderDistribution: {},
    ageDistribution: {},
    recentFeedback: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await api.get('/admin/stats');
        setStats(response.data);
      } catch (error) {
        console.error('Error fetching admin stats:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const genderChartData = {
    labels: Object.keys(stats.genderDistribution),
    datasets: [
      {
        data: Object.values(stats.genderDistribution),
        backgroundColor: ['#3B82F6', '#EC4899', '#F59E0B'],
        borderWidth: 0,
      },
    ],
  };

  const ageChartData = {
    labels: Object.keys(stats.ageDistribution),
    datasets: [
      {
        data: Object.values(stats.ageDistribution),
        backgroundColor: ['#10B981', '#EF4444'],
        borderWidth: 0,
      },
    ],
  };

  if (loading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Welcome Admin {authService.getCurrentUser()?.username}</h1>
        <p style={{ color: 'var(--text-muted)' }}>Overview of the library system</p>
      </div>

      {/* Stats Grid */}
      <div className="stat-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Users</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0 }}>{stats.totalUsers}</h3>
            </div>
            <Users size={32} color="var(--primary)" />
          </div>
        </div>
        
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Staff Members</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0 }}>{stats.staffCount}</h3>
            </div>
            <UserCheck size={32} color="var(--success)" />
          </div>
        </div>
        
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Administrators</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0 }}>{stats.adminCount}</h3>
            </div>
            <Shield size={32} color="var(--danger)" />
          </div>
        </div>
        
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Active Members</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--success)' }}>{stats.activeMembers}</h3>
            </div>
            <UserCheck size={32} color="var(--success)" />
          </div>
        </div>
      </div>

      {/* Charts Section */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="premium-card">
          <h4 style={{ marginBottom: '1.5rem' }}>Gender Distribution</h4>
          <div style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Pie data={genderChartData} />
          </div>
        </div>

        <div className="premium-card">
          <h4 style={{ marginBottom: '1.5rem' }}>Age Distribution</h4>
          <div style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Pie data={ageChartData} />
          </div>
        </div>
      </div>

      {/* Recent Feedback */}
      <div className="premium-card">
        <h4 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <MessageSquare size={20} />
          Recent Member Feedback
        </h4>
        {stats.recentFeedback.length > 0 ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {stats.recentFeedback.map((feedback) => (
              <div key={feedback.id} style={{ 
                padding: '1rem', 
                background: 'rgba(255,255,255,0.02)', 
                borderRadius: '0.5rem',
                borderLeft: '3px solid var(--primary)'
              }}>
                <p style={{ margin: '0 0 0.5rem 0', lineHeight: 1.6 }}>{feedback.content}</p>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  <span>By: {feedback.userName}</span>
                  <span>{new Date(feedback.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>No feedback yet</p>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
