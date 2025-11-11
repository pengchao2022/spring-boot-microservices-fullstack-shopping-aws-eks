import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CheckoutSteps from '../components/checkout/CheckoutSteps';
import DeliveryForm from '../components/checkout/DeliveryForm';
import OrderReview from '../components/checkout/OrderReview';
import CheckoutSummary from '../components/checkout/CheckoutSummary';
import AlipayPayment from '../components/checkout/AlipayPayment';
import { getFullRegionName } from '../utils/regionUtils';
import './Checkout.css';

const Checkout = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [cartData, setCartData] = useState(null);
  const [orderData, setOrderData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

  const steps = [
    { number: 1, title: '配送信息', component: DeliveryForm },
    { number: 2, title: '订单确认', component: OrderReview },
    { number: 3, title: '支付', component: AlipayPayment }
  ];

  useEffect(() => {
    console.log('🔄 Checkout页面加载 - 强制重新加载购物车数据');
    fetchCartData();
  }, []);

  // 监听 cartData 变化
  useEffect(() => {
    console.log('📊 cartData 发生变化:', cartData);
    console.log('📦 cartData.items:', cartData?.items);
    console.log('🔢 商品数量:', cartData?.items?.length);
  }, [cartData]);

  const getUserIdFromToken = (token) => {
    try {
      if (!token) {
        console.warn('❌ 没有token');
        return null;
      }

      // 直接解析token，不使用缓存
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const userId = payload.userId || payload.sub || payload.id;
        console.log('🔑 从Token解析的用户ID:', userId);
        
        if (userId) {
          // 更新localStorage中的用户ID
          localStorage.setItem('userId', userId.toString());
          return userId.toString();
        }
      } catch (decodeError) {
        console.error('❌ Token解析失败:', decodeError);
      }

      // 如果token解析失败，尝试从localStorage获取
      const savedUserId = localStorage.getItem('userId');
      if (savedUserId) {
        console.log('📝 使用localStorage中的用户ID:', savedUserId);
        return savedUserId;
      }

      console.error('❌ 无法获取用户ID');
      return null;

    } catch (error) {
      console.error('❌ 获取用户ID失败:', error);
      return null;
    }
  };

  const fetchCartData = async () => {
    try {
      setLoading(true);
      setError('');
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login', { state: { from: '/checkout' } });
        return;
      }

      const userId = getUserIdFromToken(token);
      if (!userId) {
        setError('无法获取用户信息，请重新登录');
        setLoading(false);
        return;
      }

      console.log('🔄 强制重新加载购物车数据，用户ID:', userId);

      const response = await fetch(`${API_BASE_URL}/cart/items`, {
        headers: {
          'X-User-Id': userId,
          'Authorization': `Bearer ${token}`
        }
      });

      console.log('📊 购物车API响应状态:', response.status);

      if (response.ok) {
        const data = await response.json();
        console.log('🛒 API返回的原始购物车数据:', data);
        
        // 关键调试：检查数据内容
        console.log('🔍 数据详细检查:');
        console.log('- items 数组:', data.items);
        console.log('- items 长度:', data.items?.length);
        if (data.items && data.items.length > 0) {
          console.log('- 第一个商品:', data.items[0]);
          console.log('- 商品名称:', data.items[0].productName);
          console.log('- 商品价格:', data.items[0].price);
          console.log('- 商品数量:', data.items[0].quantity);
        }
        
        // 确保数据格式正确
        if (!data.items || data.items.length === 0) {
          console.log('⚠️ 购物车为空，跳转到购物车页面');
          navigate('/cart');
          return;
        }
        
        console.log('📦 购物车中的商品:', data.items);
        console.log('💰 总金额:', data.items.reduce((total, item) => total + (item.price * item.quantity), 0));
        
        // 强制使用API数据，忽略任何可能的模拟数据
        console.log('✅ 使用API返回的真实数据');
        setCartData(data);
        
      } else {
        throw new Error(`获取购物车数据失败: ${response.status}`);
      }
    } catch (err) {
      console.error('❌ 获取购物车数据错误:', err);
      if (err.message.includes('未登录')) {
        navigate('/login', { state: { from: '/checkout' } });
      } else {
        setError(err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDeliverySubmit = async (deliveryData) => {
    try {
      setLoading(true);
      setError('');
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login', { state: { from: '/checkout' } });
        return;
      }

      const userId = getUserIdFromToken(token);
      if (!userId) {
        setError('无法获取用户信息，请重新登录');
        setLoading(false);
        return;
      }

      console.log('📦 创建订单，用户ID:', userId);

      // 验证必要的字段
      if (!deliveryData.shippingName || !deliveryData.shippingPhone || !deliveryData.shippingAddress) {
        setError('请填写完整的配送信息');
        setLoading(false);
        return;
      }

      // 再次验证购物车数据
      if (!cartData || !cartData.items || cartData.items.length === 0) {
        setError('购物车数据为空，请返回购物车重新添加商品');
        setLoading(false);
        return;
      }

      console.log('🛒 用于创建订单的购物车数据:', cartData);
      console.log('📋 商品列表:', cartData.items);

      // 使用 getFullRegionName 构建完整的城市地址
      const fullCityName = getFullRegionName(
        deliveryData.selectedProvince, 
        deliveryData.selectedCity, 
        deliveryData.selectedArea
      );

      console.log('📍 完整的地区名称:', fullCityName);

      // 计算金额
      const subtotalAmount = calculateSubtotal(cartData.items);
      const totalAmount = subtotalAmount;

      console.log('💰 订单金额 - 小计:', subtotalAmount, '总计:', totalAmount);

      // 订单请求数据
      const orderRequest = {
        userId: parseInt(userId),
        // 配送信息
        shippingName: deliveryData.shippingName,
        shippingPhone: deliveryData.shippingPhone,
        shippingProvince: deliveryData.selectedProvince,
        shippingCity: deliveryData.selectedCity,
        shippingDistrict: deliveryData.selectedArea,
        shippingDetailAddress: deliveryData.shippingAddress,
        
        // 账单信息（与配送信息相同）
        billingName: deliveryData.shippingName,
        billingPhone: deliveryData.shippingPhone,
        billingProvince: deliveryData.selectedProvince,
        billingCity: deliveryData.selectedCity,
        billingDistrict: deliveryData.selectedArea,
        billingDetailAddress: deliveryData.shippingAddress,
        
        // 支付方式
        paymentMethod: 'ALIPAY',
        shippingMethod: 'standard',
        notes: deliveryData.notes || '',
        currency: 'CNY',
        
        // 金额信息
        subtotalAmount: subtotalAmount,
        shippingAmount: 0,
        taxAmount: 0,
        discountAmount: 0,
        totalAmount: totalAmount,
        
        // 订单项目 - 使用购物车中的实际商品
        items: cartData.items.map(item => ({
          productId: item.productId,
          productName: item.productName,
          price: item.price,
          quantity: item.quantity,
          imageUrl: item.imageUrl || '',
          weight: item.weight || '500g'
        }))
      };

      console.log('📋 订单请求数据:', orderRequest);

      // 直接调用创建订单端点
      const response = await fetch(`${API_BASE_URL}/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId
        },
        body: JSON.stringify(orderRequest)
      });

      console.log('📊 创建订单响应状态:', response.status);
      
      if (response.ok) {
        const orderResult = await response.json();
        console.log('✅ 订单创建成功:', orderResult);
        
        setOrderData(orderResult);
        setCurrentStep(2);
      } else {
        const errorText = await response.text();
        console.error('❌ 创建订单失败:', errorText);
        setError(`创建订单失败: ${response.status} - ${errorText}`);
      }
    } catch (err) {
      console.error('❌ 处理配送信息错误:', err);
      setError(`网络错误: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleOrderConfirm = () => {
    console.log('✅ 订单确认，进入支付步骤');
    setCurrentStep(3);
  };

  const handlePaymentSuccess = async (paymentData) => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login', { state: { from: '/checkout' } });
        return;
      }

      const userId = getUserIdFromToken(token);
      if (!userId) {
        setError('无法获取用户信息，请重新登录');
        setLoading(false);
        return;
      }

      // 更新订单支付状态
      const updateData = {
        paymentMethod: paymentData.paymentMethod,
        paymentId: paymentData.paymentId,
        paymentStatus: 'COMPLETED',
        status: 'CONFIRMED'
      };

      console.log('💳 更新订单支付状态:', updateData);

      const response = await fetch(`${API_BASE_URL}/orders/${orderData.id}/payment`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
          'X-User-Id': userId
        },
        body: JSON.stringify(updateData)
      });

      if (response.ok) {
        const updatedOrder = await response.json();
        console.log('✅ 订单支付状态更新成功:', updatedOrder);
        
        await clearCart(userId, token);
        
        navigate('/order-success', { 
          state: { 
            orderId: orderData.id,
            orderNumber: orderData.orderNumber 
          } 
        });
      } else {
        const errorText = await response.text();
        throw new Error(`更新订单支付状态失败: ${errorText}`);
      }
    } catch (err) {
      console.error('❌ 处理支付成功错误:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const clearCart = async (userId, token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/cart/clear`, {
        method: 'POST',
        headers: {
          'X-User-Id': userId,
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        console.log('✅ 购物车清空成功');
      } else {
        console.warn('⚠️ 清空购物车失败:', response.status);
      }
    } catch (error) {
      console.error('❌ 清空购物车失败:', error);
    }
  };

  const calculateSubtotal = (items) => {
    return items.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const CurrentStepComponent = steps[currentStep - 1].component;

  // 关键调试：在渲染前检查数据
  console.log('🎯 Checkout.jsx render - cartData:', cartData);
  console.log('🎯 Checkout.jsx render - cartData.items:', cartData?.items);
  if (cartData?.items) {
    console.log('🎯 商品详情:');
    cartData.items.forEach((item, index) => {
      console.log(`  ${index + 1}. ${item.productName} - ¥${item.price} × ${item.quantity}`);
    });
  }

  if (loading && !cartData) {
    return (
      <div className="checkout-loading">
        <div className="spinner"></div>
        <p>正在加载购物车数据...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="checkout-error">
        <h2>加载失败</h2>
        <p>{error}</p>
        <div className="error-actions">
          <button onClick={() => window.location.reload()} className="retry-btn">
            重试
          </button>
          <button onClick={() => navigate('/cart')} className="back-to-cart-btn">
            返回购物车
          </button>
        </div>
      </div>
    );
  }

  if (!cartData || !cartData.items || cartData.items.length === 0) {
    return (
      <div className="checkout-error">
        <h2>购物车为空</h2>
        <p>您的购物车中没有商品，请先添加商品再结算</p>
        <button onClick={() => navigate('/products')} className="back-to-cart-btn">
          去选购商品
        </button>
      </div>
    );
  }

  return (
    <div className="checkout-container">
      <div className="checkout-header">
        <h1>确认订单</h1>
        <button 
          onClick={fetchCartData} 
          className="refresh-cart-btn"
          style={{marginLeft: '20px', padding: '5px 10px', fontSize: '14px'}}
        >
          刷新购物车数据
        </button>
      </div>

      <CheckoutSteps 
        currentStep={currentStep} 
        steps={steps.map(step => step.title)} 
      />

      <div className="checkout-content">
        <div className="checkout-main">
          <CurrentStepComponent
            currentStep={currentStep}
            cartData={cartData}
            orderData={orderData}
            onDeliverySubmit={handleDeliverySubmit}
            onOrderConfirm={handleOrderConfirm}
            onPaymentSuccess={handlePaymentSuccess}
            onStepChange={setCurrentStep}
            loading={loading}
          />
        </div>

        <div className="checkout-sidebar">
          <CheckoutSummary 
            cartData={cartData}
            orderData={orderData}
            currentStep={currentStep}
          />
        </div>
      </div>

      {error && (
        <div className="checkout-error-message">
          <span className="error-icon">⚠️</span>
          {error}
          <button 
            onClick={() => setError('')} 
            className="error-close-btn"
          >
            ×
          </button>
        </div>
      )}
    </div>
  );
};

export default Checkout;