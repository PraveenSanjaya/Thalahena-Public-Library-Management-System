import React, { useEffect, useState } from 'react';
import { BookOpen, Calendar, Info, Shield, Layers } from 'lucide-react';
import api from '../services/api';

const About = () => {
  const [statements, setStatements] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStatements();
  }, []);

  const fetchStatements = async () => {
    try {
      const response = await api.get('/about');
      setStatements(response.data);
    } catch (error) {
      console.error('Error fetching about statements:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
        <div style={{ color: 'var(--primary)', fontWeight: 600 }}>Loading...</div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto', paddingBottom: '3rem' }}>
      {/* Hero Header */}
      <div className="premium-card" style={{
        background: 'linear-gradient(135deg, var(--primary) 0%, #4338ca 100%)',
        color: 'white',
        border: 'none',
        borderRadius: '1.5rem',
        padding: '3rem 2rem',
        marginBottom: '2.5rem',
        boxShadow: '0 10px 30px rgba(99, 102, 241, 0.25)',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div style={{ position: 'absolute', right: '-20px', bottom: '-20px', opacity: 0.1, pointerEvents: 'none' }}>
          <BookOpen size={240} />
        </div>
        <div style={{ position: 'relative', zIndex: 1 }}>
          <span style={{
            background: 'rgba(255, 255, 255, 0.15)',
            padding: '0.4rem 1rem',
            borderRadius: '2rem',
            fontSize: '0.85rem',
            fontWeight: 600,
            letterSpacing: '1px',
            textTransform: 'uppercase',
            display: 'inline-block',
            marginBottom: '1rem'
          }}>
            Welcome to Thalahena
          </span>
          <h1 style={{ fontSize: '2.5rem', fontWeight: 800, marginBottom: '0.75rem', lineHeight: 1.2 }}>
            Thalahena Public Library
          </h1>
          <p style={{ fontSize: '1.1rem', opacity: 0.9, maxWidth: '600px', lineHeight: 1.6 }}>
            Empowering our community through knowledge, education, and standard facilities. Explore our statements and staff information below.
          </p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: '2rem' }}>
        {/* Left Column: Announcements & Statements */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <h2 style={{ fontSize: '1.35rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-main)' }}>
            <Info size={22} className="text-primary" />
            Library Announcements & About Statements
          </h2>

          {statements.length > 0 ? (
            statements.map((statement) => (
              <div key={statement.id} className="premium-card" style={{ position: 'relative', padding: '1.75rem' }}>
                <p style={{ fontSize: '1rem', lineHeight: 1.7, color: 'var(--text-main)', marginBottom: '1.25rem', whiteSpace: 'pre-line' }}>
                  {statement.content}
                </p>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border-light)', paddingTop: '0.85rem' }}>
                  <Calendar size={14} />
                  <span>Statement Published: {new Date(statement.updatedAt).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' })}</span>
                </div>
              </div>
            ))
          ) : (
            <div className="premium-card" style={{ textAlign: 'center', padding: '3.5rem 2rem', color: 'var(--text-muted)' }}>
              <BookOpen size={48} style={{ marginBottom: '1rem', opacity: 0.3, color: 'var(--primary)' }} />
              <p style={{ fontWeight: 500 }}>No announcements are currently published.</p>
              <p style={{ fontSize: '0.85rem', marginTop: '0.25rem' }}>Please check back later for updates from library management.</p>
            </div>
          )}
        </div>

        {/* Right Column: Library Guidelines & Quick Info */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div className="premium-card">
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Shield size={18} style={{ color: 'var(--primary)' }} />
              Quick Rules
            </h3>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.75rem', fontSize: '0.875rem', color: 'var(--text-main)' }}>
              <li style={{ display: 'flex', gap: '0.5rem' }}>
                <span style={{ color: 'var(--primary)' }}>•</span>
                <span>One book borrowed at a time limit.</span>
              </li>
              <li style={{ display: 'flex', gap: '0.5rem' }}>
                <span style={{ color: 'var(--primary)' }}>•</span>
                <span>Return the borrowed book on or before the due date.</span>
              </li>
              <li style={{ display: 'flex', gap: '0.5rem' }}>
                <span style={{ color: 'var(--primary)' }}>•</span>
                <span>Reservations will be acknowledged by staff.</span>
              </li>
            </ul>
          </div>

          <div className="premium-card" style={{ background: 'rgba(99, 102, 241, 0.03)', borderColor: 'rgba(99, 102, 241, 0.1)' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Layers size={18} style={{ color: 'var(--primary)' }} />
              General Services
            </h3>
            <p style={{ fontSize: '0.875rem', lineHeight: 1.6, color: 'var(--text-muted)' }}>
              For queries or assistance, please reach out to library staff during operating hours. Feedback can be directly submitted via our Feedback tab.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default About;
