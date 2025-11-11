import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth'; // 修正导入路径

const AlipayCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth();

  useEffect(() => {
    const processCallback = async () => {
      try {
        // 从URL参数中获取授权码
        const authCode = searchParams.get('auth_code');
        const state = searchParams.get('state');

        console.log('支付宝回调参数:', {
          authCode,
          state,
          allParams: Object.fromEntries(searchParams.entries())
        });

        if (authCode && state === 'alipay_login') {
          // 显示处理中的状态
          console.log('收到支付宝授权码:', authCode);
          
          // 在实际项目中，这里应该调用您的后端API来处理支付宝回调
          // 现在先模拟登录成功
          setTimeout(async () => {
            try {
              // 模拟调用后端API
              // const response = await fetch('/api/auth/alipay/callback', {
              //   method: 'POST',
              //   headers: {
              //     'Content-Type': 'application/json',
              //   },
              //   body: JSON.stringify({ authCode }),
              // });
              // const result = await response.json();

              // 模拟成功响应
              const result = {
                success: true,
                user: {
                  id: `alipay_user_${Date.now()}`,
                  name: '支付宝用户',
                  avatar: '',
                  loginType: 'alipay'
                }
              };

              if (result.success) {
                // 登录成功
                await login(result.user, 'alipay');
                navigate('/');
              } else {
                navigate('/login', { state: { error: '支付宝登录失败' } });
              }
            } catch (error) {
              console.error('处理支付宝回调错误:', error);
              navigate('/login', { state: { error: '登录过程发生错误' } });
            }
          }, 1500);
          
        } else {
          console.error('支付宝回调参数错误:', { authCode, state });
          navigate('/login', { state: { error: '授权参数错误' } });
        }
      } catch (error) {
        console.error('支付宝回调处理错误:', error);
        navigate('/login', { state: { error: '登录过程发生错误' } });
      }
    };

    processCallback();
  }, [searchParams, navigate, login]);

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      height: '100vh',
      flexDirection: 'column',
      gap: '1rem',
      backgroundColor: '#f8f9fa'
    }}>
      <div style={{
        fontSize: '1.2rem',
        color: '#1677ff',
        fontWeight: '600'
      }}>
        正在处理支付宝登录...
      </div>
      <div style={{
        color: '#666',
        fontSize: '0.9rem'
      }}>
        请稍候，正在验证您的信息
      </div>
      <div style={{
        width: '40px',
        height: '40px',
        border: '3px solid #f3f3f3',
        borderTop: '3px solid #1677ff',
        borderRadius: '50%',
        animation: 'spin 1s linear infinite'
      }}></div>
      <style>
        {`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}
      </style>
    </div>
  );
};

export default AlipayCallback;