import React from 'react';
import { Link } from 'react-router-dom';

const CartRecommendations = () => {
  const recommendations = [
    {
      id: 1,
      name: '黄金猕猴桃',
      englishName: 'golden-kiwi',
      imageUrl: 'https://d3sx9glhrpxv9q.cloudfront.net/kiwi-category/kiwi-replace-2.jpg',
      price: 22.8,
      originalPrice: 26.8,
      weight: '500g'
    },
    {
      id: 2,
      name: '有机樱桃番茄',
      englishName: 'organic-cherry-tomato',
      imageUrl: 'https://d3sx9glhrpxv9q.cloudfront.net/tomatoes.jpg',
      price: 16.5,
      originalPrice: 19.9,
      weight: '500g'
    },
    {
      id: 3,
      name: '宁夏硒砂瓜',
      englishName: 'ningxia-watermelon',
      imageUrl: 'https://d3sx9glhrpxv9q.cloudfront.net/watermelon.png',
      price: 25.9,
      originalPrice: 29.9,
      weight: '1kg'
    },
    {
      id: 4,
      name: '青苹果',
      englishName: 'green-apple',
      imageUrl: 'https://d3sx9glhrpxv9q.cloudfront.net/apple_category/apple-green.jpg',
      price: 14.9,
      originalPrice: 17.9,
      weight: '500g'
    }
  ];

  return (
    <div className="cart-recommendations">
      <h3>猜你喜欢</h3>
      <div className="recommendations-grid">
        {recommendations.map(product => (
          <Link
            key={product.id}
            to={`/fruit/${product.englishName}`}
            className="recommendation-card"
          >
            <div className="product-image">
              <img src={product.imageUrl} alt={product.name} />
            </div>
            <div className="product-info">
              <h4 className="product-name">{product.name}</h4>
              <p className="product-weight">{product.weight}</p>
              <div className="product-price">
                <span className="current-price">¥{product.price}</span>
                {product.originalPrice > product.price && (
                  <span className="original-price">¥{product.originalPrice}</span>
                )}
              </div>
            </div>
            <button className="add-to-cart-btn">加入购物车</button>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default CartRecommendations;