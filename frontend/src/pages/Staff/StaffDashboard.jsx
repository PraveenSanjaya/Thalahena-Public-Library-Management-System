import React, { useEffect, useState } from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';
import { Book, TrendingUp, Bookmark, DollarSign } from 'lucide-react';
import api from '../../services/api';
import authService from '../../services/auth.service';

ChartJS.register(ArcElement, Tooltip, Legend);

// Dewey Decimal category colors
const CATEGORY_COLORS = {
  '000': '#3B82F6',  // Generalities - Blue
  '100': '#EC4899',  // Philosophy - Rose
  '200': '#F97316',  // Religion - Orange
  '300': '#A855F7',  // Social Studies - Purple
  '400': '#10B981',  // Language - Green
  '500': '#92400E',  // Natural Science - Brown
  '600': '#EF4444',  // Technology - Red
  '700': '#1E3A8A',  // Arts - Dark Blue
  '800': '#EAB308',  // Literature - Yellow
  '900': '#84CC16',  // History/Geography - Light Green
};

const StaffDashboard = () => {
  const [stats, setStats] = useState({
    totalBooks: 0,
    booksBorrowed: 0,
    activeReservations: 0,
    totalFines: 0,
    categoryCounts: [],
    top5Books: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await api.get('/staff/dashboard/stats');
        setStats(response.data);
      } catch (error) {
        console.error('Error fetching staff stats:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  // Prepare pie chart data
  const categoryChartData = {
    labels: stats.categoryCounts.map(cat => `${cat.code} - ${cat.category}`),
    datasets: [
      {
        data: stats.categoryCounts.map(cat => cat.count),
        backgroundColor: stats.categoryCounts.map(
          cat => CATEGORY_COLORS[cat.code] || '#6B7280'
        ),
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
        <h1 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Welcome Staff {authService.getCurrentUser()?.username}</h1>
        <p style={{ color: 'var(--text-muted)' }}>Quick statistics for today</p>
      </div>

      {/* KPI Cards */}
      <div className="stat-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Books</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0 }}>{stats.totalBooks}</h3>
            </div>
            <Book size={32} color="var(--primary)" />
          </div>
        </div>
        
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Books Borrowed</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--warning)' }}>{stats.booksBorrowed}</h3>
            </div>
            <TrendingUp size={32} color="var(--warning)" />
          </div>
        </div>
        
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Active Reservations</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--primary)' }}>{stats.activeReservations}</h3>
            </div>
            <Bookmark size={32} color="var(--primary)" />
          </div>
        </div>
        
        <div className="premium-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>Total Unpaid Fines</p>
              <h3 style={{ fontSize: '2rem', fontWeight: 700, margin: 0, color: 'var(--danger)' }}>Rs. {stats.totalFines?.toFixed(2)}</h3>
            </div>
            <DollarSign size={32} color="var(--danger)" />
          </div>
        </div>
      </div>

      {/* Charts and Top Books */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
        <div className="premium-card">
          <h4 style={{ marginBottom: '1.5rem' }}>Books by Category (Dewey Decimal)</h4>
          <div style={{ height: '350px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            {stats.categoryCounts.length > 0 ? (
              <Pie 
                data={categoryChartData} 
                options={{
                  plugins: {
                    legend: {
                      position: 'right',
                      labels: {
                        padding: 15,
                        font: {
                          size: 12
                        }
                      }
                    }
                  }
                }}
              />
            ) : (
              <p style={{ color: 'var(--text-muted)' }}>No category data available</p>
            )}
          </div>
        </div>

        <div className="premium-card">
          <h4 style={{ marginBottom: '1.5rem' }}>Top 5 Most Borrowed Books</h4>
          {stats.top5Books.length > 0 ? (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                    <th style={{ textAlign: 'left', padding: '0.75rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>ID</th>
                    <th style={{ textAlign: 'left', padding: '0.75rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Title</th>
                    <th style={{ textAlign: 'right', padding: '0.75rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>Borrows</th>
                  </tr>
                </thead>
                <tbody>
                  {stats.top5Books.map((book, index) => (
                    <tr key={book.bookId} style={{ borderBottom: '1px solid var(--border-color)' }}>
                      <td style={{ padding: '0.75rem' }}>{book.bookId}</td>
                      <td style={{ padding: '0.75rem', fontWeight: 500 }}>{book.title}</td>
                      <td style={{ padding: '0.75rem', textAlign: 'right', fontWeight: 600, color: 'var(--primary)' }}>{book.borrowCount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>No borrowing data yet</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default StaffDashboard;
