import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { AddressForm, AddressCard } from '../components/Address';
import { ProfileForm, AvatarUpload } from '../components/UserProfile';
import './Profile.css';

const Profile = () => {
  const { user, logout, updateProfile, token, getToken } = useAuth();
  const navigate = useNavigate();
  
  const [activeTab, setActiveTab] = useState('profile');
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    avatar: ''
  });
  const [addresses, setAddresses] = useState([]);
  const [isAddingAddress, setIsAddingAddress] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [profileData, setProfileData] = useState(null);

  // 头像 URL 处理函数
  const getAvatarUrl = (avatar) => {
    if (avatar) {
      return avatar.startsWith('http') ? avatar : `${process.env.REACT_APP_API_URL || ''}${avatar}`;
    }
    return '/images/default-avatar.png';
  };

  // 获取完整用户资料
  const fetchUserProfile = async () => {
    console.log('开始获取用户资料...');
    
    // 使用 getToken() 确保获取最新的 token
    const currentToken = getToken ? getToken() : token;
    console.log('当前token:', currentToken);
    
    if (!currentToken) {
      console.error('无法获取用户资料: token为空');
      return;
    }
    
    setIsLoading(true);
    try {
      console.log('发送请求到 /api/user/profile');
      
      const response = await fetch('/api/user/profile', {
        headers: {
          'Authorization': `Bearer ${currentToken}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('收到响应:', response.status, response.statusText);
      
      if (response.ok) {
        const data = await response.json();
        console.log('响应数据:', data);
        
        if (data.success) {
          const profile = data.profile;
          setProfileData(profile);
          
          // 更新表单数据，包含所有用户资料信息
          setFormData(prev => ({
            ...prev,
            name: profile.name || '',
            email: profile.email || '',  // 从 user_profiles 表获取
            phone: profile.phone || '',
            avatar: profile.avatarUrl || ''
          }));
          console.log('获取完整用户资料成功');
        }
      } else {
        console.error('获取用户资料失败:', response.status, await response.text());
      }
    } catch (error) {
      console.error('获取用户资料失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 初始化数据 - 修改这里
  useEffect(() => {
    console.log('Profile组件初始化 - user:', user);
    console.log('Profile组件初始化 - token:', token);
    
    if (user) {
      // 首先设置基本数据
      setFormData({
        name: user.name || '',
        email: user.email || '', // 可能为空
        phone: user.phone || '',
        avatar: user.avatarUrl || ''
      });
      
      // 然后获取完整的用户资料（包含邮箱）
      fetchUserProfile();
      
      setAddresses([]);
    }
  }, [user, token]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveProfile = async () => {
    try {
      await updateProfile(formData);
      setIsEditing(false);
      alert('个人信息更新成功！');
      
      // 保存成功后重新获取资料，确保数据同步
      fetchUserProfile();
    } catch (error) {
      alert('更新失败，请重试');
    }
  };

  const handleAvatarUpload = (file) => {
    const reader = new FileReader();
    reader.onload = (event) => {
      setFormData(prev => ({
        ...prev,
        avatar: event.target.result
      }));
    };
    reader.readAsDataURL(file);
  };

  // 地址相关函数保持不变
  const handleAddAddress = (addressData) => {
    const newAddress = { ...addressData, id: Date.now() };
    setAddresses(prev => [...prev, newAddress]);
    setIsAddingAddress(false);
  };

  const handleEditAddress = (addressData) => {
    setAddresses(prev => 
      prev.map(addr => 
        addr.id === editingAddress.id ? { ...addr, ...addressData } : addr
      )
    );
    setEditingAddress(null);
  };

  const handleSetDefaultAddress = (addressId) => {
    setAddresses(prev => 
      prev.map(addr => ({
        ...addr,
        isDefault: addr.id === addressId
      }))
    );
  };

  const handleDeleteAddress = (addressId) => {
    setAddresses(prev => prev.filter(addr => addr.id !== addressId));
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (!user) {
    return (
      <div className="profile-container">
        <div className="login-prompt">
          <h2>请先登录</h2>
          <p>登录后查看个人中心</p>
          <button onClick={() => navigate('/login')}>去登录</button>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <div className="profile-header">
        <h1>个人中心</h1>
        <div className="user-welcome">
          欢迎回来，{user.name}！
          {isLoading && <span style={{marginLeft: '10px', color: '#666'}}>加载中...</span>}
        </div>
      </div>

      <div className="profile-content">
        {/* 侧边栏导航 */}
        <div className="profile-sidebar">
          <div 
            className={`sidebar-item ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            📝 个人信息
          </div>
          <div 
            className={`sidebar-item ${activeTab === 'address' ? 'active' : ''}`}
            onClick={() => setActiveTab('address')}
          >
            🏠 收货地址
          </div>
          <div 
            className={`sidebar-item ${activeTab === 'security' ? 'active' : ''}`}
            onClick={() => setActiveTab('security')}
          >
            🔒 账户安全
          </div>
          <div className="sidebar-item logout" onClick={handleLogout}>
            🚪 退出登录
          </div>
        </div>

        {/* 主要内容区域 */}
        <div className="profile-main">
          {/* 个人信息标签页 */}
          {activeTab === 'profile' && (
            <div className="tab-content">
              <AvatarUpload
                avatarUrl={formData.avatar}
                onAvatarUpload={handleAvatarUpload}
                getAvatarUrl={getAvatarUrl}
              />
              <ProfileForm
                user={user}
                formData={formData}
                isEditing={isEditing}
                onInputChange={handleInputChange}
                onSave={handleSaveProfile}
                onCancelEdit={() => setIsEditing(false)}
                onStartEdit={() => setIsEditing(true)}
              />
            </div>
          )}

          {/* 收货地址标签页 */}
          {activeTab === 'address' && (
            <div className="tab-content">
              <div className="address-header">
                <h2>收货地址</h2>
                <button 
                  className="btn-primary"
                  onClick={() => setIsAddingAddress(true)}
                >
                  ＋ 添加新地址
                </button>
              </div>

              {isAddingAddress && (
                <div className="address-form-container">
                  <AddressForm
                    onSubmit={handleAddAddress}
                    onCancel={() => setIsAddingAddress(false)}
                    title="添加新地址"
                  />
                </div>
              )}

              {editingAddress && (
                <div className="address-form-container">
                  <AddressForm
                    initialData={editingAddress}
                    onSubmit={handleEditAddress}
                    onCancel={() => setEditingAddress(null)}
                    title="编辑地址"
                    submitButtonText="更新地址"
                  />
                </div>
              )}

              <div className="address-list">
                {addresses.length === 0 ? (
                  <div className="empty-address">
                    <p>暂无收货地址</p>
                    <p className="empty-hint">请添加您的第一个收货地址</p>
                  </div>
                ) : (
                  addresses.map(address => (
                    <AddressCard
                      key={address.id}
                      address={address}
                      onSetDefault={handleSetDefaultAddress}
                      onEdit={setEditingAddress}
                      onDelete={handleDeleteAddress}
                    />
                  ))
                )}
              </div>
            </div>
          )}

          {/* 账户安全标签页 */}
          {activeTab === 'security' && (
            <div className="tab-content">
              <h2>账户安全</h2>
              <div className="security-card">
                <div className="security-item">
                  <div className="security-info">
                    <h3>登录密码</h3>
                    <p>定期更换密码可以让账户更安全</p>
                  </div>
                  <button className="btn-primary">修改密码</button>
                </div>
                
                <div className="security-item">
                  <div className="security-info">
                    <h3>绑定手机</h3>
                    <p>已绑定手机：{user.phone || '未绑定'}</p>
                  </div>
                  <button className="btn-secondary">更换手机</button>
                </div>

                <div className="security-item">
                  <div className="security-info">
                    <h3>第三方账号</h3>
                    <p>支付宝账号{formData.email ? '已绑定' : '未绑定'}</p>
                  </div>
                  <button className="btn-secondary">管理绑定</button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;