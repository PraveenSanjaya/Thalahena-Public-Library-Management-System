import React, { useState } from 'react';
import { ExternalLink, HelpCircle, FileText } from 'lucide-react';

const AdditionalService = () => {
  const [loading, setLoading] = useState(true);
  const formUrl = "https://docs.google.com/forms/d/e/1FAIpQLSf-E1V5VCNXC4r3bj7eCUxM43qHwU9qk6N_tDJTwWhdQECDfg/viewform?embedded=true";
  const externalUrl = "https://docs.google.com/forms/d/e/1FAIpQLSf-E1V5VCNXC4r3bj7eCUxM43qHwU9qk6N_tDJTwWhdQECDfg/viewform?usp=publish-editor";

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', paddingBottom: '2rem' }}>
      {/* Header */}
      <div style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <FileText className="text-primary" size={24} />
            Additional Library Services
          </h1>
          <p style={{ color: 'var(--text-muted)' }}>Request additional library facilities, request custom book acquisitions, or provide service evaluations.</p>
        </div>
        <a 
          href={externalUrl} 
          target="_blank" 
          rel="noopener noreferrer" 
          className="btn btn-secondary"
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none' }}
        >
          Open Form Directly
          <ExternalLink size={16} />
        </a>
      </div>

      {/* Embed Container */}
      <div className="premium-card" style={{ padding: '0.5rem', background: '#ffffff', overflow: 'hidden', height: '750px', position: 'relative' }}>
        {loading && (
          <div style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            background: '#ffffff',
            zIndex: 10
          }}>
            <div style={{ color: 'var(--primary)', fontWeight: 600, marginBottom: '0.5rem' }}>Loading Form...</div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Connecting securely to Google Forms</p>
          </div>
        )}
        <iframe
          src={formUrl}
          width="100%"
          height="100%"
          frameBorder="0"
          marginHeight="0"
          marginWidth="0"
          onLoad={() => setLoading(false)}
          style={{ border: 'none', borderRadius: '0.75rem', display: 'block' }}
          title="Additional Library Service Form"
        >
          Loading…
        </iframe>
      </div>

      {/* Note/Guidance */}
      <div className="premium-card" style={{ marginTop: '1.5rem', background: 'rgba(99, 102, 241, 0.03)', borderColor: 'rgba(99, 102, 241, 0.1)', display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
        <HelpCircle size={20} className="text-primary" style={{ flexShrink: 0, marginTop: '2px' }} />
        <div>
          <h4 style={{ fontWeight: 600, fontSize: '0.95rem', marginBottom: '0.25rem' }}>About this Service Request Form</h4>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
            This service form is securely processed via Google Forms. You can submit special request inquiries, new book purchase requests, or general complaints directly. Library admins will review your submission and take necessary actions.
          </p>
        </div>
      </div>
    </div>
  );
};

export default AdditionalService;
