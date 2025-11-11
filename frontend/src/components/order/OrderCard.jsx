import React from 'react';
import './OrderCard.css';

const OrderCard = ({ order, onClick }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getStatusColor = (status) => {
    const statusColors = {
      pending: '#ffc107',
      paid: '#17a2b8',
      shipped: '#007bff',
      delivered: '#28a745',
      cancelled: '#dc3545'
    };
    return statusColors[status] || '#6c757d';
  };

  const getStatusText = (status) => {
    const statusTexts = {
      pending: '待支付',
      paid: '已支付',
      shipped: '已发货',
      delivered: '已送达',
      cancelled: '已取消'
    };
    return statusTexts[status] || '未知状态';
  };

  return (
    <div className="order-card" onClick={() => onClick(order)}>
      <div className="order-header">
        <div className="order-info">
          <h3 className="order-id">订单号: {order.id}</h3>
          <span className="order-date">{formatDate(order.createdAt)}</span>
        </div>
        <div 
          className="order-status"
          style={{ backgroundColor: getStatusColor(order.status) }}
        >
          {getStatusText(order.status)}
        </div>
      </div>

      <div className="order-items">
        {order.items.slice(0, 3).map((item, index) => (
          <div key={index} className="order-item-preview">
            <img src={item.image} alt={item.name} className="item-image" />
            <div className="item-info">
              <span className="item-name">{item.name}</span>
              <span className="item-quantity">×{item.quantity}</span>
            </div>
          </div>
        ))}
        {order.items.length > 3 && (
          <div className="more-items">+{order.items.length - 3} 件商品</div>
        )}
      </div>

      <div className="order-footer">
        <div className="order-total">
          共 {order.items.reduce((total, item) => total + item.quantity, 0)} 件商品
          <span className="total-amount">¥{order.totalAmount}</span>
        </div>
        <div className="order-actions">
          {order.status === 'pending' && (
            <button className="btn-pay">立即支付</button>
          )}
          {order.status === 'delivered' && (
            <button className="btn-review">评价商品</button>
          )}
          <button className="btn-detail">查看详情</button>
        </div>
      </div>
    </div>
  );
};

export default OrderCard;