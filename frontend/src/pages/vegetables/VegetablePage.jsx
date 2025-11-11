import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import './VegetablePage.css';

const VegetablePage = () => {
  const { productName } = useParams(); // 动态获取蔬菜名称
  const navigate = useNavigate();
  
  const [product, setProduct] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // API基础URL
  const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

  useEffect(() => {
    const fetchProductData = async () => {
      if (!productName || productName === 'undefined') {
        setError('无效的产品名称');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        
        const response = await fetch(`${API_BASE_URL}/products/${productName}`);
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
          setProduct(result.data.product);
          
          if (result.data.variants && result.data.variants.length > 0) {
            setSelectedVariant(result.data.variants[0]);
          }
        } else {
          throw new Error(result.message || '获取产品信息失败');
        }
      } catch (err) {
        console.error('获取产品信息失败:', err);
        setError(err.message || '获取产品信息失败，请稍后重试');
      } finally {
        setLoading(false);
      }
    };

    fetchProductData();
  }, [productName, API_BASE_URL]);

  // 加载状态
  if (loading) {
    return (
      <div className="vegetable-page-loading">
        <div className="vegetable-page-spinner"></div>
        <p>正在加载蔬菜信息...</p>
      </div>
    );
  }

  // 错误状态
  if (error) {
    return (
      <div className="vegetable-page-error">
        <div className="vegetable-page-error-icon">⚠️</div>
        <h2>加载失败</h2>
        <p>{error}</p>
        <button onClick={() => navigate('/')}>返回首页</button>
      </div>
    );
  }

  return (
    <div className="vegetable-page-container">
      <nav className="vegetable-page-breadcrumb">
        <Link to="/">首页</Link>
        <span>/</span>
        <Link to="/">蔬菜</Link>
        <span>/</span>
        <span>{product?.name}</span>
      </nav>

      <div className="vegetable-page-product-container">
        <div className="vegetable-page-image-section">
          <img 
            src={product?.mainImageUrl} 
            alt={product?.name}
            className="vegetable-page-main-image"
          />
        </div>

        <div className="vegetable-page-info-section">
          <h1>{product?.name}</h1>
          <p>{product?.shortDescription}</p>
          
          {/* 可以根据需要添加更多蔬菜特定的UI */}
        </div>
      </div>
    </div>
  );
};

export default VegetablePage;