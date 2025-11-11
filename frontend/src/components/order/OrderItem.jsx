import React from 'react';
import './OrderItem.css';

const OrderItem = ({ item, showActions = false, onReview }) => {
  return (
    <div className="order-item">
      <div className="item-main">
        <img src={item.image} alt={item.name} className="item-image" />
        
        <div className="item-info">
          <h4 className="item-name">{item.name}</h4>
          <p className="item-category">{item.category}</p>
          
          <div className="item-specifications">
            <span className="spec weight">{item.weight}</span>
            <span className="spec freshness">{item.freshness}</span>
            {item.origin && <span className="spec origin">{item.origin}</span>}
          </div>
          
          {item.description && (
            <p className="item-description">{item.description}</p>
          )}
        </div>
        
        <div className="item-pricing">
          <div className="price-section">
            <span className="price-label">单价</span>
            <span className="item-price">¥{item.price}</span>
          </div>
          
          <div className="quantity-section">
            <span className="quantity-label">数量</span>
            <span className="item-quantity">×{item.quantity}</span>
          </div>
          
          <div className="total-section">
            <span className="total-label">小计</span>
            <span className="item-total">¥{(item.price * item.quantity).toFixed(2)}</span>
          </div>
        </div>
      </div>

      {showActions && (
        <div className="item-actions">
          {item.canReview ? (
            <button 
              className="btn-review"
              onClick={() => onReview(item)}
            >
              评价商品
            </button>
          ) : (
            <button className="btn-rebuy">再次购买</button>
          )}
          
          {item.canComplain && (
            <button className="btn-complain">申请售后</button>
          )}
        </div>
      )}
    </div>
  );
};

export default OrderItem;