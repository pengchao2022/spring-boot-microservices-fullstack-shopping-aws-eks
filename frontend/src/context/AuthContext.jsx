import React, { createContext, useState, useContext, useEffect, useRef } from 'react';

// 创建并导出 AuthContext
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [serverTimeOffset, setServerTimeOffset] = useState(0);
  
  // 添加 token 状态
  const [token, setTokenState] = useState(null);
  
  // 添加防重复验证标志
  const isVerifyingRef = useRef(false);
  const hasInitializedRef = useRef(false);

  // 获取 token 的函数
  const getToken = () => {
    return token || localStorage.getItem('token');
  };

  useEffect(() => {
    // 防止重复初始化
    if (hasInitializedRef.current) {
      setLoading(false);
      return;
    }
    
    const storedToken = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (storedToken) {
      setTokenState(storedToken);
    }
    
    if (storedToken && userData) {
      setUser(JSON.parse(userData));
    }
    
    // 测试 API 连接
    testApiConnection();
    setLoading(false);
    hasInitializedRef.current = true;
  }, []);

  // 测试 API 连接
  const testApiConnection = async () => {
    try {
      const response = await fetch('/api/auth/test');
      if (response.ok) {
        const result = await response.text();
        console.log('API 连接测试成功:', result);
      } else {
        console.warn('API 连接测试失败，状态:', response.status);
      }
    } catch (error) {
      console.warn('API 连接测试异常:', error.message);
    }
  };

  // 获取当前服务器时间（简化版本）
  const getServerTime = () => {
    return Date.now();
  };

  // 手机号标准化函数
  const normalizePhoneNumber = (phone) => {
    if (!phone) return '';
    // 只保留数字
    return phone.replace(/[^\d]/g, '').trim();
  };

  // 提取国家区号和手机号
  const parsePhoneNumber = (phoneNumber) => {
    if (!phoneNumber) return { countryCode: '+86', phone: '' };
    
    // 如果包含国家区号，提取出来
    if (phoneNumber.startsWith('+')) {
      const match = phoneNumber.match(/^(\+\d{1,4})(\d+)$/);
      if (match) {
        return {
          countryCode: match[1],
          phone: match[2]
        };
      }
    }
    
    // 默认中国区号
    return {
      countryCode: '+86',
      phone: normalizePhoneNumber(phoneNumber)
    };
  };

  // 修复的：使用token登录的方法 - 添加防重复验证
  const loginWithToken = async (token, userId, userName) => {
    // 防止重复验证
    if (isVerifyingRef.current) {
      console.log('AuthContext: 跳过重复的token验证');
      return { success: true, user };
    }

    try {
      isVerifyingRef.current = true;
      
      console.log('AuthContext: 使用token登录', {
        token: token ? `${token.substring(0, 20)}...` : 'null',
        userId,
        userName
      });

      if (!token) {
        console.error('AuthContext: Token为空');
        isVerifyingRef.current = false;
        return { success: false, error: 'Token不能为空' };
      }

      // 如果已经有用户信息且token相同，直接返回
      const currentToken = localStorage.getItem('token');
      if (currentToken === token && user) {
        console.log('AuthContext: 使用现有用户信息，跳过验证');
        isVerifyingRef.current = false;
        return { success: true, user };
      }

      // 存储token
      localStorage.setItem('token', token);
      localStorage.setItem('user_id', userId);
      setTokenState(token);

      // 如果有用户名，创建用户对象
      let userData = {
        id: userId,
        name: userName || '用户',
        loginType: 'token'
      };

      // 如果没有完整的用户信息，尝试从服务器获取
      if (!userName) {
        try {
          const userResponse = await fetch(`/api/user/${userId}`, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });

          if (userResponse.ok) {
            const userInfo = await userResponse.json();
            userData = { ...userData, ...userInfo };
            console.log('AuthContext: 从服务器获取用户信息成功', userInfo);
          }
        } catch (error) {
          console.warn('AuthContext: 获取用户信息失败，使用基础信息', error.message);
        }
      }

      // 存储用户信息
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);

      console.log('AuthContext: Token登录成功', {
        userId: userData.id,
        userName: userData.name
      });

      isVerifyingRef.current = false;
      return { success: true, user: userData };

    } catch (error) {
      console.error('AuthContext: Token登录异常', {
        error: error.message,
        userId,
        userName
      });
      isVerifyingRef.current = false;
      return { success: false, error: error.message || 'Token登录失败' };
    }
  };

  // 登录函数
  const login = async (loginData) => {
    try {
      let response;
      const clientTime = Date.now();
      const serverTime = getServerTime();
      
      console.log('AuthContext: 开始登录', { 
        loginData,
        clientTime: new Date(clientTime).toISOString(),
        serverTime: new Date(serverTime).toISOString()
      });

      // 处理手机号
      let requestBody = { ...loginData };
      
      if (loginData.phone) {
        const { countryCode, phone } = parsePhoneNumber(loginData.phone);
        requestBody.countryCode = countryCode;
        requestBody.phone = phone;
      }

      // 确保登录类型正确
      if (!requestBody.loginType) {
        if (requestBody.password) {
          requestBody.loginType = 'password';
        } else if (requestBody.verificationCode) {
          requestBody.loginType = 'verification';
        }
      }

      console.log('AuthContext: 登录请求体', requestBody);
      
      response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      console.log('AuthContext: 登录响应状态', {
        status: response?.status,
        statusText: response?.statusText,
        ok: response?.ok
      });

      if (response && response.ok) {
        const data = await response.json();
        console.log('AuthContext: 登录成功', {
          data,
          hasToken: !!data.token,
          hasUser: !!data.user
        });
        
        if (data.token) {
          localStorage.setItem('token', data.token);
          setTokenState(data.token); // 设置 token 状态
          console.log('AuthContext: Token存储成功');
        }
        if (data.user) {
          localStorage.setItem('user', JSON.stringify(data.user));
          setUser(data.user);
          console.log('AuthContext: 用户信息存储成功', data.user);
        }
        return { success: true, user: data.user };
      } else {
        const errorText = await response?.text();
        console.error('AuthContext: 登录失败', {
          status: response?.status,
          statusText: response?.statusText,
          errorText,
          requestBody: requestBody
        });
        
        let errorMessage = '登录失败';
        try {
          const errorData = JSON.parse(errorText);
          errorMessage = errorData.error || errorData.message || '登录失败';
        } catch (e) {
          // 如果响应不是JSON，使用原始文本
          errorMessage = errorText || '登录失败';
        }
        
        return { success: false, error: errorMessage };
      }
    } catch (error) {
      console.error('AuthContext: 登录异常', {
        error: error.message
      });
      return { success: false, error: error.message || '网络请求失败' };
    }
  };

  // 注册函数
  const register = async (userData) => {
    try {
      const serverTime = getServerTime();
      
      console.log('AuthContext: 开始注册', {
        userData,
        serverTime: new Date(serverTime).toISOString()
      });
      
      // 处理手机号
      let requestBody = { ...userData };
      
      if (userData.phone) {
        const { countryCode, phone } = parsePhoneNumber(userData.phone);
        requestBody.countryCode = countryCode;
        requestBody.phone = phone;
      }

      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      console.log('AuthContext: 注册响应状态', {
        status: response.status,
        statusText: response.statusText
      });

      if (response.ok) {
        const data = await response.json();
        console.log('AuthContext: 注册成功', {
          data,
          hasToken: !!data.token,
          hasUser: !!data.user
        });
        
        if (data.token) {
          localStorage.setItem('token', data.token);
          setTokenState(data.token);
        }
        if (data.user) {
          localStorage.setItem('user', JSON.stringify(data.user));
          setUser(data.user);
        }
        return { success: true, user: data.user };
      } else {
        const errorText = await response.text();
        let errorMessage = '注册失败';
        try {
          const errorData = JSON.parse(errorText);
          errorMessage = errorData.error || errorData.message || '注册失败';
        } catch (e) {
          errorMessage = errorText || '注册失败';
        }
        
        console.error('AuthContext: 注册失败', {
          errorMessage,
          status: response.status
        });
        return { success: false, error: errorMessage };
      }
    } catch (error) {
      console.error('AuthContext: 注册异常', error);
      return { success: false, error: error.message };
    }
  };

  // 发送验证码 - 修正版本
  const sendVerificationCode = async (phoneData) => {
    try {
      const clientTime = Date.now();
      const serverTime = getServerTime();
      
      console.log('AuthContext: 发送验证码', {
        phoneData,
        clientTime: new Date(clientTime).toISOString(),
        serverTime: new Date(serverTime).toISOString()
      });

      // 处理参数格式
      let countryCode = '+86';
      let phone = '';
      
      if (typeof phoneData === 'string') {
        // 如果是字符串，解析手机号
        const parsed = parsePhoneNumber(phoneData);
        countryCode = parsed.countryCode;
        phone = parsed.phone;
      } else {
        // 如果是对象，直接使用
        countryCode = phoneData.countryCode || '+86';
        phone = normalizePhoneNumber(phoneData.phone);
      }

      // 构建查询参数
      const params = new URLSearchParams({
        phone: phone,
        countryCode: countryCode
      });

      // 记录验证码发送时间
      localStorage.setItem('verificationCodeSendTime', serverTime.toString());
      localStorage.setItem('verificationCodePhone', phone);
      
      console.log('AuthContext: 发送验证码请求参数', {
        params: params.toString(),
        fullUrl: `/api/auth/verification-code?${params}`
      });
      
      const response = await fetch(`/api/auth/verification-code?${params}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        }
      });

      console.log('AuthContext: 发送验证码响应状态', {
        status: response.status,
        statusText: response.statusText,
        ok: response.ok
      });

      if (response.ok) {
        console.log('AuthContext: 发送验证码成功');
        return { success: true };
      } else {
        const errorText = await response.text();
        console.error('AuthContext: 发送验证码失败', {
          errorText,
          params: params.toString(),
          status: response.status
        });
        return { success: false, error: errorText || '发送验证码失败' };
      }
    } catch (error) {
      console.error('AuthContext: 发送验证码异常', {
        error: error.message,
        phoneData: phoneData
      });
      return { success: false, error: error.message };
    }
  };

  // 其他函数保持不变...
  const logout = () => {
    console.log('AuthContext: 用户退出登录', {
      previousUser: user
    });
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('user_id');
    setUser(null);
    setTokenState(null); // 清除 token 状态
    // 重置验证状态
    isVerifyingRef.current = false;
    hasInitializedRef.current = false;
  };

  const updateProfile = async (profileData) => {
    try {
      const currentToken = getToken();
      console.log('AuthContext: 更新用户资料', {
        profileData,
        hasToken: !!currentToken,
        currentUser: user
      });
      
      const response = await fetch('/api/user/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentToken}`
        },
        body: JSON.stringify(profileData),
      });

      console.log('AuthContext: 更新资料响应状态', {
        status: response.status,
        statusText: response.statusText
      });

      if (response.ok) {
        const data = await response.json();
        console.log('AuthContext: 更新资料成功', {
          data,
          updatedFields: Object.keys(profileData)
        });
        
        const updatedUser = { ...user, ...data.user };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        setUser(updatedUser);
        return { success: true, user: updatedUser };
      } else {
        const errorData = await response.json();
        console.error('AuthContext: 更新资料失败', {
          errorData,
          status: response.status
        });
        return { success: false, error: errorData.error || errorData.message || '更新资料失败' };
      }
    } catch (error) {
      console.error('AuthContext: 更新资料异常', error);
      return { success: false, error: error.message };
    }
  };

  const changePassword = async (oldPassword, newPassword) => {
    try {
      const currentToken = getToken();
      console.log('AuthContext: 修改密码', {
        hasOldPassword: !!oldPassword,
        hasNewPassword: !!newPassword,
        hasToken: !!currentToken
      });
      
      const response = await fetch('/api/auth/change-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentToken}`
        },
        body: JSON.stringify({ oldPassword, newPassword }),
      });

      console.log('AuthContext: 修改密码响应状态', {
        status: response.status,
        statusText: response.statusText,
        ok: response.ok
      });

      if (response.ok) {
        console.log('AuthContext: 修改密码成功');
        return { success: true };
      } else {
        const errorData = await response.json();
        console.error('AuthContext: 修改密码失败', {
          errorData,
          status: response.status
        });
        return { success: false, error: errorData.error || errorData.message || '修改密码失败' };
      }
    } catch (error) {
      console.error('AuthContext: 修改密码异常', error);
      return { success: false, error: error.message };
    }
  };

  const value = {
    user,
    token: getToken(), // 提供 token
    getToken, // 提供获取 token 的函数
    login,
    register,
    logout,
    loading,
    isAuthenticated: !!user,
    sendVerificationCode,
    updateProfile,
    changePassword,
    loginWithToken, // 新增的方法
    getServerTime: () => new Date(getServerTime()).toISOString()
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;