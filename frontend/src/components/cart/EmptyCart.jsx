import React from 'react';
import { Link } from 'react-router-dom';

const EmptyCart = ({ onContinueShopping }) => {
  return (
    <div className="empty-cart">
      <div className="empty-cart-content">
        <div className="empty-cart-icon">🛒</div>
        <h2>购物车空空如也</h2>
        <p>快去挑选一些美味的水果吧！</p>
        
        <div className="empty-cart-actions">
          <button className="shopping-btn" onClick={onContinueShopping}>
            去购物
          </button>
          <Link to="/products?filter=fruit" className="fruit-link">
            浏览水果
          </Link>
          <Link to="/products?filter=vegetable" className="vegetable-link">
            浏览蔬菜
          </Link>
        </div>

        <div className="featured-categories">
          <h3>热门分类</h3>
          <div className="category-grid">
            <Link to="/products?filter=apple" className="category-card">
              <div className="category-icon">🍎</div>
              <span>苹果专区</span>
            </Link>
            <Link to="/products?filter=kiwi" className="category-card">
              <div className="category-icon">🥝</div>
              <span>猕猴桃专区</span>
            </Link>
            <Link to="/products?filter=fruit" className="category-card">
              <div className="category-icon">🍊</div>
              <span>所有水果</span>
            </Link>
            <Link to="/products?filter=vegetable" className="category-card">
              <div className="category-icon">🥦</div>
              <span>新鲜蔬菜</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EmptyCart;