import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { getCartItemsCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  // 头像 URL 处理函数
  const getAvatarUrl = (avatar) => {
    if (avatar) {
      return avatar.startsWith('http') ? avatar : `${process.env.REACT_APP_API_URL || ''}${avatar}`;
    }
    // 使用本地默认头像
    return '/images/default-avatar.png';
  };

  return (
    <header style={headerStyle}>
      <div style={containerStyle}>
        <Link to="/" style={logoStyle}>
          <h1 style={logoTitleStyle}>普罗米修甄选</h1>
        </Link>
        
        <nav style={navStyle}>
          <Link to="/" style={linkStyle}>首页</Link>
          <Link to="/products" style={linkStyle}>产品</Link>
          
          {isAuthenticated ? (
            <>
              {/* 优化购物车链接 */}
              <Link to="/cart" style={cartLinkStyle}>
                <span>购物车</span>
                {getCartItemsCount() > 0 && (
                  <span style={cartBadgeStyle}>
                    {getCartItemsCount()}
                  </span>
                )}
              </Link>
              
              <Link to="/orders" style={linkStyle}>我的订单</Link>
              
              {/* 个人中心链接 - 显示文字和头像 */}
              <Link to="/profile" style={profileLinkStyle}>
                <span style={profileTextStyle}>个人中心</span>
                <img 
                  src={getAvatarUrl(user?.avatar)} 
                  alt="个人中心" 
                  style={profileAvatarStyle}
                  onError={(e) => {
                    e.target.src = '/images/default-avatar.png';
                  }}
                />
              </Link>
              
              <button onClick={handleLogout} style={logoutButtonStyle}>
                退出
              </button>
            </>
          ) : (
            <>
              <Link to="/login" style={linkStyle}>登录</Link>
              <Link to="/register" style={linkStyle}>注册</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

const headerStyle = {
  backgroundColor: '#ff5722',
  padding: '0.5rem 0',
  color: 'white',
  boxShadow: '0 2px 10px rgba(255, 87, 34, 0.3)',
  position: 'sticky',
  top: 0,
  zIndex: 1000,
  borderBottom: '2px solid #ff8a65',
};

const containerStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '0 1rem',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
};

const logoStyle = {
  color: 'white',
  textDecoration: 'none',
};

const logoTitleStyle = {
  fontSize: '1.25rem',
  fontWeight: 'bold',
  margin: 0,
};

const navStyle = {
  display: 'flex',
  alignItems: 'center',
  gap: '1rem',
};

const linkStyle = {
  color: 'white',
  textDecoration: 'none',
  padding: '0.4rem 0.8rem',
  borderRadius: '4px',
  transition: 'all 0.3s ease',
  fontSize: '0.9rem',
  position: 'relative',
  display: 'flex',
  alignItems: 'center',
};

// 购物车链接样式
const cartLinkStyle = {
  ...linkStyle,
  position: 'relative',
  display: 'flex',
  alignItems: 'center',
  gap: '0.3rem',
};

// 购物车徽章样式
const cartBadgeStyle = {
  backgroundColor: '#ffeb3b',
  color: '#333',
  borderRadius: '50%',
  width: '20px',
  height: '20px',
  fontSize: '0.7rem',
  fontWeight: 'bold',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  position: 'relative',
  animation: 'pulse 2s infinite',
};

// 个人中心链接样式
const profileLinkStyle = {
  display: 'flex',
  alignItems: 'center',
  gap: '0.5rem',
  color: 'white',
  textDecoration: 'none',
  padding: '0.4rem 0.8rem',
  borderRadius: '4px',
  transition: 'all 0.3s ease',
  fontSize: '0.9rem',
};

// 个人中心文字样式
const profileTextStyle = {
  fontSize: '0.9rem',
  fontWeight: 'normal',
};

// 个人中心头像样式
const profileAvatarStyle = {
  width: '28px',
  height: '28px',
  borderRadius: '50%',
  objectFit: 'cover',
  border: '2px solid rgba(255, 255, 255, 0.5)',
  transition: 'all 0.3s ease',
};

const logoutButtonStyle = {
  backgroundColor: 'transparent',
  color: 'white',
  border: '1px solid white',
  padding: '0.4rem 0.8rem',
  borderRadius: '4px',
  cursor: 'pointer',
  transition: 'all 0.3s ease',
  fontSize: '0.9rem',
};

// 添加悬停效果
Object.assign(linkStyle, {
  ':hover': {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    transform: 'translateY(-1px)',
  }
});

Object.assign(cartLinkStyle, {
  ':hover': {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    transform: 'translateY(-1px)',
  }
});

Object.assign(profileLinkStyle, {
  ':hover': {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    transform: 'translateY(-1px)',
  }
});

Object.assign(profileAvatarStyle, {
  ':hover': {
    border: '2px solid rgba(255, 255, 255, 0.8)',
    transform: 'scale(1.05)',
  }
});

Object.assign(logoutButtonStyle, {
  ':hover': {
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    transform: 'translateY(-1px)',
  }
});

Object.assign(logoStyle, {
  ':hover': {
    opacity: 0.8,
  }
});

// 添加动画样式
const styleSheet = document.styleSheets[0];
styleSheet.insertRule(`
  @keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.1); }
    100% { transform: scale(1); }
  }
`, styleSheet.cssRules.length);

export default Header;