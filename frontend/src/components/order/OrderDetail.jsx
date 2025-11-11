import React from 'react';
import './OrderDetail.css';

const OrderDetail = ({ order, onBack }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('zh-CN');
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
    <div className="order-detail">
      <div className="detail-header">
        <button className="back-button" onClick={onBack}>
          ← 返回订单列表
        </button>
        <h2>订单详情</h2>
      </div>

      <div className="detail-sections">
        {/* 订单基本信息 */}
        <section className="detail-section">
          <h3>订单信息</h3>
          <div className="info-grid">
            <div className="info-item">
              <span className="info-label">订单号:</span>
              <span className="info-value">{order.id}</span>
            </div>
            <div className="info-item">
              <span className="info-label">订单状态:</span>
              <span className="info-value status">{getStatusText(order.status)}</span>
            </div>
            <div className="info-item">
              <span className="info-label">创建时间:</span>
              <span className="info-value">{formatDate(order.createdAt)}</span>
            </div>
            <div className="info-item">
              <span className="info-label">支付时间:</span>
              <span className="info-value">
                {order.paidAt ? formatDate(order.paidAt) : '未支付'}
              </span>
            </div>
          </div>
        </section>

        {/* 商品信息 */}
        <section className="detail-section">
          <h3>商品信息</h3>
          <div className="items-list">
            {order.items.map((item, index) => (
              <div key={index} className="detail-item">
                <img src={item.image} alt={item.name} className="item-image" />
                <div className="item-details">
                  <h4 className="item-name">{item.name}</h4>
                  <p className="item-category">{item.category}</p>
                  <div className="item-specs">
                    <span className="item-weight">{item.weight}</span>
                    <span className="item-freshness">{item.freshness}</span>
                  </div>
                </div>
                <div className="item-pricing">
                  <div className="item-quantity">×{item.quantity}</div>
                  <div className="item-price">¥{item.price}</div>
                  <div className="item-total">¥{(item.price * item.quantity).toFixed(2)}</div>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* 价格汇总 */}
        <section className="detail-section">
          <h3>价格汇总</h3>
          <div className="price-summary">
            <div className="price-row">
              <span>商品总价</span>
              <span>¥{order.subtotal}</span>
            </div>
            <div className="price-row">
              <span>配送费用</span>
              <span>{order.shippingFee === 0 ? '免费' : `¥${order.shippingFee}`}</span>
            </div>
            <div className="price-row">
              <span>优惠折扣</span>
              <span className="discount">-¥{order.discount}</span>
            </div>
            <div className="price-row total">
              <span>实付金额</span>
              <span className="final-amount">¥{order.totalAmount}</span>
            </div>
          </div>
        </section>

        {/* 配送信息 */}
        <section className="detail-section">
          <h3>配送信息</h3>
          <div className="delivery-info">
            <p><strong>收货人:</strong> {order.deliveryInfo.recipientName}</p>
            <p><strong>联系电话:</strong> {order.deliveryInfo.phone}</p>
            <p><strong>配送地址:</strong> {order.deliveryInfo.address}</p>
            <p><strong>配送时间:</strong> {order.deliveryInfo.deliveryTime}</p>
            {order.deliveryInfo.specialInstructions && (
              <p><strong>特殊说明:</strong> {order.deliveryInfo.specialInstructions}</p>
            )}
          </div>
        </section>

        {/* 订单操作 */}
        {order.status === 'pending' && (
          <section className="detail-section">
            <h3>订单操作</h3>
            <div className="order-actions">
              <button className="btn-pay">立即支付</button>
              <button className="btn-cancel">取消订单</button>
            </div>
          </section>
        )}
      </div>
    </div>
  );
};

export default OrderDetail;