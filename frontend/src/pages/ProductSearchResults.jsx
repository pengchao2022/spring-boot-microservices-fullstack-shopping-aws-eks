import React, { useState, useEffect } from 'react';
import { useSearchParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const ProductSearchResults = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [searchResults, setSearchResults] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Elasticsearch 搜索端点
  const SEARCH_API_URL = '/api/products/search';

  useEffect(() => {
    const keyword = searchParams.get('search');
    
    if (!keyword) {
      navigate('/');
      return;
    }

    setSearchTerm(keyword);
    performSearch(keyword);
  }, [searchParams, navigate]);

  // 执行 Elasticsearch 搜索
  const performSearch = async (keyword) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await axios.get(SEARCH_API_URL, {
        params: {
          keyword: keyword,
          page: 0,
          size: 50
        }
      });

      if (response.data && response.data.success) {
        setSearchResults(response.data.data || []);
      } else {
        setSearchResults([]);
        setError('搜索无结果');
      }
    } catch (error) {
      console.error('搜索失败:', error);
      setError('搜索失败，请稍后重试');
      setSearchResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  // 处理新的搜索
  const handleNewSearch = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      navigate(`/products?search=${encodeURIComponent(searchTerm.trim())}`);
    }
  };

  // 使用 CloudFront 域名
  const CLOUDFRONT_DOMAIN = 'https://d3sx9glhrpxv9q.cloudfront.net';

  // 获取产品图片
  const getProductImage = (product) => {
    if (product.mainImageUrl) {
      return product.mainImageUrl;
    }
    
    // 根据英文名称映射图片
    const imageMap = {
      'apple': 'apples.jpg',
      'kiwi': 'kiwi.png',
      'watermelon': 'watermelon.png',
      'orange': 'oranges.jpg',
      'strawberry': 'strawberries.jpg',
      'grape': 'grape.png',
      'pomegranate': 'shiliu.png',
      'red-date': 'hongzao.png',
      'plum': 'plums.jpg',
      'tomato': 'tomatoes.jpg',
      'chilli-pepper': 'chilli-pepper.jpg',
      'potato': 'potatoes.jpg'
    };

    const imageName = imageMap[product.englishName?.toLowerCase()] || 'default-product.jpg';
    return `${CLOUDFRONT_DOMAIN}/${imageName}`;
  };

  // 获取产品详情链接
  const getProductDetailLink = (product) => {
    if (product.englishName === 'apple') {
      return '/fruits/apples';
    } else if (product.englishName === 'kiwi') {
      return '/fruits/kiwis';
    } else if (product.categoryType === 'FRUIT') {
      return `/fruit/${product.englishName}`;
    } else {
      return `/vegetable/${product.englishName}`;
    }
  };

  if (isLoading) {
    return (
      <div style={containerStyle}>
        <div style={loadingStyle}>
          <div style={spinnerStyle}></div>
          <p>正在搜索 "{searchParams.get('search')}"...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={containerStyle}>
        <div style={errorStyle}>
          <h2>搜索出错</h2>
          <p>{error}</p>
          <button 
            onClick={() => navigate('/')}
            style={homeButtonStyle}
          >
            返回首页
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={containerStyle}>
      {/* 搜索头部 */}
      <div style={headerStyle}>
        <div style={searchBoxStyle}>
          <form onSubmit={handleNewSearch} style={searchFormStyle}>
            <input
              type="text"
              placeholder="搜索水果或蔬菜..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={searchInputStyle}
            />
            <button type="submit" style={searchButtonStyle}>
              搜索
            </button>
          </form>
        </div>
        
        <div style={resultsInfoStyle}>
          <h1>搜索结果</h1>
          <p>关键词: "{searchParams.get('search')}"</p>
          <p>找到 {searchResults.length} 个相关产品</p>
        </div>
      </div>

      {/* 搜索结果网格 */}
      {searchResults.length > 0 ? (
        <div style={resultsGridStyle}>
          {searchResults.map((product) => (
            <Link
              key={product.id}
              to={getProductDetailLink(product)}
              style={productCardStyle}
            >
              <div style={imageContainerStyle}>
                <img
                  src={getProductImage(product)}
                  alt={product.name}
                  style={productImageStyle}
                  onError={(e) => {
                    e.target.src = `${CLOUDFRONT_DOMAIN}/default-product.jpg`;
                  }}
                />
              </div>
              <div style={productInfoStyle}>
                <h3 style={productNameStyle}>{product.name}</h3>
                <p style={productEnglishNameStyle}>{product.englishName}</p>
                <p style={productDescriptionStyle}>
                  {product.shortDescription || product.description}
                </p>
                <div style={productMetaStyle}>
                  <span style={priceStyle}>¥{product.basePrice}</span>
                  <span style={originStyle}>{product.origin}</span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      ) : (
        <div style={noResultsStyle}>
          <h2>没有找到相关产品</h2>
          <p>请尝试其他关键词或返回首页浏览所有产品</p>
          <button 
            onClick={() => navigate('/')}
            style={homeButtonStyle}
          >
            返回首页
          </button>
        </div>
      )}
    </div>
  );
};

// 样式定义
const containerStyle = {
  minHeight: '100vh',
  backgroundColor: '#f8f9fa',
  padding: '2rem 1rem',
};

const headerStyle = {
  maxWidth: '1200px',
  margin: '0 auto 3rem auto',
  textAlign: 'center',
};

const searchBoxStyle = {
  marginBottom: '2rem',
};

const searchFormStyle = {
  display: 'flex',
  maxWidth: '500px',
  margin: '0 auto',
  borderRadius: '25px',
  overflow: 'hidden',
  boxShadow: '0 4px 15px rgba(0, 0, 0, 0.1)',
};

const searchInputStyle = {
  flex: 1,
  border: 'none',
  outline: 'none',
  padding: '0.8rem 1.5rem',
  fontSize: '1rem',
  color: '#333',
};

const searchButtonStyle = {
  background: 'linear-gradient(135deg, #ff5722, #e64a19)',
  color: 'white',
  border: 'none',
  padding: '0.8rem 1.5rem',
  fontSize: '1rem',
  fontWeight: 'bold',
  cursor: 'pointer',
  minWidth: '100px',
};

const resultsInfoStyle = {
  marginBottom: '2rem',
};

const resultsGridStyle = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
  gap: '2rem',
  maxWidth: '1200px',
  margin: '0 auto',
};

const productCardStyle = {
  backgroundColor: 'white',
  borderRadius: '15px',
  textDecoration: 'none',
  color: '#333',
  boxShadow: '0 4px 15px rgba(0, 0, 0, 0.1)',
  transition: 'all 0.3s ease',
  overflow: 'hidden',
  display: 'flex',
  flexDirection: 'column',
  cursor: 'pointer',
};

const imageContainerStyle = {
  width: '100%',
  height: '200px',
  overflow: 'hidden',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  backgroundColor: 'white',
};

const productImageStyle = {
  width: '100%',
  height: '100%',
  objectFit: 'contain',
  padding: '1rem',
};

const productInfoStyle = {
  padding: '1.5rem',
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
};

const productNameStyle = {
  fontSize: '1.2rem',
  fontWeight: 'bold',
  margin: '0 0 0.5rem 0',
  color: '#333',
};

const productEnglishNameStyle = {
  fontSize: '0.9rem',
  color: '#666',
  margin: '0 0 1rem 0',
  fontStyle: 'italic',
};

const productDescriptionStyle = {
  fontSize: '0.9rem',
  color: '#666',
  margin: '0 0 1rem 0',
  lineHeight: '1.4',
  flex: 1,
};

const productMetaStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginTop: 'auto',
};

const priceStyle = {
  fontSize: '1.1rem',
  fontWeight: 'bold',
  color: '#ff5722',
};

const originStyle = {
  fontSize: '0.8rem',
  color: '#999',
};

const loadingStyle = {
  textAlign: 'center',
  padding: '4rem 1rem',
};

const spinnerStyle = {
  width: '50px',
  height: '50px',
  border: '5px solid #f3f3f3',
  borderTop: '5px solid #ff5722',
  borderRadius: '50%',
  animation: 'spin 1s linear infinite',
  margin: '0 auto 1rem auto',
};

const errorStyle = {
  textAlign: 'center',
  padding: '4rem 1rem',
  color: '#d32f2f',
};

const noResultsStyle = {
  textAlign: 'center',
  padding: '4rem 1rem',
  color: '#666',
};

const homeButtonStyle = {
  background: 'linear-gradient(135deg, #ff5722, #e64a19)',
  color: 'white',
  border: 'none',
  padding: '0.8rem 2rem',
  borderRadius: '25px',
  fontSize: '1rem',
  fontWeight: 'bold',
  cursor: 'pointer',
  marginTop: '1rem',
};

// 添加 CSS 动画
const styleSheet = document.styleSheets[0];
if (styleSheet) {
  styleSheet.insertRule(`
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `, styleSheet.cssRules.length);
}

export default ProductSearchResults;