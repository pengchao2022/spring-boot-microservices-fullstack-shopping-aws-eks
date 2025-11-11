import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import './FruitDetailPage.css';

const FruitDetailPage = () => {
  const { productName } = useParams();
  const navigate = useNavigate();
  
  const [product, setProduct] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addingToCart, setAddingToCart] = useState(false);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [pendingAction, setPendingAction] = useState(null); // 'addToCart' or 'buyNow'
  const [cartMessage, setCartMessage] = useState('');

  const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

  // 检查用户登录状态
  useEffect(() => {
    checkLoginStatus();
  }, []);

  const checkLoginStatus = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        // 可以添加API调用来验证token有效性
        setIsLoggedIn(true);
      } else {
        setIsLoggedIn(false);
      }
    } catch (error) {
      console.error('检查登录状态失败:', error);
      setIsLoggedIn(false);
    }
  };

  // 从token获取用户ID
  const getUserIdFromToken = async (token) => {
    try {
      // 这里假设token是JWT，可以解码获取用户信息
      // 或者调用API获取当前用户信息
      const response = await fetch(`${API_BASE_URL}/users/me`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const userData = await response.json();
        return userData.id || userData.userId;
      } else {
        throw new Error('获取用户信息失败');
      }
    } catch (error) {
      console.error('获取用户ID失败:', error);
      // 如果无法从API获取，尝试从token解码（简单实现）
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.userId || payload.sub;
      } catch (decodeError) {
        console.error('解码token失败:', decodeError);
        throw new Error('无法获取用户ID');
      }
    }
  };

  // 将S3链接转换为CloudFront CDN链接
  const convertToCDNUrl = (url) => {
    if (!url || typeof url !== 'string') return url;
    
    // 如果是S3直接链接，转换为CloudFront
    if (url.includes('s3fruits202511010101.s3.us-east-1.amazonaws.com')) {
      const path = url.split('.com/')[1];
      return `https://d3sx9glhrpxv9q.cloudfront.net/${path}`;
    }
    
    // 如果已经是CloudFront链接，直接返回
    return url;
  };

  useEffect(() => {
    const fetchProductData = async () => {
      if (!productName) {
        setError('产品名称无效');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        
        const response = await fetch(`${API_BASE_URL}/products/${productName}`);
        
        if (!response.ok) {
          throw new Error(`HTTP错误! 状态: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success && result.data) {
          // 修复：从 result.data.product 获取产品数据
          const productData = result.data.product || result.data;
          
          if (productData) {
            setProduct(productData);
            
            // 设置默认变体（使用产品基础信息）
            setSelectedVariant({
              variantName: '标准',
              price: productData.basePrice || productData.price || 0,
              originalPrice: productData.originalPrice,
              weight: productData.weightUnit || '500g'
            });
          } else {
            throw new Error('产品数据为空');
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

  // 处理需要登录的操作
  const handleActionRequiringLogin = (action) => {
    if (!isLoggedIn) {
      setPendingAction(action);
      setShowLoginModal(true);
      return false;
    }
    return true;
  };

  const handleAddToCart = async () => {
    // 检查登录状态
    if (!handleActionRequiringLogin('addToCart')) {
      return;
    }

    if (!selectedVariant || !product) return;
    
    try {
      setAddingToCart(true);
      setCartMessage('');
      
      // 获取用户token
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('用户未登录');
      }

      // 获取用户ID
      const userId = await getUserIdFromToken(token);
      
      // 准备添加到购物车的请求数据
      const cartItem = {
        productId: product.id,
        productName: product.name,
        imageUrl: product.mainImageUrl || product.imageUrls?.[0],
        price: selectedVariant.price,
        quantity: quantity,
        weight: selectedVariant.weight
      };

      // 调用真实的购物车API
      const response = await fetch(`${API_BASE_URL}/cart/items`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': userId.toString(),
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(cartItem)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || `添加到购物车失败: ${response.status}`);
      }

      const result = await response.json();
      
      // 显示成功消息
      setCartMessage(`成功添加 ${quantity} 件 ${product.name} 到购物车`);
      
      // 3秒后清除消息
      setTimeout(() => {
        setCartMessage('');
      }, 3000);

    } catch (err) {
      console.error('添加到购物车失败:', err);
      setCartMessage(`添加失败: ${err.message}`);
      
      // 3秒后清除错误消息
      setTimeout(() => {
        setCartMessage('');
      }, 3000);
    } finally {
      setAddingToCart(false);
    }
  };

  const handleBuyNow = () => {
    // 检查登录状态
    if (!handleActionRequiringLogin('buyNow')) {
      return;
    }

    if (!selectedVariant || !product) return;
    setCartMessage(`立即购买功能开发中，将购买 ${quantity} 件 ${product.name}`);
    
    // 3秒后清除消息
    setTimeout(() => {
      setCartMessage('');
    }, 3000);
  };

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity >= 1 && newQuantity <= 99) {
      setQuantity(newQuantity);
    }
  };

  // 跳转到登录页面
  const goToLogin = () => {
    navigate('/login', { 
      state: { 
        from: window.location.pathname,
        pendingAction: pendingAction
      } 
    });
  };

  // 关闭登录提示模态框
  const closeLoginModal = () => {
    setShowLoginModal(false);
    setPendingAction(null);
  };

  // 获取详情页展示的图片 - 修复版本，处理字符串格式的 imageUrls 并转换为CDN
  const getDetailImages = () => {
    if (!product) return [];
    
    try {
      let images = [];
      
      // 处理 imageUrls - 可能是数组或JSON字符串
      if (product.imageUrls) {
        if (Array.isArray(product.imageUrls)) {
          // 如果是数组，直接使用
          images = [...product.imageUrls];
        } else if (typeof product.imageUrls === 'string') {
          // 如果是字符串，尝试解析JSON
          const parsed = JSON.parse(product.imageUrls);
          if (Array.isArray(parsed)) {
            images = parsed;
          }
        }
      }
      
      // 确保返回有效的URL数组，并转换为CDN
      const validImages = images
        .filter(url => url && typeof url === 'string' && url.startsWith('http'))
        .map(url => convertToCDNUrl(url));
      
      if (validImages.length > 0) {
        return validImages;
      }
    } catch (error) {
      console.error('处理 imageUrls 失败:', error);
      
      // 如果JSON解析失败，尝试其他格式处理
      if (typeof product.imageUrls === 'string') {
        try {
          // 处理格式错误的字符串
          const cleanedString = product.imageUrls
            .replace(/^\["/, '')
            .replace(/"\]$/, '')
            .replace(/\\"/g, '"');
          
          const urls = cleanedString
            .split('","')
            .filter(url => url && url.startsWith('http'))
            .map(url => convertToCDNUrl(url));
          
          if (urls.length > 0) {
            return urls;
          }
        } catch (fallbackError) {
          console.error('备用处理也失败:', fallbackError);
        }
      }
    }
    
    // 后备方案：使用 mainImageUrl，也转换为CDN
    if (product.mainImageUrl) {
      const cdnMainImage = convertToCDNUrl(product.mainImageUrl);
      return [cdnMainImage];
    }
    
    // 最后的后备方案 - 使用默认CDN图片
    return ['https://d3sx9glhrpxv9q.cloudfront.net/fruit-placeholder.jpg'];
  };

  // 获取当前选中的图片
  const getCurrentImage = () => {
    const images = getDetailImages();
    return images[selectedImageIndex] || images[0] || 'https://d3sx9glhrpxv9q.cloudfront.net/fruit-placeholder.jpg';
  };

  // 解析营养信息
  const parseNutritionalInfo = (nutritionalInfo) => {
    if (!nutritionalInfo) return null;
    
    try {
      // 如果是 JSON 字符串，解析它
      if (typeof nutritionalInfo === 'string' && nutritionalInfo.trim().startsWith('{')) {
        const parsed = JSON.parse(nutritionalInfo);
        return parsed;
      }
      // 如果已经是对象，直接返回
      else if (typeof nutritionalInfo === 'object') {
        return nutritionalInfo;
      }
      // 其他情况返回 null
      else {
        return null;
      }
    } catch (error) {
      console.error('解析营养信息失败:', error);
      return null;
    }
  };

  // 渲染营养信息
  const renderNutritionalInfo = () => {
    const nutritionalData = parseNutritionalInfo(product.nutritionalInfo);
    
    if (!nutritionalData) {
      return <p>暂无营养信息</p>;
    }

    return (
      <div className="nutritional-grid">
        {nutritionalData.sugar && (
          <div className="nutrition-item">
            <span className="nutrition-label">糖分:</span>
            <span className="nutrition-value">{nutritionalData.sugar}</span>
          </div>
        )}
        {nutritionalData.calories && (
          <div className="nutrition-item">
            <span className="nutrition-label">热量:</span>
            <span className="nutrition-value">{nutritionalData.calories}</span>
          </div>
        )}
        {nutritionalData.potassium && (
          <div className="nutrition-item">
            <span className="nutrition-label">钾:</span>
            <span className="nutrition-value">{nutritionalData.potassium}</span>
          </div>
        )}
        {nutritionalData.vitamin_c && (
          <div className="nutrition-item">
            <span className="nutrition-label">维生素C:</span>
            <span className="nutrition-value">{nutritionalData.vitamin_c}</span>
          </div>
        )}
        {nutritionalData.dietary_fiber && (
          <div className="nutrition-item">
            <span className="nutrition-label">膳食纤维:</span>
            <span className="nutrition-value">{nutritionalData.dietary_fiber}</span>
          </div>
        )}
        {/* 添加其他可能的营养字段 */}
        {nutritionalData.protein && (
          <div className="nutrition-item">
            <span className="nutrition-label">蛋白质:</span>
            <span className="nutrition-value">{nutritionalData.protein}</span>
          </div>
        )}
        {nutritionalData.fat && (
          <div className="nutrition-item">
            <span className="nutrition-label">脂肪:</span>
            <span className="nutrition-value">{nutritionalData.fat}</span>
          </div>
        )}
      </div>
    );
  };

  if (loading) {
    return (
      <div className="fruit-detail-page">
        <div className="loading">
          <div className="spinner"></div>
          <p>正在加载产品信息...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="fruit-detail-page">
        <div className="error-page">
          <h2>加载失败</h2>
          <p>{error}</p>
          <div className="action-buttons">
            <button onClick={() => navigate(-1)} className="back-button">返回上页</button>
            <button onClick={() => navigate('/')} className="home-button">返回首页</button>
          </div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="fruit-detail-page">
        <div className="not-found">
          <h2>产品未找到</h2>
          <p>抱歉，没有找到您要查看的产品。</p>
          <div className="action-buttons">
            <button onClick={() => navigate(-1)} className="back-button">返回上页</button>
            <button onClick={() => navigate('/')} className="home-button">返回首页</button>
          </div>
        </div>
      </div>
    );
  }

  const detailImages = getDetailImages();
  const currentImage = getCurrentImage();

  return (
    <div className="fruit-detail-page">
      {/* 面包屑导航 */}
      <nav className="breadcrumb">
        <Link to="/">首页</Link>
        <span> / </span>
        <Link to="/products">产品商城</Link>
        <span> / </span>
        <Link to={`/products?filter=${product.categoryType?.toLowerCase() || 'fruit'}`}>
          {product.categoryType === 'FRUIT' ? '水果专区' : '蔬菜专区'}
        </Link>
        <span> / </span>
        <span>{product.name}</span>
      </nav>

      <div className="product-container">
        {/* 产品图片区域 - 使用 imageUrls 展示详情图片 */}
        <div className="product-image-section">
          {/* 主图 */}
          <div className="main-image-container">
            <img 
              src={currentImage}
              alt={product.name}
              className="main-image"
              onError={(e) => {
                e.target.src = 'https://d3sx9glhrpxv9q.cloudfront.net/fruit-placeholder.jpg';
              }}
            />
          </div>

          {/* 缩略图列表（如果有多个图片） */}
          {detailImages.length > 1 && (
            <div className="thumbnail-list">
              {detailImages.map((imageUrl, index) => (
                <div 
                  key={index}
                  className={`thumbnail-item ${selectedImageIndex === index ? 'active' : ''}`}
                  onClick={() => setSelectedImageIndex(index)}
                >
                  <img 
                    src={imageUrl}
                    alt={`${product.name} ${index + 1}`}
                    className="thumbnail-image"
                    onError={(e) => {
                      e.target.src = 'https://d3sx9glhrpxv9q.cloudfront.net/fruit-placeholder.jpg';
                    }}
                  />
                </div>
              ))}
            </div>
          )}
        </div>

        {/* 产品信息区域 */}
        <div className="product-info-section">
          <h1 className="product-name">{product.name}</h1>
          <p className="product-english-name">{product.englishName}</p>
          <p className="short-description">{product.shortDescription || product.description}</p>
          
          {/* 价格区域 */}
          <div className="price-section">
            <div className="price">
              <span className="current-price">¥{selectedVariant?.price}</span>
              {selectedVariant?.originalPrice && selectedVariant.originalPrice > selectedVariant.price && (
                <span className="original-price">¥{selectedVariant.originalPrice}</span>
              )}
            </div>
            <span className="weight-unit">/{selectedVariant?.weight}</span>
          </div>

          {/* 产品属性 */}
          <div className="product-attributes">
            {product.origin && (
              <div className="attribute-item">
                <span className="attribute-label">产地：</span>
                <span className="attribute-value">{product.origin}</span>
              </div>
            )}
            
            {product.harvestSeason && (
              <div className="attribute-item">
                <span className="attribute-label">收获季节：</span>
                <span className="attribute-value">{product.harvestSeason}</span>
              </div>
            )}
            
            {product.sweetnessLevel && (
              <div className="attribute-item">
                <span className="attribute-label">甜度：</span>
                <span className="attribute-value">{'⭐'.repeat(product.sweetnessLevel)}</span>
              </div>
            )}
            
            {product.vitaminCContent && (
              <div className="attribute-item">
                <span className="attribute-label">维生素C：</span>
                <span className="attribute-value">{product.vitaminCContent}</span>
              </div>
            )}

            {product.growingMethod && (
              <div className="attribute-item">
                <span className="attribute-label">种植方式：</span>
                <span className="attribute-value">{product.growingMethod}</span>
              </div>
            )}

            {product.certification && (
              <div className="attribute-item">
                <span className="attribute-label">认证：</span>
                <span className="attribute-value">{product.certification}</span>
              </div>
            )}
          </div>

          {/* 数量选择 */}
          <div className="quantity-section">
            <span className="quantity-label">数量：</span>
            <div className="quantity-controls">
              <button 
                className="quantity-btn"
                onClick={() => handleQuantityChange(quantity - 1)}
                disabled={quantity <= 1}
              >
                -
              </button>
              <span className="quantity-display">{quantity}</span>
              <button 
                className="quantity-btn"
                onClick={() => handleQuantityChange(quantity + 1)}
                disabled={quantity >= 99}
              >
                +
              </button>
            </div>
          </div>

          {/* 操作按钮 */}
          <div className="action-section">
            <button 
              className="add-to-cart-btn"
              onClick={handleAddToCart}
              disabled={addingToCart}
            >
              {addingToCart ? '添加中...' : '加入购物车'}
            </button>
            <button 
              className="buy-now-btn"
              onClick={handleBuyNow}
            >
              立即购买
            </button>
          </div>

          {/* 购物车消息提示 */}
          {cartMessage && (
            <div className={`cart-message ${cartMessage.includes('失败') ? 'error' : 'success'}`}>
              {cartMessage}
            </div>
          )}

          {/* 产品详情 */}
          <div className="product-details">
            <h3>产品详情</h3>
            <p>{product.description}</p>
            
            {product.nutritionalInfo && (
              <div className="nutritional-info">
                <h4>营养信息</h4>
                {renderNutritionalInfo()}
              </div>
            )}
            
            {product.storageMethod && (
              <div className="storage-info">
                <h4>储存方法</h4>
                <p>{product.storageMethod}</p>
              </div>
            )}

            {product.tasteDescription && (
              <div className="taste-info">
                <h4>口感描述</h4>
                <p>{product.tasteDescription}</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 登录提示模态框 */}
      {showLoginModal && (
        <div className="login-modal-overlay">
          <div className="login-modal">
            <h3>需要登录</h3>
            <p>请先登录账号才能{pendingAction === 'addToCart' ? '加入购物车' : '立即购买'}</p>
            <div className="login-modal-buttons">
              <button className="login-btn" onClick={goToLogin}>
                立即登录
              </button>
              <button className="cancel-btn" onClick={closeLoginModal}>
                稍后再说
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FruitDetailPage;