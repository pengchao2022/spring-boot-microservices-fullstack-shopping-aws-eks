import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import CartItem from '../components/cart/CartItem';
import CartSummary from '../components/cart/CartSummary';
import EmptyCart from '../components/cart/EmptyCart';
import CartRecommendations from '../components/cart/CartRecommendations';
import '../styles/CartPage.css';

const CartPage = () => {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // 检查登录状态
  useEffect(() => {
    checkLoginStatus();
    loadCartItems();
  }, []);

  const checkLoginStatus = () => {
    const token = localStorage.getItem('token');
    setIsLoggedIn(!!token);
  };

  // 从后端API加载购物车数据
  const loadCartItems = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const token = localStorage.getItem('token');
      console.log('🔑 Token:', token ? '存在' : '不存在');
      
      if (!token) {
        setIsLoggedIn(false);
        setLoading(false);
        return;
      }

      // 从JWT token中解析用户ID
      let userId;
      try {
        const user = JSON.parse(atob(token.split('.')[1]));
        userId = user.userId || user.sub;
        console.log('👤 User ID:', userId);
      } catch (parseError) {
        console.log('❌ Token解析失败:', parseError);
        setError('登录信息无效');
        return;
      }

      // 使用相对路径调用购物车API
      console.log('🔄 调用购物车API...');
      const response = await fetch(`/api/cart/items`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId,
          'Content-Type': 'application/json'
        }
      });

      console.log('📊 API响应状态:', response.status);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          setIsLoggedIn(false);
          setLoading(false);
          return;
        }
        throw new Error('获取购物车数据失败');
      }

      const cartData = await response.json();
      console.log('🛒 原始API响应数据:', cartData);
      
      // 转换数据格式以匹配前端组件
      const transformedItems = transformCartItems(cartData);
      console.log('🎉 转换后的购物车数据:', transformedItems);
      
      setCartItems(transformedItems);
      
    } catch (err) {
      console.error('❌ 加载购物车错误:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 转换购物车数据格式 - 修复版
  const transformCartItems = (cartData) => {
    console.log('🛒 开始转换购物车数据结构:', cartData);
    
    // 处理响应格式: {id: 1, userId: 1, items: Array(2), totalItems: 2}
    let items = [];
    
    if (cartData && Array.isArray(cartData.items)) {
      items = cartData.items;
      console.log('✅ 使用 cartData.items, 数量:', items.length);
    } else {
      console.log('❌ 数据格式不正确');
      return [];
    }

    if (items.length === 0) {
      console.log('🛒 购物车为空');
      return [];
    }

    // 根据实际数据结构转换每个商品项
    const transformed = items.map(item => {
      console.log('📦 转换商品项:', item);
      
      const transformedItem = {
        id: item.id, // 使用购物车项ID (如: 5, 6)
        productId: item.productId, // 商品ID (如: 1, 13)
        name: item.productName, // 商品名称 (如: '栖霞红富士苹果')
        englishName: '', // 后端没有提供英文名
        imageUrl: item.imageUrl, // 图片URL
        price: parseFloat(item.price), // 价格 (如: 12.5, 22.8)
        originalPrice: parseFloat(item.price) * 1.2, // 计算原价
        quantity: parseInt(item.quantity), // 数量
        weight: item.weight || '500g', // 重量 (如: '斤')
        stock: 50, // 需要从库存服务获取
        maxQuantity: 10
      };
      
      console.log('🔄 转换结果:', transformedItem);
      return transformedItem;
    });

    console.log('🎉 最终购物车商品:', transformed);
    return transformed;
  };

  // 添加商品到购物车 - 修复版
  const addToCart = async (productId, quantity = 1, productData = {}) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return false;
      }

      const user = JSON.parse(atob(token.split('.')[1]));
      const userId = user.userId || user.sub;

      console.log(`🛒 添加商品到购物车: productId=${productId}, quantity=${quantity}`);

      // 调用API添加商品到购物车
      const response = await fetch(`/api/cart/items`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          productId: productId,
          quantity: quantity,
          productName: productData.name || '商品',
          price: productData.price || 0,
          imageUrl: productData.imageUrl || '/images/default-product.jpg',
          weight: productData.weight || '500g'
        })
      });

      console.log('📊 添加商品响应状态:', response.status);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return false;
        }
        throw new Error('添加商品失败');
      }

      const result = await response.json();
      console.log('✅ 添加商品成功:', result);
      
      // 重新加载购物车数据
      await loadCartItems();
      
      return true;

    } catch (err) {
      console.error('❌ 添加商品错误:', err);
      alert('添加商品失败，请重试');
      return false;
    }
  };

  // 更新商品数量 - 修复版
  const updateQuantity = async (itemId, newQuantity) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      const user = JSON.parse(atob(token.split('.')[1]));
      const userId = user.userId || user.sub;

      console.log(`🔄 更新数量: itemId=${itemId}, quantity=${newQuantity}`);

      // 调用API更新数量
      const response = await fetch(`/api/cart/items/${itemId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          quantity: newQuantity
        })
      });

      console.log('📊 更新数量响应状态:', response.status);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('更新数量失败');
      }

      console.log('✅ 数量更新成功');
      // 重新加载购物车数据
      await loadCartItems();

    } catch (err) {
      console.error('❌ 更新数量错误:', err);
      alert('更新数量失败，请重试');
    }
  };

  // 删除商品 - 修复版
  const removeItem = async (itemId) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      const user = JSON.parse(atob(token.split('.')[1]));
      const userId = user.userId || user.sub;

      console.log(`🗑️ 删除商品: itemId=${itemId}`);

      // 调用API删除商品
      const response = await fetch(`/api/cart/items/${itemId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId,
          'Content-Type': 'application/json'
        }
      });

      console.log('📊 删除商品响应状态:', response.status);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('删除商品失败');
      }

      console.log('✅ 删除商品成功');
      // 重新加载购物车数据
      await loadCartItems();

    } catch (err) {
      console.error('❌ 删除商品错误:', err);
      alert('删除商品失败，请重试');
    }
  };

  // 清空购物车
  const clearCart = async () => {
    if (!window.confirm('确定要清空购物车吗？')) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      const user = JSON.parse(atob(token.split('.')[1]));
      const userId = user.userId || user.sub;

      console.log('🧹 清空购物车');

      // 调用API清空购物车
      const response = await fetch(`/api/cart/clear`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId,
          'Content-Type': 'application/json'
        }
      });

      console.log('📊 清空购物车响应状态:', response.status);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('清空购物车失败');
      }

      console.log('✅ 清空购物车成功');
      // 更新本地状态
      setCartItems([]);

    } catch (err) {
      console.error('❌ 清空购物车错误:', err);
      alert('清空购物车失败，请重试');
    }
  };

  // 跳转到结算页面
  const proceedToCheckout = () => {
    if (!isLoggedIn) {
      alert('请先登录账号');
      navigate('/login', { state: { from: '/cart' } });
      return;
    }

    if (cartItems.length === 0) {
      alert('购物车为空');
      return;
    }

    console.log('➡️ 跳转到结算页面');
    // 跳转到结算页面
    navigate('/checkout');
  };

  // 继续购物
  const continueShopping = () => {
    console.log('🛍️ 继续购物');
    navigate('/products');
  };

  // 重新加载购物车
  const retryLoadCart = () => {
    console.log('🔄 重新加载购物车');
    setError(null);
    loadCartItems();
  };

  if (loading) {
    return (
      <div className="cart-page">
        <div className="loading">
          <div className="spinner"></div>
          <p>正在加载购物车...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="cart-page">
        <div className="error-container">
          <h2>加载失败</h2>
          <p>{error}</p>
          <button onClick={retryLoadCart} className="btn-retry">
            重试
          </button>
          <button onClick={continueShopping} className="btn-continue">
            继续购物
          </button>
        </div>
      </div>
    );
  }

  if (!isLoggedIn) {
    return (
      <div className="cart-page">
        <div className="login-prompt">
          <h2>请先登录</h2>
          <p>登录后查看购物车</p>
          <button 
            onClick={() => navigate('/login', { state: { from: '/cart' } })}
            className="btn-login"
          >
            立即登录
          </button>
          <button onClick={continueShopping} className="btn-continue">
            继续购物
          </button>
        </div>
      </div>
    );
  }

  if (cartItems.length === 0) {
    return <EmptyCart onContinueShopping={continueShopping} />;
  }

  return (
    <div className="cart-page">
      {/* 面包屑导航 */}
      <nav className="breadcrumb">
        <Link to="/">首页</Link>
        <span> / </span>
        <span>购物车</span>
      </nav>

      <div className="cart-container">
        <div className="cart-main">
          {/* 购物车头部 */}
          <div className="cart-header">
            <h1>购物车</h1>
            <div className="cart-actions">
              <span className="item-count">共 {cartItems.length} 件商品</span>
              <button className="clear-cart-btn" onClick={clearCart}>
                清空购物车
              </button>
            </div>
          </div>

          {/* 购物车商品列表 */}
          <div className="cart-items">
            {cartItems.map(item => (
              <CartItem
                key={item.id}
                item={item}
                onUpdateQuantity={updateQuantity}
                onRemove={removeItem}
              />
            ))}
          </div>

          {/* 推荐商品 */}
          <CartRecommendations onAddToCart={addToCart} />
        </div>

        {/* 购物车汇总 */}
        <div className="cart-sidebar">
          <CartSummary 
            items={cartItems}
            onCheckout={proceedToCheckout}
            onContinueShopping={continueShopping}
          />
        </div>
      </div>
    </div>
  );
};

export default CartPage;