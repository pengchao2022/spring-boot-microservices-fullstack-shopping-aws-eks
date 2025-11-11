import React from 'react';
import './CheckoutSummary.css';

const CheckoutSummary = ({ cartData, orderPreview, currentStep }) => {
  if (!cartData || !cartData.items) {
    return null;
  }

  const calculateTotals = () => {
    const subtotal = cartData.items.reduce((total, item) => 
      total + (item.price * item.quantity), 0
    );
    
    const shipping = orderPreview?.shippingAmount || 0;
    const tax = orderPreview?.taxAmount || 0;
    const discount = orderPreview?.discountAmount || 0;
    const total = subtotal + shipping + tax - discount;

    return { subtotal, shipping, tax, discount, total };
  };

  const totals = calculateTotals();

  return (
    <div className="checkout-summary">
      <div className="summary-card">
        <h3 className="summary-title">订单摘要</h3>
        
        {/* 商品列表 */}
        <div className="summary-items">
          {cartData.items.map((item, index) => (
            <div key={index} className="summary-item">
              <div className="item-image">
                <img 
                  src={item.imageUrl || '/images/placeholder-fruit.jpg'} 
                  alt={item.productName}
                  onError={(e) => {
                    e.target.src = '/images/placeholder-fruit.jpg';
                  }}
                />
                <span className="item-quantity">{item.quantity}</span>
              </div>
              <div className="item-info">
                <h4 className="item-name">{item.productName}</h4>
                <p className="item-weight">{item.weight}</p>
              </div>
              <div className="item-price">
                ¥{(item.price * item.quantity).toFixed(2)}
              </div>
            </div>
          ))}
        </div>

        {/* 价格明细 */}
        <div className="price-details">
          <div className="price-row">
            <span>商品总价：</span>
            <span>¥{totals.subtotal.toFixed(2)}</span>
          </div>
          
          <div className="price-row">
            <span>配送费：</span>
            <span className={totals.shipping === 0 ? 'free' : ''}>
              {totals.shipping === 0 ? '免费' : `¥${totals.shipping.toFixed(2)}`}
            </span>
          </div>
          
          {totals.tax > 0 && (
            <div className="price-row">
              <span>税费：</span>
              <span>¥{totals.tax.toFixed(2)}</span>
            </div>
          )}
          
          {totals.discount > 0 && (
            <div className="price-row discount">
              <span>优惠：</span>
              <span>-¥{totals.discount.toFixed(2)}</span>
            </div>
          )}
          
          <div className="price-divider"></div>
          
          <div className="price-row total">
            <strong>实付金额：</strong>
            <strong className="total-amount">¥{totals.total.toFixed(2)}</strong>
          </div>
        </div>

        {/* 配送信息预览 */}
        {currentStep >= 2 && orderPreview && (
          <div className="delivery-preview">
            <h4>配送信息</h4>
            <div className="delivery-details">
              <p>
                <strong>{orderPreview.deliveryInfo.shippingFirstName} {orderPreview.deliveryInfo.shippingLastName}</strong>
              </p>
              <p>{orderPreview.deliveryInfo.shippingPhone}</p>
              <p className="address">
                {orderPreview.deliveryInfo.shippingState} {orderPreview.deliveryInfo.shippingCity}
                {orderPreview.deliveryInfo.shippingAddressLine1}
              </p>
            </div>
          </div>
        )}

        {/* 优惠信息 */}
        <div className="promotion-section">
          <div className="promotion-tag">
            <span className="tag-icon">🎁</span>
            <span>新鲜直达 · 品质保证</span>
          </div>
          <div className="promotion-tag">
            <span className="tag-icon">🚚</span>
            <span>满¥10免配送费</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutSummary;