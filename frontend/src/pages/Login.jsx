import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import authService from '../services/auth.service';
import { Library, Lock, User, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';
import OtpLoginModal from '../components/Auth/OtpLoginModal';

const Login = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [isOtpModalOpen, setIsOtpModalOpen] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (data) => {
    setLoading(true);
    setError('');
    try {
      const user = await authService.login(data.username, data.password);
      console.log('=== Login Response ===');
      console.log('User data:', user);
      console.log('Role:', user.role);
      console.log('Token present:', !!user.token);
      console.log('=====================');
      if (user.role === 'ADMIN') navigate('/admin');
      else if (user.role === 'STAFF') navigate('/staff');
      else navigate('/member');
    } catch (err) {
      setError('Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'radial-gradient(circle at center, #1e1b4b 0%, #0f172a 100%)' }}>
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="glass" 
        style={{ width: '100%', maxWidth: '400px', padding: '2.5rem' }}
      >
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ background: 'var(--primary)', width: '60px', height: '60px', borderRadius: '1rem', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
            <Library size={32} color="white" />
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700 }}>Welcome Thalahena Public Library</h1>
          <p style={{ color: 'var(--text-muted)', marginTop: '0.5rem' }}>Login to your account</p>
        </div>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.1)', color: 'var(--danger)', padding: '0.75rem', borderRadius: '0.5rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertCircle size={18} />
            <span style={{ fontSize: '0.875rem' }}>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div style={{ position: 'relative' }}>
            <User style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
            <input
              {...register('username', { required: 'Username is required' })}
              type="text"
              placeholder="Username"
              style={{ paddingLeft: '2.75rem' }}
            />
            {errors.username && <p style={{ color: 'var(--danger)', fontSize: '0.75rem', marginTop: '0.25rem' }}>{errors.username.message}</p>}
          </div>

          <div style={{ position: 'relative' }}>
            <Lock style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
            <input
              {...register('password', { required: 'Password is required' })}
              type="password"
              placeholder="Password"
              style={{ paddingLeft: '2.75rem' }}
            />
            {errors.password && <p style={{ color: 'var(--danger)', fontSize: '0.75rem', marginTop: '0.25rem' }}>{errors.password.message}</p>}
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span onClick={() => setIsOtpModalOpen(true)} style={{ color: 'var(--primary)', cursor: 'pointer', fontSize: '0.875rem', fontWeight: 500 }}>
              Forgotten password?
            </span>
            <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
              Sign up not available
            </span>
          </div>

          <button type="submit" className="btn-primary" disabled={loading} style={{ marginTop: '0.5rem' }}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          <p>Admin: admin1 / admin123</p>
          <p>Staff: staff1 / staff123</p>
          <p>Member: user1 / user123</p>
        </div>
      </motion.div>
      <OtpLoginModal isOpen={isOtpModalOpen} onClose={() => setIsOtpModalOpen(false)} />
    </div>
  );
};

export default Login;
