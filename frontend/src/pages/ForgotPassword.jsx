import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import authService from '../services/auth.service';
import { Mail, ShieldCheck, Key, ArrowLeft, CheckCircle } from 'lucide-react';
import { motion } from 'framer-motion';

const ForgotPassword = () => {
  const [step, setStep] = useState(1); // 1: Email, 2: OTP, 3: New Password
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { register, handleSubmit } = useForm();

  const handleEmailSubmit = async (data) => {
    setLoading(true);
    try {
      await authService.forgotPassword(data.email);
      setEmail(data.email);
      setStep(2);
      setMessage('OTP sent to your email.');
    } catch (err) {
      setError('Email not found.');
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSubmit = async (data) => {
    setLoading(true);
    try {
      await authService.verifyOtp(email, data.otp);
      setStep(3);
      setMessage('OTP verified. Set new password.');
    } catch (err) {
      setError('Invalid OTP.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetSubmit = async (data) => {
    setLoading(true);
    try {
      await authService.resetPassword(email, data.password);
      setMessage('Password reset successful! Redirecting to login...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError('Failed to reset password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--bg-dark)' }}>
      <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} className="glass" style={{ width: '400px', padding: '2.5rem' }}>
        <Link to="/login" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-muted)', textDecoration: 'none', marginBottom: '1.5rem', fontSize: '0.875rem' }}>
          <ArrowLeft size={16} /> Back to Login
        </Link>

        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '0.5rem' }}>Reset Password</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
          {step === 1 && "Enter your email to receive an OTP."}
          {step === 2 && "Enter the 6-digit code sent to your email."}
          {step === 3 && "Create a strong new password."}
        </p>

        {message && <div style={{ color: 'var(--success)', background: 'rgba(16, 185, 129, 0.1)', padding: '0.75rem', borderRadius: '0.5rem', marginBottom: '1rem', fontSize: '0.875rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><CheckCircle size={16}/>{message}</div>}
        {error && <div style={{ color: 'var(--danger)', background: 'rgba(239, 68, 68, 0.1)', padding: '0.75rem', borderRadius: '0.5rem', marginBottom: '1rem', fontSize: '0.875rem' }}>{error}</div>}

        {step === 1 && (
          <form onSubmit={handleSubmit(handleEmailSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ position: 'relative' }}>
              <Mail style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
              <input {...register('email')} type="email" placeholder="Email Address" style={{ paddingLeft: '2.75rem' }} required />
            </div>
            <button className="btn-primary" disabled={loading}>{loading ? 'Sending...' : 'Send OTP'}</button>
          </form>
        )}

        {step === 2 && (
          <form onSubmit={handleSubmit(handleOtpSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ position: 'relative' }}>
              <ShieldCheck style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
              <input {...register('otp')} type="text" placeholder="6-digit OTP" style={{ paddingLeft: '2.75rem' }} required />
            </div>
            <button className="btn-primary" disabled={loading}>{loading ? 'Verify' : 'Verify OTP'}</button>
          </form>
        )}

        {step === 3 && (
          <form onSubmit={handleSubmit(handleResetSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ position: 'relative' }}>
              <Key style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
              <input {...register('password')} type="password" placeholder="New Password" style={{ paddingLeft: '2.75rem' }} required />
            </div>
            <button className="btn-primary" disabled={loading}>{loading ? 'Resetting...' : 'Reset Password'}</button>
          </form>
        )}
      </motion.div>
    </div>
  );
};

export default ForgotPassword;
