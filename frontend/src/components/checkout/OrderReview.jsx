import React from 'react';
import './OrderReview.css';

const OrderReview = ({ 
  orderPreview, 
  cartData, 
  onOrderConfirm, 
  onStepChange,
  loading 
}) => {
  if (!orderPreview) {
    return (
      <div className="order-review-loading">
        <p>加载订单信息中...</p>
      </div>
    );
  }

  const handleEditDelivery = () => {
    onStepChange(1);
  };

  const handleConfirmOrder = () => {
    onOrderConfirm();
  };

  return (
    <div className="order-review">
      <h2>订单确认</h2>
      
      {/* 配送信息概览 */}
      <div className="review-section">
        <div className="section-header">
          <h3>配送信息</h3>
          <button 
            type="button" 
            className="edit-btn"
            onClick={handleEditDelivery}
          >
            修改
          </button>
        </div>
        
        <div className="delivery-info">
          <div className="info-row">
            <strong>收货人：</strong>
            <span>{orderPreview.deliveryInfo.shippingFirstName} {orderPreview.deliveryInfo.shippingLastName}</span>
          </div>
          <div className="info-row">
            <strong>联系电话：</strong>
            <span>{orderPreview.deliveryInfo.shippingPhone}</span>
          </div>
          <div className="info-row">
            <strong>配送地址：</strong>
            <span>
              {orderPreview.deliveryInfo.shippingState} {orderPreview.deliveryInfo.shippingCity}
              {orderPreview.deliveryInfo.shippingAddressLine1}
              {orderPreview.deliveryInfo.shippingAddressLine2 && `，${orderPreview.deliveryInfo.shippingAddressLine2}`}
            </span>
          </div>
          <div className="info-row">
            <strong>配送时间：</strong>
            <span>
              {orderPreview.deliveryInfo.deliveryTime === 'anytime' && '任意时间'}
              {orderPreview.deliveryInfo.deliveryTime === 'morning' && '上午 (9:00-12:00)'}
              {orderPreview.deliveryInfo.deliveryTime === 'afternoon' && '下午 (14:00-18:00)'}
              {orderPreview.deliveryInfo.deliveryTime === 'evening' && '晚上 (18:00-21:00)'}
            </span>
          </div>
          {orderPreview.deliveryInfo.deliveryNote && (
            <div className="info-row">
              <strong>配送备注：</strong>
              <span>{orderPreview.deliveryInfo.deliveryNote}</span>
            </div>
          )}
        </div>
      </div>

      {/* 商品清单 */}
      <div className="review-section">
        <h3>商品清单</h3>
        <div className="order-items">
          {orderPreview.cartItems.map((item, index) => (
            <div key={index} className="order-item">
              <div className="item-image">
                <img 
                  src={item.imageUrl || '/images/placeholder-fruit.jpg'} 
                  alt={item.productName}
                  onError={(e) => {
                    e.target.src = '/images/placeholder-fruit.jpg';
                  }}
                />
              </div>
              <div className="item-details">
                <h4 className="item-name">{item.productName}</h4>
                <div className="item-specs">
                  <span className="item-weight">{item.weight}</span>
                </div>
              </div>
              <div className="item-pricing">
                <div className="item-quantity">×{item.quantity}</div>
                <div className="item-price">¥{(item.price * item.quantity).toFixed(2)}</div>
                <div className="item-unit-price">¥{item.price}/份</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 价格明细 */}
      <div className="review-section">
        <h3>价格明细</h3>
        <div className="price-breakdown">
          <div className="price-row">
            <span>商品总价：</span>
            <span>¥{orderPreview.subtotalAmount.toFixed(2)}</span>
          </div>
          <div className="price-row">
            <span>配送费：</span>
            <span>{orderPreview.shippingAmount === 0 ? '免费' : `¥${orderPreview.shippingAmount.toFixed(2)}`}</span>
          </div>
          {orderPreview.taxAmount > 0 && (
            <div className="price-row">
              <span>税费：</span>
              <span>¥{orderPreview.taxAmount.toFixed(2)}</span>
            </div>
          )}
          {orderPreview.discountAmount > 0 && (
            <div className="price-row discount">
              <span>优惠：</span>
              <span>-¥{orderPreview.discountAmount.toFixed(2)}</span>
            </div>
          )}
          <div className="price-row total">
            <strong>实付金额：</strong>
            <strong>¥{orderPreview.totalAmount.toFixed(2)}</strong>
          </div>
        </div>
      </div>

      {/* 确认按钮 */}
      <div className="review-actions">
        <button 
          type="button" 
          className="back-btn"
          onClick={handleEditDelivery}
        >
          返回修改
        </button>
        <button 
          type="button" 
          className="confirm-btn"
          onClick={handleConfirmOrder}
          disabled={loading}
        >
          {loading ? '处理中...' : '确认订单并支付'}
        </button>
      </div>
    </div>
  );
};

export default OrderReview;