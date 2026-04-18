import React from 'react';

const Feedback = () => (
   <div className="premium-card">
    <h2>Feedback</h2>
    <p style={{ color: 'var(--text-muted)' }}>Share your thoughts with us to improve our service.</p>
    <textarea placeholder="Write your feedback..." style={{ marginTop: '1rem', minHeight: '150px' }}></textarea>
    <button className="btn-primary" style={{ marginTop: '1rem' }}>Submit Feedback</button>
  </div>
);

export default Feedback;
