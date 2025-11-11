import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import FruitList from '../components/product/FruitList';
import './ProductList.css';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedFilter, setSelectedFilter] = useState('all'); // 筛选条件
  
  const location = useLocation();
  const navigate = useNavigate();

  // 从 URL 参数获取筛选信息
  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const filterParam = searchParams.get('filter') || 'all';
    setSelectedFilter(filterParam);
    
    // 根据筛选条件获取数据
    fetchProducts(filterParam);
  }, [location.search]);

  const fetchProducts = async (filter) => {
    try {
      setLoading(true);
      let apiUrl = '';
      let pageTitle = '';
      let pageDescription = '';

      // 根据筛选条件确定API端点和页面信息
      switch (filter) {
        case 'apple':
          apiUrl = '/api/products/apple-category';
          pageTitle = '🍎 苹果专区';
          pageDescription = '精选优质苹果，新鲜直达，脆甜可口';
          break;
        case 'kiwi':
          apiUrl = '/api/products/kiwi-category';
          pageTitle = '🥝 猕猴桃专区';
          pageDescription = '精选优质猕猴桃，新鲜直达，营养丰富';
          break;
        case 'fruit':
          apiUrl = '/api/products/category/FRUIT';
          pageTitle = '🍊 水果专区';
          pageDescription = '各种新鲜水果，品质保证';
          break;
        case 'vegetable':
          apiUrl = '/api/products/category/VEGETABLE';
          pageTitle = '🥦 蔬菜专区';
          pageDescription = '新鲜蔬菜，健康生活';
          break;
        case 'all':
        default:
          apiUrl = '/api/products/category/FRUIT'; // 默认显示水果
          pageTitle = '🍎 水果商城';
          pageDescription = '发现各种优质产品';
      }

      const response = await fetch(apiUrl);
      
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`);
      }
      
      const result = await response.json();
      
      if (result && result.success) {
        const productData = Array.isArray(result.data) ? result.data : [];
        
        // 设置页面标题
        document.title = pageTitle;
        
        setProducts(productData);
      } else {
        throw new Error(result.message || '获取产品数据失败');
      }
    } catch (err) {
      console.error('获取产品数据错误:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 处理筛选条件变化
  const handleFilterChange = (filter) => {
    setSelectedFilter(filter);
    if (filter === 'all') {
      navigate('/products');
    } else {
      navigate(`/products?filter=${filter}`);
    }
  };

  // 获取页面标题和描述
  const getPageInfo = () => {
    switch (selectedFilter) {
      case 'apple':
        return {
          title: '🍎 苹果专区',
          description: '精选优质苹果，新鲜直达，脆甜可口',
          breadcrumb: '苹果专区'
        };
      case 'kiwi':
        return {
          title: '🥝 猕猴桃专区',
          description: '精选优质猕猴桃，新鲜直达，营养丰富',
          breadcrumb: '猕猴桃专区'
        };
      case 'fruit':
        return {
          title: '🍊 水果专区',
          description: '各种新鲜水果，品质保证',
          breadcrumb: '水果专区'
        };
      case 'vegetable':
        return {
          title: '🥦 蔬菜专区',
          description: '新鲜蔬菜，健康生活',
          breadcrumb: '蔬菜专区'
        };
      default:
        return {
          title: '甄选果蔬，发现美好',
          description: '发现各种优质产品',
          breadcrumb: '产品商城'
        };
    }
  };

  const pageInfo = getPageInfo();

  if (loading) {
    return (
      <div className="product-list-page">
        <div className="loading">🍎 加载产品数据中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="product-list-page">
        <div className="error">❌ 加载失败: {error}</div>
        <button 
          onClick={() => fetchProducts(selectedFilter)} 
          className="retry-button"
        >
          重试
        </button>
      </div>
    );
  }

  return (
    <div className="product-list-page">
      {/* 面包屑导航 */}
      <nav className="breadcrumb">
        <Link to="/">首页</Link>
        <span> / </span>
        <Link to="/products">产品商城</Link>
        {selectedFilter !== 'all' && (
          <>
            <span> / </span>
            <span>{pageInfo.breadcrumb}</span>
          </>
        )}
      </nav>

      <div className="product-list-content">
        {/* 左侧内容区域 */}
        <div className="products-main">
          {/* 产品列表 */}
          <FruitList 
            fruits={products}
            title={pageInfo.title}
            description={pageInfo.description}
          />
        </div>

        {/* 右侧筛选菜单 */}
        <div className="filter-sidebar">
          <div className="filter-card">
            <h3 className="filter-title">产品筛选</h3>
            
            <div className="filter-group">
              <label className="filter-label">产品分类</label>
              <select 
                value={selectedFilter}
                onChange={(e) => handleFilterChange(e.target.value)}
                className="filter-select"
              >
                <option value="all">全部产品</option>
                <optgroup label="水果分类">
                  <option value="fruit">所有水果</option>
                  <option value="apple">苹果专区</option>
                  <option value="kiwi">猕猴桃专区</option>
                </optgroup>
                <optgroup label="蔬菜分类">
                  <option value="vegetable">所有蔬菜</option>
                </optgroup>
              </select>
            </div>

            {/* 快速导航链接 */}
            <div className="quick-links">
              <h4>快速导航</h4>
              <Link to="/fruits/apples" className="quick-link">
                🍎 独立苹果页面
              </Link>
              <Link to="/fruits/kiwis" className="quick-link">
                🥝 独立猕猴桃页面
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductList;