// src/pages/fruits/AppleCategoryPage.jsx
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './AppleCategoryPage.css';

const AppleCategoryPage = () => {
  const [apples, setApples] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // CDN 域名
  const CLOUDFRONT_DOMAIN = 'https://d3sx9glhrpxv9q.cloudfront.net';

  useEffect(() => {
    fetchApples();
  }, []);

  const fetchApples = async () => {
    try {
      const response = await fetch('/api/products/apple-category');
      
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`);
      }
      
      const result = await response.json();
      
      if (result && result.success) {
        const appleData = Array.isArray(result.data) ? result.data : [];
        
        // 处理图片URL：将S3链接转换为CDN链接
        const simplifiedApples = appleData.map(apple => {
          let mainImageUrl = apple.mainImageUrl;
          
          // 如果图片URL是S3链接，转换为CDN链接
          if (mainImageUrl && mainImageUrl.includes('s3.us-east-1.amazonaws.com')) {
            // 提取S3路径并转换为CDN路径
            const s3Path = mainImageUrl.split('.com/')[1];
            mainImageUrl = `${CLOUDFRONT_DOMAIN}/${s3Path}`;
          }
          
          return {
            id: apple.id,
            name: apple.name,
            englishName: apple.englishName,
            description: apple.shortDescription || apple.description,
            origin: apple.origin,
            sweetnessLevel: apple.sweetnessLevel,
            crunchinessLevel: apple.crunchinessLevel,
            isFeatured: apple.isFeatured,
            mainImageUrl: mainImageUrl, // 使用转换后的CDN URL
            basePrice: apple.basePrice,
            weightUnit: apple.weightUnit || '500g'
          };
        });
        
        setApples(simplifiedApples);
      } else {
        throw new Error(result.message || '获取苹果数据失败');
      }
    } catch (err) {
      console.error('获取苹果数据错误:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 获取价格信息
  const getPriceInfo = (apple) => {
    return {
      currentPrice: apple.basePrice || '0.00',
      weight: apple.weightUnit || '500g'
    };
  };

  // 处理查看详情点击
  const handleViewDetails = (englishName) => {
    window.location.href = `/fruit/${englishName}`;
  };

  if (loading) {
    return (
      <div className="apple-category-page">
        <div className="loading">🍎 加载苹果数据中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="apple-category-page">
        <div className="error">❌ 加载失败: {error}</div>
        <button 
          onClick={fetchApples} 
          className="retry-button"
        >
          重试
        </button>
      </div>
    );
  }

  if (!Array.isArray(apples) || apples.length === 0) {
    return (
      <div className="apple-category-page">
        <div className="error">暂无苹果产品数据</div>
        <button 
          onClick={fetchApples} 
          className="retry-button"
        >
          重新加载
        </button>
      </div>
    );
  }

  return (
    <div className="apple-category-page">
      <nav className="breadcrumb">
        <Link to="/">首页</Link>
        <span> / </span>
        <span>苹果专区</span>
      </nav>

      <div className="page-header">
        <h1>🍎 苹果专区</h1>
        <p>精选 {apples.length} 种优质苹果，新鲜直达，满足不同口味需求</p>
      </div>

      <div className="apples-grid">
        {apples.map(apple => {
          const priceInfo = getPriceInfo(apple);
          const description = apple.description || '优质苹果产品';
          
          return (
            <div key={apple.id} className="apple-card">
              {/* 图片部分 */}
              <Link to={`/fruit/${apple.englishName}`} className="apple-link">
                <div className="apple-image-container">
                  <img 
                    src={apple.mainImageUrl} 
                    alt={apple.name}
                    className="apple-img"
                  />
                  {/* 已移除推荐标签 */}
                </div>
              </Link>
              
              {/* 文字信息部分 */}
              <div className="apple-info">
                <h3 className="apple-name">{apple.name}</h3>
                <p className="apple-description">{description}</p>
                
                {/* 苹果特色信息 */}
                {(apple.sweetnessLevel || apple.crunchinessLevel) && (
                  <div className="apple-attributes">
                    {apple.sweetnessLevel && (
                      <span className="attribute">甜度: {'⭐'.repeat(apple.sweetnessLevel)}</span>
                    )}
                    {apple.crunchinessLevel && (
                      <span className="attribute">脆度: {'⭐'.repeat(apple.crunchinessLevel)}</span>
                    )}
                  </div>
                )}
                
                {/* 价格信息 */}
                <div className="apple-price-section">
                  <div className="apple-price">
                    <span className="current-price">¥{priceInfo.currentPrice}</span>
                    <span className="weight">/{priceInfo.weight}</span>
                  </div>
                </div>

                {/* 查看详情按钮 - 使用点击事件而不是Link包裹 */}
                <div className="view-details-btn-container">
                  <button 
                    className="apple-view-details-btn"
                    onClick={() => handleViewDetails(apple.englishName)}
                  >
                    查看详情
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default AppleCategoryPage;