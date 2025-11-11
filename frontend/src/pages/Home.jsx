import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import axios from 'axios';

const Home = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [filteredSuggestions, setFilteredSuggestions] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { loginWithToken, user } = useAuth();

  // 处理登录重定向
  useEffect(() => {
    const token = searchParams.get('token');
    const userId = searchParams.get('userId');
    const userName = searchParams.get('userName');
    const loginSuccess = searchParams.get('loginSuccess');
    
    if (loginSuccess === 'true' && token && userId) {
      loginWithToken(token, userId, userName);
      const cleanUrl = window.location.pathname;
      window.history.replaceState({}, '', cleanUrl);
      console.log('登录成功，欢迎 ' + userName);
    }
  }, [searchParams, loginWithToken]);

  // 处理搜索输入变化
  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    
    if (value.trim()) {
      setShowSuggestions(true);
      // 简单的本地建议过滤
      const suggestions = products
        .filter(product => 
          product.name.toLowerCase().includes(value.toLowerCase()) ||
          product.description.toLowerCase().includes(value.toLowerCase())
        )
        .map(product => product.name)
        .slice(0, 5);
      setFilteredSuggestions(suggestions);
    } else {
      setShowSuggestions(false);
      setFilteredSuggestions([]);
    }
  };

  // 执行搜索
  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;

    setIsSearching(true);
    setShowSuggestions(false);

    try {
      // ✅ 修改：跳转到 /search 而不是 /products
      navigate(`/search?search=${encodeURIComponent(searchTerm.trim())}`);
    } catch (error) {
      console.error('搜索跳转失败:', error);
    } finally {
      setIsSearching(false);
    }
  };

  // 处理建议项点击
  const handleSuggestionClick = (suggestion) => {
    setSearchTerm(suggestion);
    setShowSuggestions(false);
    // ✅ 修改：跳转到 /search 而不是 /products
    navigate(`/search?search=${encodeURIComponent(suggestion)}`);
  };

  // 处理键盘事件
  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSearch(e);
    } else if (e.key === 'Escape') {
      setShowSuggestions(false);
    }
  };

  // 使用 CloudFront 域名替换 S3 直接链接
  const CLOUDFRONT_DOMAIN = 'https://d3sx9glhrpxv9q.cloudfront.net';

  const products = [
    // 水果
    {
      id: 1,
      name: '秦岭猕猴桃',
      englishName: 'kiwi',
      description: '秀色可餐，果香浓郁',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/kiwi.png`
    },
    {
      id: 2,
      name: '栖霞红富士苹果',
      englishName: 'apple',
      description: '红艳诱人，脆爽多汁',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/apples.jpg`
    },
    {
      id: 3,
      name: '长安石榴',
      englishName: 'pomegranate',
      description: '晶莹剔透，粉黛抹腮',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/shiliu.png`
    },
    {
      id: 4,
      name: '鄠邑葡萄',
      englishName: 'grape',
      description: '甜而不腻，皮薄肉厚',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/grape.png`
    },
    {
      id: 5,
      name: '延川红枣',
      englishName: 'red-date',
      description: '口感脆甜，滋生养颜',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/hongzao.png`
    },
    {
      id: 6,
      name: '城固柑橘',
      englishName: 'orange',
      description: '皮薄易剥，汁多化渣',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/oranges.jpg`
    },
    {
      id: 7,
      name: '大荔西瓜',
      englishName: 'watermelon',
      description: '绿裳红心玉为魂，清甜如许胜琼浆',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/watermelon.png`
    },
    {
      id: 8,
      name: '周至草莓',
      englishName: 'strawberry',
      description: '味觉之舞，意境之妙',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/strawberries.jpg`
    },
    {
      id: 9,
      name: '焦镇李子',
      englishName: 'plum',
      description: '翡翠耀眼，童年往昔',
      category: 'fruits',
      image: `${CLOUDFRONT_DOMAIN}/plums.jpg`
    },
    // 蔬菜
    {
      id: 10,
      name: '眉县番茄',
      englishName: 'tomato',
      description: '色彩之韵，生长之诗',
      category: 'vegetables',
      image: `${CLOUDFRONT_DOMAIN}/tomatoes.jpg`
    },
    {
      id: 11,
      name: '司竹辣椒',
      englishName: 'chilli-pepper',
      description: '酣畅淋漓，辣味十足',
      category: 'vegetables',
      image: `${CLOUDFRONT_DOMAIN}/chilli-pepper.jpg`
    },
    {
      id: 12,
      name: '尚村土豆',
      englishName: 'potato',
      description: '朴实无华，不可或缺',
      category: 'vegetables',
      image: `${CLOUDFRONT_DOMAIN}/potatoes.jpg`
    }
  ];

  return (
    <div style={containerStyle}>
      <section style={heroStyle}>
        <h1 style={heroTitleStyle}>欢迎来到普罗米修甄选果蔬平台</h1>
        
        {/* 搜索框 */}
        <div style={searchContainerStyle}>
          <form onSubmit={handleSearch} style={searchFormStyle}>
            <div style={searchInputWrapperStyle}>
              <input
                type="text"
                placeholder="搜索水果或蔬菜..."
                value={searchTerm}
                onChange={handleSearchChange}
                onKeyDown={handleKeyDown}
                onFocus={() => searchTerm.trim() && setShowSuggestions(true)}
                onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
                style={searchInputStyle}
                disabled={isSearching}
              />
              <button 
                type="submit" 
                style={{
                  ...searchButtonStyle,
                  ...(isSearching ? searchButtonDisabledStyle : {})
                }}
                disabled={isSearching}
              >
                {isSearching ? '搜索中...' : '搜索'}
              </button>
            </div>
            
            {/* 搜索建议下拉框 */}
            {showSuggestions && filteredSuggestions.length > 0 && (
              <div style={suggestionsStyle}>
                {filteredSuggestions.map((suggestion, index) => (
                  <div
                    key={index}
                    style={suggestionItemStyle}
                    onMouseDown={() => handleSuggestionClick(suggestion)}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor = '#f5f5f5';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = 'white';
                    }}
                  >
                    <span style={suggestionTextStyle}>{suggestion}</span>
                  </div>
                ))}
              </div>
            )}
          </form>
          
          {/* Elasticsearch 标识 */}
          <div style={searchInfoStyle}>
            <span style={searchBadgeStyle}>
              🔍 Elasticsearch 智能搜索
            </span>
          </div>
        </div>
      </section>

      {/* 水果分类 */}
      <section style={categoriesStyle}>
        <h2 style={sectionTitleStyle}>时令水果</h2>
        <div style={categoriesGridStyle}>
          {products.filter(product => product.category === 'fruits').map(product => (
            <Link 
              key={product.id}
              to={
                product.englishName === 'apple' 
                  ? '/fruits/apples'
                  : product.englishName === 'kiwi'
                  ? '/fruits/kiwis'
                  : `/fruit/${product.englishName}`
              }
              style={categoryCardStyle}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-8px) scale(1.03)';
                e.currentTarget.style.boxShadow = '0 15px 35px rgba(255, 87, 34, 0.25)';
                e.currentTarget.style.border = '2px solid #ff5722';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0) scale(1)';
                e.currentTarget.style.boxShadow = '0 4px 15px rgba(0, 0, 0, 0.1)';
                e.currentTarget.style.border = '2px solid transparent';
              }}
            >
              <div style={imageContainerStyle}>
                <img 
                  src={product.image} 
                  alt={product.name}
                  style={imageStyle}
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              </div>
              <div style={textContainerStyle}>
                <h3 style={productNameStyle}>{product.name}</h3>
                <p style={productDescriptionStyle}>{product.description}</p>
              </div>
            </Link>
          ))}
        </div>
      </section>

      {/* 蔬菜分类 */}
      <section style={categoriesStyle}>
        <h2 style={sectionTitleStyle}>新鲜蔬菜</h2>
        <div style={categoriesGridStyle}>
          {products.filter(product => product.category === 'vegetables').map(product => (
            <Link 
              key={product.id}
              to={`/vegetable/${product.englishName}`}
              style={categoryCardStyle}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-8px) scale(1.03)';
                e.currentTarget.style.boxShadow = '0 15px 35px rgba(76, 175, 80, 0.25)';
                e.currentTarget.style.border = '2px solid #4caf50';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0) scale(1)';
                e.currentTarget.style.boxShadow = '0 4px 15px rgba(0, 0, 0, 0.1)';
                e.currentTarget.style.border = '2px solid transparent';
              }}
            >
              <div style={imageContainerStyle}>
                <img 
                  src={product.image} 
                  alt={product.name}
                  style={imageStyle}
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              </div>
              <div style={textContainerStyle}>
                <h3 style={productNameStyle}>{product.name}</h3>
                <p style={productDescriptionStyle}>{product.description}</p>
              </div>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
};

// 样式部分保持不变...
const containerStyle = {
  minHeight: 'calc(100vh - 200px)',
};

const heroStyle = {
  background: 'linear-gradient(135deg, #ff5722 0%, #ff8a65 100%)',
  color: 'white',
  padding: '1.5rem 1rem',
  textAlign: 'center',
  minHeight: '30vh',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  position: 'relative',
  overflow: 'hidden',
};

const heroTitleStyle = {
  fontSize: '2rem',
  marginBottom: '1.5rem',
  fontWeight: 'bold',
  textShadow: '2px 2px 4px rgba(0,0,0,0.3)',
  position: 'relative',
  zIndex: 2,
};

// 搜索框样式
const searchContainerStyle = {
  width: '100%',
  maxWidth: '500px',
  margin: '0 auto',
  position: 'relative',
  zIndex: 2,
};

const searchFormStyle = {
  position: 'relative',
  background: 'rgba(255, 255, 255, 0.95)',
  borderRadius: '50px',
  overflow: 'hidden',
  boxShadow: '0 8px 25px rgba(0, 0, 0, 0.2)',
  transition: 'all 0.3s ease',
  backdropFilter: 'blur(10px)',
};

const searchInputWrapperStyle = {
  display: 'flex',
  position: 'relative',
  zIndex: 3,
};

const searchInputStyle = {
  flex: 1,
  border: 'none',
  outline: 'none',
  padding: '0.8rem 1.2rem',
  fontSize: '0.9rem',
  color: '#333',
  background: 'transparent',
};

const searchButtonStyle = {
  background: 'linear-gradient(135deg, #e64a19, #ff5722)',
  color: 'white',
  border: 'none',
  padding: '0.8rem 1.5rem',
  fontSize: '0.9rem',
  fontWeight: 'bold',
  cursor: 'pointer',
  transition: 'all 0.3s ease',
  minWidth: '80px',
};

const searchButtonDisabledStyle = {
  opacity: 0.7,
  cursor: 'not-allowed',
};

// 搜索建议样式
const suggestionsStyle = {
  position: 'absolute',
  top: '100%',
  left: 0,
  right: 0,
  backgroundColor: 'white',
  border: '1px solid #e0e0e0',
  borderRadius: '0 0 15px 15px',
  boxShadow: '0 4px 15px rgba(0, 0, 0, 0.1)',
  zIndex: 10,
  maxHeight: '200px',
  overflowY: 'auto',
};

const suggestionItemStyle = {
  padding: '0.8rem 1.2rem',
  cursor: 'pointer',
  borderBottom: '1px solid #f5f5f5',
  transition: 'background-color 0.2s ease',
};

const suggestionTextStyle = {
  color: '#333',
  fontSize: '0.9rem',
};

const searchInfoStyle = {
  marginTop: '0.5rem',
  textAlign: 'center',
};

const searchBadgeStyle = {
  display: 'inline-block',
  backgroundColor: 'rgba(255, 255, 255, 0.2)',
  color: 'white',
  padding: '0.3rem 0.8rem',
  borderRadius: '15px',
  fontSize: '0.8rem',
  fontWeight: 'bold',
  backdropFilter: 'blur(10px)',
};

const categoriesStyle = {
  padding: '2rem 1rem',
  backgroundColor: '#f8f9fa',
  minHeight: '45vh',
  marginTop: '-1rem',
};

const sectionTitleStyle = {
  textAlign: 'center',
  fontSize: '2.2rem',
  marginBottom: '2rem',
  color: '#ff5722',
  fontWeight: 'bold',
  background: 'linear-gradient(135deg, #ff5722, #e64a19)',
  WebkitBackgroundClip: 'text',
  WebkitTextFillColor: 'transparent',
};

const categoriesGridStyle = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
  gap: '2rem',
  maxWidth: '1200px',
  margin: '0 auto',
};

const categoryCardStyle = {
  backgroundColor: 'white',
  borderRadius: '15px',
  textDecoration: 'none',
  color: '#333',
  boxShadow: '0 4px 15px rgba(0, 0, 0, 0.1)',
  transition: 'all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
  border: '2px solid transparent',
  position: 'relative',
  overflow: 'hidden',
  display: 'flex',
  flexDirection: 'column',
  cursor: 'pointer',
  height: '400px',
};

const imageContainerStyle = {
  width: '100%',
  height: '250px',
  overflow: 'hidden',
  position: 'relative',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  backgroundColor: 'white',
  borderRadius: '13px 13px 0 0',
};

const imageStyle = {
  width: '100%',
  height: '100%',
  objectFit: 'contain',
  display: 'block',
  padding: '10px',
  backgroundColor: 'white',
  transition: 'transform 0.3s ease',
};

const textContainerStyle = {
  padding: '1.5rem',
  textAlign: 'center',
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  backgroundColor: 'white',
  minHeight: '150px',
};

const productNameStyle = {
  fontSize: '1.3rem',
  fontWeight: 'bold',
  margin: '0 0 0.5rem 0',
  color: '#333',
};

const productDescriptionStyle = {
  fontSize: '0.9rem',
  color: '#666',
  margin: '0',
  lineHeight: '1.4',
  minHeight: '40px',
};

export default Home;