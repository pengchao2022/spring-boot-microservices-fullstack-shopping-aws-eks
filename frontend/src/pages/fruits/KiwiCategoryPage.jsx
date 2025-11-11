// src/pages/fruits/KiwiCategoryPage.jsx
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './KiwiCategoryPage.css';

const KiwiCategoryPage = () => {
  const [kiwis, setKiwis] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // CDN 域名
  const CLOUDFRONT_DOMAIN = 'https://d3sx9glhrpxv9q.cloudfront.net';

  useEffect(() => {
    fetchKiwis();
  }, []);

  const fetchKiwis = async () => {
    try {
      // 更新为正确的 API URL：/api/products/kiwi-category
      const response = await fetch('/api/products/kiwi-category');
      
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`);
      }
      
      const result = await response.json();
      
      if (result && result.success) {
        const kiwiData = Array.isArray(result.data) ? result.data : [];
        
        // 处理图片URL：将S3链接转换为CDN链接
        const simplifiedKiwis = kiwiData.map(kiwi => {
          let mainImageUrl = kiwi.mainImageUrl;
          
          // 如果图片URL是S3链接，转换为CDN链接
          if (mainImageUrl && mainImageUrl.includes('s3.us-east-1.amazonaws.com')) {
            const s3Path = mainImageUrl.split('.com/')[1];
            mainImageUrl = `${CLOUDFRONT_DOMAIN}/${s3Path}`;
          }
          
          return {
            id: kiwi.id,
            name: kiwi.name,
            englishName: kiwi.englishName,
            description: kiwi.shortDescription || kiwi.description,
            origin: kiwi.origin,
            sweetnessLevel: kiwi.sweetnessLevel,
            acidityLevel: kiwi.acidityLevel, // 猕猴桃特有的酸度属性
            isFeatured: kiwi.isFeatured,
            mainImageUrl: mainImageUrl,
            basePrice: kiwi.basePrice,
            weightUnit: kiwi.weightUnit || '500g',
            variety: kiwi.kiwiVariety, // 修正：使用 kiwiVariety 而不是 variety
            harvestSeason: kiwi.harvestSeason, // 收获季节
            vitaminCContent: kiwi.vitaminCContent, // 维生素C含量
            skinType: kiwi.skinType // 果皮类型
          };
        });
        
        setKiwis(simplifiedKiwis);
      } else {
        throw new Error(result.message || '获取猕猴桃数据失败');
      }
    } catch (err) {
      console.error('获取猕猴桃数据错误:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 获取价格信息
  const getPriceInfo = (kiwi) => {
    return {
      currentPrice: kiwi.basePrice || '0.00',
      weight: kiwi.weightUnit || '500g'
    };
  };

  if (loading) {
    return (
      <div className="kiwi-category-page">
        <div className="loading">🥝 加载猕猴桃数据中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="kiwi-category-page">
        <div className="error">❌ 加载失败: {error}</div>
        <button 
          onClick={fetchKiwis} 
          className="retry-button"
        >
          重试
        </button>
      </div>
    );
  }

  if (!Array.isArray(kiwis) || kiwis.length === 0) {
    return (
      <div className="kiwi-category-page">
        <div className="error">暂无猕猴桃产品数据</div>
        <button 
          onClick={fetchKiwis} 
          className="retry-button"
        >
          重新加载
        </button>
      </div>
    );
  }

  return (
    <div className="kiwi-category-page">
      <nav className="breadcrumb">
        <Link to="/">首页</Link>
        <span> / </span>
        <span>猕猴桃专区</span>
      </nav>

      <div className="page-header">
        <h1>🥝 猕猴桃专区</h1>
        <p>精选 {kiwis.length} 种优质猕猴桃，新鲜直达，营养丰富</p>
      </div>

      <div className="kiwis-grid">
        {kiwis.map(kiwi => {
          const priceInfo = getPriceInfo(kiwi);
          const description = kiwi.description || '优质猕猴桃产品';
          
          return (
            <div key={kiwi.id} className="kiwi-card">
              {/* 图片部分 */}
              <Link to={`/fruit/${kiwi.englishName}`} className="kiwi-link">
                <div className="kiwi-image">
                  <img 
                    src={kiwi.mainImageUrl} 
                    alt={kiwi.name}
                    className="kiwi-img"
                    onError={(e) => {
                      // 图片加载失败时使用默认图片
                      e.target.src = 'https://d3sx9glhrpxv9q.cloudfront.net/kiwi.png';
                    }}
                  />
                  {/* 已移除推荐标签 */}
                </div>
              </Link>
              
              {/* 文字信息部分 */}
              <div className="kiwi-info">
                <h3 className="kiwi-name">{kiwi.name}</h3>
                <p className="kiwi-description">{description}</p>
                
                {/* 猕猴桃特色信息 - 只保留甜度和酸度 */}
                {(kiwi.sweetnessLevel || kiwi.acidityLevel) && (
                  <div className="kiwi-attributes">
                    {kiwi.sweetnessLevel && (
                      <span className="attribute">甜度: {'⭐'.repeat(kiwi.sweetnessLevel)}</span>
                    )}
                    {kiwi.acidityLevel && (
                      <span className="attribute">酸度: {'🍋'.repeat(kiwi.acidityLevel)}</span>
                    )}
                  </div>
                )}
                
                {/* 价格信息 */}
                <div className="kiwi-price-section">
                  <div className="kiwi-price">
                    <span className="current-price">¥{priceInfo.currentPrice}</span>
                    <span className="weight">/{priceInfo.weight}</span>
                  </div>
                </div>

                {/* 查看详情按钮 */}
                <Link to={`/fruit/${kiwi.englishName}`} className="view-details-link">
                  <button className="view-details-btn">
                    查看详情
                  </button>
                </Link>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default KiwiCategoryPage;