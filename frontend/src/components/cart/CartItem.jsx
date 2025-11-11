import React from 'react';
import { Link } from 'react-router-dom';

const CartItem = ({ item, onUpdateQuantity, onRemove }) => {
  const handleQuantityChange = (newQuantity) => {
    if (newQuantity >= 1 && newQuantity <= item.maxQuantity) {
      onUpdateQuantity(item.id, newQuantity);
    }
  };

  const handleRemove = () => {
    if (window.confirm(`确定要删除 ${item.name} 吗？`)) {
      onRemove(item.id);
    }
  };

  const totalPrice = item.price * item.quantity;
  const totalOriginalPrice = item.originalPrice * item.quantity;
  const savings = totalOriginalPrice - totalPrice;

  return (
    <div className="cart-item">
      <div className="item-select">
        <input type="checkbox" defaultChecked />
      </div>
      
      <Link to={`/fruit/${item.englishName}`} className="item-image">
        <img src={item.imageUrl} alt={item.name} />
      </Link>

      <div className="item-info">
        <Link to={`/fruit/${item.englishName}`} className="item-name">
          {item.name}
        </Link>
        <p className="item-english-name">{item.englishName}</p>
        <div className="item-attributes">
          <span className="weight">{item.weight}</span>
          {item.stock < 10 && (
            <span className="low-stock">仅剩 {item.stock} 件</span>
          )}
        </div>
      </div>

      <div className="item-price">
        <div className="current-price">¥{item.price}</div>
        {item.originalPrice > item.price && (
          <div className="original-price">¥{item.originalPrice}</div>
        )}
      </div>

      <div className="item-quantity">
        <div className="quantity-controls">
          <button
            className="quantity-btn"
            onClick={() => handleQuantityChange(item.quantity - 1)}
            disabled={item.quantity <= 1}
          >
            -
          </button>
          <span className="quantity-display">{item.quantity}</span>
          <button
            className="quantity-btn"
            onClick={() => handleQuantityChange(item.quantity + 1)}
            disabled={item.quantity >= item.maxQuantity}
          >
            +
          </button>
        </div>
      </div>

      <div className="item-total">
        <div className="total-price">¥{totalPrice.toFixed(2)}</div>
        {savings > 0 && (
          <div className="savings">省 ¥{savings.toFixed(2)}</div>
        )}
      </div>

      <div className="item-actions">
        <button className="remove-btn" onClick={handleRemove}>
          删除
        </button>
      </div>
    </div>
  );
};

export default CartItem;