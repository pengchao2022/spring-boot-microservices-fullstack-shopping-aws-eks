import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Login = () => {
  const [formData, setFormData] = useState({
    countryCode: '+86',
    phoneNumber: '',
    verificationCode: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [codeSent, setCodeSent] = useState(false);
  const [activeTab, setActiveTab] = useState('phone'); // 'phone', 'password', 'taobao', 'alipay'
  
  const { login, sendVerificationCode } = useAuth();
  const navigate = useNavigate();

  const countryCodes = [
    { code: '+86', country: '中国' },
    { code: '+1', country: '美国' },
    { code: '+44', country: '英国' },
    { code: '+81', country: '日本' },
    { code: '+82', country: '韩国' },
    { code: '+65', country: '新加坡' },
  ];

  // 支付宝登录配置
  const alipayConfig = {
    appId: '2021006103655907',
    redirectUri: 'https://awsmpc.asia/api/auth/alipay/callback',
    scope: 'auth_user',
    state: 'alipay_login',
  };

  useEffect(() => {
    if (window.AlipayJSBridge) {
      window.AlipayJSBridge.call('init', {
        appId: alipayConfig.appId
      });
    }
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
  };

  // 发送验证码 - 修正版本
  const handleSendCode = async () => {
    if (!formData.phoneNumber) {
      setError('请输入手机号码');
      return;
    }

    if (formData.phoneNumber.length < 8) {
      setError('请输入有效的手机号码');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // 使用 AuthContext 的 sendVerificationCode 函数
      const result = await sendVerificationCode({
        countryCode: formData.countryCode,
        phone: formData.phoneNumber
      });

      if (result.success) {
        setCodeSent(true);
        setCountdown(60);
        
        // 开发环境下提示用户查看日志
        if (process.env.NODE_ENV === 'development') {
          console.log(`💡 开发提示: 验证码已发送到后端，手机号: ${formData.countryCode}${formData.phoneNumber}`);
          console.log(`💡 请在 user-service 日志中查看验证码`);
        }
        
        const timer = setInterval(() => {
          setCountdown(prev => {
            if (prev <= 1) {
              clearInterval(timer);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);

      } else {
        setError(result.error || '发送验证码失败');
      }
    } catch (err) {
      console.error('发送验证码失败:', err);
      setError('发送验证码失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  // 验证码登录 - 修正版本
  const handleVerificationLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!formData.phoneNumber) {
      setError('请输入手机号码');
      setLoading(false);
      return;
    }

    if (!formData.verificationCode) {
      setError('请输入验证码');
      setLoading(false);
      return;
    }

    try {
      // 修正：发送分开的国家区号和手机号，而不是合并的完整手机号
      const loginData = {
        countryCode: formData.countryCode,      // 单独发送国家区号
        phone: formData.phoneNumber,            // 单独发送手机号（注意字段名是phone）
        verificationCode: formData.verificationCode,
        loginType: 'verification'
      };

      console.log('开始验证码登录流程:', loginData);

      // 调用 AuthContext 的 login 函数
      const loginResult = await login(loginData);
      
      console.log('登录API返回:', loginResult);
      
      if (loginResult.success) {
        console.log('登录成功，跳转到首页');
        navigate('/');
      } else {
        setError(loginResult.error || '登录失败');
      }
    } catch (err) {
      console.error('登录过程中发生错误:', err);
      setError('登录过程中发生错误，请重试');
    } finally {
      setLoading(false);
    }
  };

  // 账密登录 - 修正版本
  const handlePasswordLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!formData.phoneNumber) {
      setError('请输入手机号码');
      setLoading(false);
      return;
    }

    if (!formData.password) {
      setError('请输入密码');
      setLoading(false);
      return;
    }

    try {
      // 修正：发送分开的国家区号和手机号
      const loginData = {
        countryCode: formData.countryCode,      // 单独发送国家区号
        phone: formData.phoneNumber,            // 单独发送手机号
        password: formData.password,
        loginType: 'password'
      };

      console.log('开始账密登录流程:', loginData);

      // 调用 AuthContext 的 login 函数
      const result = await login(loginData);
      
      console.log('登录API返回:', result);
      
      if (result.success) {
        navigate('/');
      } else {
        setError(result.error || '登录失败');
      }
    } catch (err) {
      console.error('登录过程中发生错误:', err);
      setError('登录过程中发生错误，请重试');
    } finally {
      setLoading(false);
    }
  };

  // 支付宝登录
  const handleAlipayLogin = () => {
    setLoading(true);
    setError('');

    try {
      const alipayAuthUrl = `https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=${alipayConfig.appId}&scope=${alipayConfig.scope}&redirect_uri=${encodeURIComponent(alipayConfig.redirectUri)}&state=${alipayConfig.state}`;
      
      window.location.href = alipayAuthUrl;

    } catch (err) {
      console.error('支付宝登录错误:', err);
      setError('支付宝登录失败，请重试');
      setLoading(false);
    }
  };

  // 淘宝登录（模拟）
  const handleTaobaoLogin = async () => {
    setLoading(true);
    setError('');
    
    try {
      // 使用对象参数调用 login 函数
      const loginData = {
        loginType: 'taobao',
        thirdPartyUserId: `taobao_user_${Date.now()}`
      };

      const result = await login(loginData);
      if (result.success) {
        navigate('/');
      } else {
        setError('淘宝登录失败');
      }
      setLoading(false);
    } catch (err) {
      setError('淘宝登录失败');
      setLoading(false);
    }
  };

  const renderPhoneLogin = () => (
    <form onSubmit={handleVerificationLogin} style={formStyle}>
      <div style={inputGroupStyle}>
        <label htmlFor="phoneNumber" style={labelStyle}>手机号码</label>
        <div style={phoneInputContainerStyle}>
          <select
            name="countryCode"
            value={formData.countryCode}
            onChange={handleInputChange}
            style={countrySelectStyle}
            disabled={loading}
          >
            {countryCodes.map((country) => (
              <option key={country.code} value={country.code}>
                {country.country} {country.code}
              </option>
            ))}
          </select>
          <input
            type="tel"
            id="phoneNumber"
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handleInputChange}
            required
            style={phoneInputStyle}
            placeholder="请输入手机号码"
            disabled={loading}
          />
        </div>
      </div>

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
            disabled={loading}
          />
          <button
            type="button"
            onClick={handleSendCode}
            disabled={loading || countdown > 0}
            style={codeButtonStyle(countdown > 0)}
          >
            {countdown > 0 ? `${countdown}秒后重发` : '获取验证码'}
          </button>
        </div>
        {process.env.NODE_ENV === 'development' && codeSent && (
          <div style={devHintStyle}>
            💡 开发提示：验证码已发送到后端，请在 user-service 日志中查看
          </div>
        )}
      </div>

      <button 
        type="submit" 
        disabled={loading}
        style={buttonStyle(loading)}
      >
        {loading ? '登录中...' : '验证码登录'}
      </button>
    </form>
  );

  const renderPasswordLogin = () => (
    <form onSubmit={handlePasswordLogin} style={formStyle}>
      <div style={inputGroupStyle}>
        <label htmlFor="phoneNumber" style={labelStyle}>手机号码</label>
        <div style={phoneInputContainerStyle}>
          <select
            name="countryCode"
            value={formData.countryCode}
            onChange={handleInputChange}
            style={countrySelectStyle}
            disabled={loading}
          >
            {countryCodes.map((country) => (
              <option key={country.code} value={country.code}>
                {country.country} {country.code}
              </option>
            ))}
          </select>
          <input
            type="tel"
            id="phoneNumber"
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handleInputChange}
            required
            style={phoneInputStyle}
            placeholder="请输入手机号码"
            disabled={loading}
          />
        </div>
      </div>

      <div style={inputGroupStyle}>
        <label htmlFor="password" style={labelStyle}>密码</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleInputChange}
          required
          style={passwordInputStyle}
          placeholder="请输入密码"
          disabled={loading}
        />
      </div>

      <button 
        type="submit" 
        disabled={loading}
        style={buttonStyle(loading)}
      >
        {loading ? '登录中...' : '账密登录'}
      </button>

      <div style={forgotPasswordStyle}>
        <Link to="/forgot-password" style={forgotPasswordLinkStyle}>
          忘记密码？
        </Link>
      </div>
    </form>
  );

  const renderAlipayLogin = () => (
    <div style={thirdPartyContentStyle}>
      <div style={alipayInfoStyle}>
        <div style={alipayIconStyle}>💰</div>
        <h3 style={alipayTitleStyle}>支付宝安全登录</h3>
        <p style={alipayDescStyle}>
          使用支付宝账号快速登录，安全便捷
        </p>
      </div>
      
      <button
        type="button"
        onClick={handleAlipayLogin}
        disabled={loading}
        style={alipayButtonStyle(loading)}
      >
        <span style={iconStyle}>💰</span>
        {loading ? '跳转中...' : '支付宝账号登录'}
      </button>

      <div style={agreementStyle}>
        点击登录即表示您同意
        <a href="/terms" style={agreementLinkStyle}>《用户协议》</a>
        和
        <a href="/privacy" style={agreementLinkStyle}>《隐私政策》</a>
      </div>
    </div>
  );

  const renderTaobaoLogin = () => (
    <div style={thirdPartyContentStyle}>
      <div style={taobaoInfoStyle}>
        <div style={taobaoIconStyle}>🛒</div>
        <h3 style={taobaoTitleStyle}>淘宝账号登录</h3>
        <p style={taobaoDescStyle}>
          使用淘宝账号快速登录
        </p>
      </div>
      
      <button
        type="button"
        onClick={handleTaobaoLogin}
        disabled={loading}
        style={taobaoButtonStyle(loading)}
      >
        <span style={iconStyle}>🛒</span>
        {loading ? '授权中...' : '淘宝账号登录'}
      </button>

      <div style={agreementStyle}>
        点击登录即表示您同意
        <a href="/terms" style={agreementLinkStyle}>《用户协议》</a>
        和
        <a href="/privacy" style={agreementLinkStyle}>《隐私政策》</a>
      </div>
    </div>
  );

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <h1 style={titleStyle}>账号登录</h1>
        
        <div style={tabContainerStyle}>
          <button
            style={tabButtonStyle(activeTab === 'phone')}
            onClick={() => setActiveTab('phone')}
            disabled={loading}
          >
            验证码登录
          </button>
          <button
            style={tabButtonStyle(activeTab === 'password')}
            onClick={() => setActiveTab('password')}
            disabled={loading}
          >
            账密登录
          </button>
          <button
            style={tabButtonStyle(activeTab === 'taobao')}
            onClick={() => setActiveTab('taobao')}
            disabled={loading}
          >
            淘宝账号
          </button>
          <button
            style={tabButtonStyle(activeTab === 'alipay')}
            onClick={() => setActiveTab('alipay')}
            disabled={loading}
          >
            支付宝
          </button>
        </div>

        {error && (
          <div style={errorStyle}>
            {error}
          </div>
        )}

        {activeTab === 'phone' && renderPhoneLogin()}
        {activeTab === 'password' && renderPasswordLogin()}
        {activeTab === 'taobao' && renderTaobaoLogin()}
        {activeTab === 'alipay' && renderAlipayLogin()}

        <div style={linksStyle}>
          <p>
            还没有账号？{' '}
            <Link to="/register" style={linkStyle}>
              立即注册
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

// 样式定义
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
  marginBottom: '1.5rem',
  color: '#1a1a1a',
  fontSize: '1.8rem',
  fontWeight: '600',
};

const tabContainerStyle = {
  display: 'flex',
  marginBottom: '2rem',
  borderBottom: '1px solid #e8e8e8',
};

const tabButtonStyle = (isActive) => ({
  flex: 1,
  padding: '0.75rem 0.5rem',
  backgroundColor: 'transparent',
  border: 'none',
  borderBottom: isActive ? '2px solid #ff6a00' : '2px solid transparent',
  color: isActive ? '#ff6a00' : '#666',
  fontSize: '0.9rem',
  fontWeight: isActive ? '600' : '400',
  cursor: 'pointer',
  transition: 'all 0.3s ease',
  ':hover': {
    color: '#ff6a00',
  }
});

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
  marginBottom: '0.25rem',
};

const phoneInputContainerStyle = {
  display: 'flex',
  gap: '0.5rem',
  alignItems: 'center',
};

const countrySelectStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '0.9rem',
  backgroundColor: 'white',
  minWidth: '120px',
  cursor: 'pointer',
  outline: 'none',
  ':focus': {
    borderColor: '#ff6a00',
  }
};

const phoneInputStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  transition: 'border-color 0.3s',
  flex: 1,
  outline: 'none',
  ':focus': {
    borderColor: '#ff6a00',
  }
};

const passwordInputStyle = {
  padding: '0.75rem',
  border: '1px solid #ddd',
  borderRadius: '6px',
  fontSize: '1rem',
  transition: 'border-color 0.3s',
  outline: 'none',
  ':focus': {
    borderColor: '#ff6a00',
  }
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
  ':focus': {
    borderColor: '#ff6a00',
  }
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
  ':hover': {
    backgroundColor: disabled ? '#ccc' : '#e55a00',
  }
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
  ':hover': {
    backgroundColor: loading ? '#ccc' : '#e55a00',
    transform: loading ? 'none' : 'translateY(-1px)',
  }
});

const thirdPartyContentStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.5rem',
  padding: '1rem 0',
};

const alipayInfoStyle = {
  textAlign: 'center',
  padding: '1.5rem',
  backgroundColor: '#f0f8ff',
  borderRadius: '8px',
  border: '1px solid #d0e8ff',
};

const taobaoInfoStyle = {
  textAlign: 'center',
  padding: '1.5rem',
  backgroundColor: '#fff8f0',
  borderRadius: '8px',
  border: '1px solid #ffddcc',
};

const alipayIconStyle = {
  fontSize: '3rem',
  marginBottom: '1rem',
};

const taobaoIconStyle = {
  fontSize: '3rem',
  marginBottom: '1rem',
};

const alipayTitleStyle = {
  margin: '0 0 0.5rem 0',
  color: '#1677ff',
  fontSize: '1.2rem',
  fontWeight: '600',
};

const taobaoTitleStyle = {
  margin: '0 0 0.5rem 0',
  color: '#ff6a00',
  fontSize: '1.2rem',
  fontWeight: '600',
};

const alipayDescStyle = {
  margin: '0 0 1rem 0',
  color: '#666',
  fontSize: '0.9rem',
};

const taobaoDescStyle = {
  margin: '0 0 1rem 0',
  color: '#666',
  fontSize: '0.9rem',
};

const alipayButtonStyle = (loading) => ({
  padding: '1rem',
  backgroundColor: loading ? '#ccc' : '#1677ff',
  color: 'white',
  border: 'none',
  borderRadius: '8px',
  fontSize: '1.1rem',
  fontWeight: '600',
  cursor: loading ? 'not-allowed' : 'pointer',
  transition: 'all 0.3s ease',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '0.75rem',
  boxShadow: loading ? 'none' : '0 4px 12px rgba(22, 119, 255, 0.3)',
  ':hover': {
    backgroundColor: loading ? '#ccc' : '#0d5cd9',
    transform: loading ? 'none' : 'translateY(-2px)',
  }
});

const taobaoButtonStyle = (loading) => ({
  padding: '1rem',
  backgroundColor: loading ? '#ccc' : '#ff6a00',
  color: 'white',
  border: 'none',
  borderRadius: '8px',
  fontSize: '1.1rem',
  fontWeight: '600',
  cursor: loading ? 'not-allowed' : 'pointer',
  transition: 'all 0.3s ease',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '0.75rem',
  boxShadow: loading ? 'none' : '0 4px 12px rgba(255, 106, 0, 0.3)',
  ':hover': {
    backgroundColor: loading ? '#ccc' : '#e55a00',
    transform: loading ? 'none' : 'translateY(-2px)',
  }
});

const iconStyle = {
  fontSize: '1.2rem',
};

const agreementStyle = {
  textAlign: 'center',
  fontSize: '0.8rem',
  color: '#999',
  lineHeight: '1.5',
};

const agreementLinkStyle = {
  color: '#ff6a00',
  textDecoration: 'none',
  margin: '0 0.25rem',
  ':hover': {
    textDecoration: 'underline',
  }
};

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
  ':hover': {
    textDecoration: 'underline',
  }
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

const devHintStyle = {
  backgroundColor: '#f0f8ff',
  color: '#1677ff',
  padding: '0.5rem',
  borderRadius: '4px',
  fontSize: '0.8rem',
  border: '1px solid #d0e8ff',
  textAlign: 'center',
  marginTop: '0.5rem',
};

const forgotPasswordStyle = {
  textAlign: 'center',
  marginTop: '1rem',
};

const forgotPasswordLinkStyle = {
  color: '#666',
  textDecoration: 'none',
  fontSize: '0.9rem',
  ':hover': {
    color: '#ff6a00',
    textDecoration: 'underline',
  }
};

export default Login;