import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    phoneNumber: '',
    password: '',
    confirmPassword: '',
    verificationCode: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [codeSent, setCodeSent] = useState(false);
  
  const { register, sendVerificationCode } = useAuth();
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
  };

  // 发送验证码 - 修复版本
  const handleSendCode = async () => {
    if (!formData.phoneNumber) {
      setError('请输入手机号码');
      return;
    }

    if (formData.phoneNumber.length !== 11) {
      setError('请输入有效的11位手机号码');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // 使用 AuthContext 提供的 sendVerificationCode 方法
      const result = await sendVerificationCode(formData.phoneNumber);
      
      if (result.success) {
        setCodeSent(true);
        setCountdown(60);
        
        // 开始倒计时
        const timer = setInterval(() => {
          setCountdown(prev => {
            if (prev <= 1) {
              clearInterval(timer);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);

        console.log('验证码发送成功');
      } else {
        setError(result.error || '发送验证码失败');
      }
    } catch (err) {
      console.error('发送验证码异常:', err);
      setError('发送验证码失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // 表单验证
    if (!formData.username) {
      setError('请输入用户名');
      setLoading(false);
      return;
    }

    if (!formData.phoneNumber) {
      setError('请输入手机号码');
      setLoading(false);
      return;
    }

    if (formData.phoneNumber.length !== 11) {
      setError('请输入有效的11位手机号码');
      setLoading(false);
      return;
    }

    if (!formData.verificationCode) {
      setError('请输入验证码');
      setLoading(false);
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('两次输入的密码不一致');
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError('密码长度至少6位');
      setLoading(false);
      return;
    }

    try {
      const result = await register({
        phone: formData.phoneNumber,
        password: formData.password,
        verificationCode: formData.verificationCode,
        name: formData.username
      });
      
      if (result.success) {
        navigate('/', { state: { registerSuccess: true } });
      } else {
        setError(result.error || '注册失败');
      }
    } catch (err) {
      setError('注册过程中发生错误');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <h1 style={titleStyle}>注册账号</h1>
        
        {error && (
          <div style={errorStyle}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={formStyle}>
          {/* 用户名输入 */}
          <div style={inputGroupStyle}>
            <label htmlFor="username" style={labelStyle}>用户名</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleInputChange}
              required
              style={inputStyle}
              placeholder="请输入您的昵称"
              maxLength="20"
            />
            <small style={helpTextStyle}>
              可以是中文、英文或数字，最长20个字符
            </small>
          </div>

          {/* 手机号输入 */}
          <div style={inputGroupStyle}>
            <label htmlFor="phoneNumber" style={labelStyle}>手机号码</label>
            <input
              type="tel"
              id="phoneNumber"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              required
              style={inputStyle}
              placeholder="请输入11位手机号码"
              maxLength="11"
            />
          </div>

          {/* 验证码输入 */}
          <div style={inputGroupStyle}>
            <label htmlFor="verificationCode" style={labelStyle}>验证码</label>
            <div style={codeInputContainerStyle}>
              <input
                type="text"
                id="verificationCode"
                name="verificationCode"
                value={formData.verificationCode}
                onChange={handleInputChange}
                required
                maxLength="6"
                style={codeInputStyle}
                placeholder="请输入6位验证码"
              />
              <button
                type="button"
                onClick={handleSendCode}
                disabled={loading || countdown > 0 || !formData.phoneNumber}
                style={codeButtonStyle(countdown > 0 || !formData.phoneNumber)}
              >
                {countdown > 0 ? `${countdown}秒后重发` : '获取验证码'}
              </button>
            </div>
          </div>

          {/* 密码输入 */}
          <div style={inputGroupStyle}>
            <label htmlFor="password" style={labelStyle}>设置密码</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleInputChange}
              required
              style={inputStyle}
              placeholder="请设置登录密码"
            />
            <small style={helpTextStyle}>
              密码长度至少6位
            </small>
          </div>

          {/* 确认密码 */}
          <div style={inputGroupStyle}>
            <label htmlFor="confirmPassword" style={labelStyle}>确认密码</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleInputChange}
              required
              style={inputStyle}
              placeholder="请再次输入密码"
            />
          </div>

          <button 
            type="submit" 
            disabled={loading}
            style={buttonStyle(loading)}
          >
            {loading ? '注册中...' : '立即注册'}
          </button>
        </form>

        <div style={linksStyle}>
          <p>
            已有账号？{' '}
            <Link to="/login" style={linkStyle}>
              立即登录
            </Link>
          </p>
        </div>

        {codeSent && process.env.NODE_ENV === 'development' && (
          <div style={hintStyle}>
            ✅ 验证码已发送，请在浏览器控制台查看
          </div>
        )}

        <div style={agreementStyle}>
          注册即表示您同意
          <a href="#" style={agreementLinkStyle}>《用户协议》</a>
          和
          <a href="#" style={agreementLinkStyle}>《隐私政策》</a>
        </div>
      </div>
    </div>
  );
};

// 样式保持不变...
const containerStyle = {
  minHeight: 'calc(100vh - 200px)',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  padding: '2rem 1rem',
  backgroundColor: '#f8f9fa',
};

const cardStyle = {
  backgroundColor: 'white',
  padding: '2.5rem',
  borderRadius: '12px',
  boxShadow: '0 8px 24px rgba(0, 0, 0, 0.1)',
  width: '100%',
  maxWidth: '450px',
};

const titleStyle = {
  textAlign: 'center',
  marginBottom: '2rem',
  color: '#1a1a1a',
  fontSize: '1.8rem',
  fontWeight: '600',
};

const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.5rem',
};

const inputGroupStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '0.5rem',
};

const labelStyle = {
  fontWeight: '600',
  color: '#333',
  fontSize: '0.9rem',
};

const inputStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  transition: 'border-color 0.3s',
  outline: 'none',
};

const codeInputContainerStyle = {
  display: 'flex',
  gap: '0.75rem',
  alignItems: 'center',
};

const codeInputStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  transition: 'border-color 0.3s',
  flex: 1,
  letterSpacing: '0.5rem',
  textAlign: 'center',
  outline: 'none',
};

const codeButtonStyle = (disabled) => ({
  padding: '0.75rem 1rem',
  backgroundColor: disabled ? '#ccc' : '#ff6a00',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  fontSize: '0.9rem',
  fontWeight: '600',
  cursor: disabled ? 'not-allowed' : 'pointer',
  transition: 'all 0.3s ease',
  whiteSpace: 'nowrap',
  minWidth: '110px',
});

const buttonStyle = (loading) => ({
  padding: '0.875rem',
  backgroundColor: loading ? '#ccc' : '#ff6a00',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  fontSize: '1rem',
  fontWeight: '600',
  cursor: loading ? 'not-allowed' : 'pointer',
  transition: 'all 0.3s ease',
  marginTop: '0.5rem',
});

const linksStyle = {
  textAlign: 'center',
  marginTop: '2rem',
  fontSize: '0.9rem',
  color: '#666',
  lineHeight: '1.6',
  borderTop: '1px solid #f0f0f0',
  paddingTop: '1.5rem',
};

const linkStyle = {
  color: '#ff6a00',
  textDecoration: 'none',
  fontWeight: '600',
};

const errorStyle = {
  backgroundColor: '#fee',
  color: '#c33',
  padding: '0.75rem',
  borderRadius: '6px',
  marginBottom: '1rem',
  textAlign: 'center',
  border: '1px solid #fcc',
  fontSize: '0.9rem',
};

const helpTextStyle = {
  color: '#666',
  fontSize: '0.8rem',
  marginTop: '0.25rem',
};

const hintStyle = {
  backgroundColor: '#fff8f0',
  color: '#ff6a00',
  padding: '0.75rem',
  borderRadius: '6px',
  marginTop: '1rem',
  textAlign: 'center',
  border: '1px solid #ffddcc',
  fontSize: '0.9rem',
};

const agreementStyle = {
  textAlign: 'center',
  fontSize: '0.8rem',
  color: '#999',
  lineHeight: '1.5',
  marginTop: '1.5rem',
  paddingTop: '1.5rem',
  borderTop: '1px solid #f0f0f0',
};

const agreementLinkStyle = {
  color: '#ff6a00',
  textDecoration: 'none',
  margin: '0 0.25rem',
};

export default Register;