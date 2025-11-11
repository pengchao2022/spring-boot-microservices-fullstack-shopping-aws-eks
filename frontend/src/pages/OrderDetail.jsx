import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { OrderDetail as OrderDetailComponent, OrderStatus, OrderTracking } from '../components/order';
import './OrderDetail.css';

const OrderDetail = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchOrderDetail();
  }, [orderId]);

  const fetchOrderDetail = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      
      if (!token) {
        navigate('/login');
        return;
      }

      // 使用相对路径，让反向代理处理
      const response = await fetch(`/api/orders/${orderId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('获取订单详情失败');
      }

      const orderData = await response.json();
      
      // 转换数据格式以匹配前端组件
      const transformedOrder = transformOrderData(orderData);
      setOrder(transformedOrder);
      
    } catch (err) {
      setError(err.message);
      console.error('获取订单详情错误:', err);
    } finally {
      setLoading(false);
    }
  };

  // 转换后端数据为前端组件需要的格式
  const transformOrderData = (orderData) => {
    return {
      id: orderData.orderNumber || orderData.id,
      status: mapOrderStatus(orderData.status),
      createdAt: orderData.createdAt,
      paidAt: orderData.confirmedAt,
      subtotal: orderData.subtotalAmount,
      shippingFee: orderData.shippingAmount || 0,
      discount: orderData.discountAmount || 0,
      totalAmount: orderData.totalAmount,
      items: orderData.items ? orderData.items.map(item => ({
        id: item.productId,
        name: item.productName,
        category: '果蔬', // 需要从商品服务获取
        image: item.imageUrl || '/images/default-product.jpg',
        price: parseFloat(item.unitPrice),
        quantity: item.quantity,
        weight: item.weight ? `${item.weight}g` : '标准装',
        freshness: '新鲜',
        origin: '产地直供'
      })) : [],
      deliveryInfo: {
        recipientName: `${orderData.shippingFirstName || ''} ${orderData.shippingLastName || ''}`.trim() || orderData.shippingName,
        phone: orderData.shippingPhone,
        address: `${orderData.shippingAddressLine1 || ''} ${orderData.shippingAddressLine2 || ''}`.trim(),
        deliveryTime: 'anytime', // 需要从订单数据中获取
        specialInstructions: orderData.notes
      },
      trackingInfo: orderData.trackingNumber ? {
        courier: orderData.shippingMethod || '快递',
        trackingNumber: orderData.trackingNumber,
        estimatedDelivery: '尽快配送',
        events: generateTrackingEvents(orderData)
      } : null
    };
  };

  // 映射订单状态
  const mapOrderStatus = (status) => {
    const statusMap = {
      'PENDING': 'pending',
      'CONFIRMED': 'paid', 
      'SHIPPED': 'shipped',
      'DELIVERED': 'delivered',
      'CANCELLED': 'cancelled'
    };
    return statusMap[status] || 'pending';
  };

  // 生成物流跟踪事件
  const generateTrackingEvents = (orderData) => {
    const events = [];
    
    if (orderData.createdAt) {
      events.push({
        status: 'info',
        description: '订单已创建',
        time: orderData.createdAt,
        location: '系统'
      });
    }
    
    if (orderData.confirmedAt) {
      events.push({
        status: 'picked',
        description: '订单已确认',
        time: orderData.confirmedAt,
        location: '仓库'
      });
    }
    
    if (orderData.shippedAt) {
      events.push({
        status: 'shipped',
        description: '商品已发货',
        time: orderData.shippedAt,
        location: '发货中心'
      });
    }
    
    if (orderData.deliveredAt) {
      events.push({
        status: 'delivered',
        description: '商品已送达',
        time: orderData.deliveredAt,
        location: orderData.shippingCity || '目的地'
      });
    }
    
    return events;
  };

  const handleBack = () => {
    navigate('/orders');
  };

  const handleCancelOrder = async () => {
    if (!window.confirm('确定要取消这个订单吗？')) return;
    
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        navigate('/login');
        return;
      }

      // 使用相对路径
      const response = await fetch(`/api/orders/${orderId}/cancel`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        alert('订单取消成功');
        fetchOrderDetail(); // 刷新订单状态
      } else {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('取消订单失败');
      }
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) {
    return (
      <div className="order-detail-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>加载订单详情中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="order-detail-page">
        <div className="error-container">
          <h2>加载失败</h2>
          <p>{error}</p>
          <button onClick={handleBack} className="btn-primary">
            返回订单列表
          </button>
        </div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="order-detail-page">
        <div className="not-found-container">
          <h2>订单不存在</h2>
          <button onClick={handleBack} className="btn-primary">
            返回订单列表
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="order-detail-page">
      <div className="order-detail-container">
        <div className="detail-main">
          <OrderDetailComponent 
            order={order}
            onBack={handleBack}
            onCancelOrder={handleCancelOrder}
          />
        </div>
        
        <div className="detail-sidebar">
          <OrderStatus 
            status={order.status}
            timeline={[
              { time: order.createdAt, description: '订单创建成功' },
              ...(order.paidAt ? [{ time: order.paidAt, description: '支付成功' }] : []),
              ...(order.trackingInfo ? order.trackingInfo.events : [])
            ]}
          />
          
          {order.trackingInfo && (
            <OrderTracking trackingInfo={order.trackingInfo} />
          )}
        </div>
      </div>
    </div>
  );
};

export default OrderDetail;