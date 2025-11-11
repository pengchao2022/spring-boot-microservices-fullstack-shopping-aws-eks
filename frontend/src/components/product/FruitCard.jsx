import React from 'react'
import { Link } from 'react-router-dom'
import './FruitCard.css'

const FruitCard = ({ fruit }) => {
  // CDN 域名
  const CLOUDFRONT_DOMAIN = 'https://d3sx9glhrpxv9q.cloudfront.net'

  // 处理图片URL
  const processImageUrl = (imageUrl) => {
    if (imageUrl && imageUrl.includes('s3.us-east-1.amazonaws.com')) {
      const s3Path = imageUrl.split('.com/')[1]
      return `${CLOUDFRONT_DOMAIN}/${s3Path}`
    }
    return imageUrl || 'https://d3sx9glhrpxv9q.cloudfront.net/fruit-placeholder.jpg'
  }

  // 获取价格信息
  const getPriceInfo = () => {
    return {
      currentPrice: fruit.basePrice || '0.00',
      weight: fruit.weightUnit || '500g'
    }
  }

  const priceInfo = getPriceInfo()
  const mainImageUrl = processImageUrl(fruit.mainImageUrl)
  const description = fruit.shortDescription || fruit.description || '优质水果产品'

  return (
    <div className="fruit-card">
      {/* 图片部分 */}
      <Link to={`/fruit/${fruit.englishName}`} className="fruit-link">
        <div className="fruit-image">
          <img 
            src={mainImageUrl} 
            alt={fruit.name}
            className="fruit-img"
            onError={(e) => {
              e.target.src = 'https://d3sx9glhrpxv9q.cloudfront.net/fruit-placeholder.jpg'
            }}
          />
        </div>
      </Link>
      
      {/* 文字信息部分 */}
      <div className="fruit-info">
        <h3 className="fruit-name">{fruit.name}</h3>
        <p className="fruit-description">{description}</p>
        
        {/* 水果特色信息 */}
        {(fruit.sweetnessLevel || fruit.acidityLevel) && (
          <div className="fruit-attributes">
            {fruit.sweetnessLevel && (
              <span className="attribute">甜度: {'⭐'.repeat(fruit.sweetnessLevel)}</span>
            )}
            {fruit.acidityLevel && (
              <span className="attribute">酸度: {'🍋'.repeat(fruit.acidityLevel)}</span>
            )}
          </div>
        )}
        
        {/* 品种信息 */}
        {fruit.kiwiVariety && (
          <div className="fruit-variety">
            <span className="variety-label">品种:</span>
            <span className="variety-value">{fruit.kiwiVariety}</span>
          </div>
        )}
        
        {/* 苹果品种 */}
        {fruit.appleVariety && (
          <div className="fruit-variety">
            <span className="variety-label">品种:</span>
            <span className="variety-value">{fruit.appleVariety}</span>
          </div>
        )}
        
        {/* 产地信息 */}
        {fruit.origin && (
          <div className="fruit-origin">
            <span className="origin-label">产地:</span>
            <span className="origin-value">{fruit.origin}</span>
          </div>
        )}
        
        {/* 收获季节 */}
        {fruit.harvestSeason && (
          <div className="fruit-season">
            <span className="season-label">收获季:</span>
            <span className="season-value">{fruit.harvestSeason}</span>
          </div>
        )}
        
        {/* 维生素C含量（猕猴桃专用） */}
        {fruit.vitaminCContent && (
          <div className="fruit-vitamin">
            <span className="vitamin-label">维生素C:</span>
            <span className="vitamin-value">{fruit.vitaminCContent}</span>
          </div>
        )}
        
        {/* 价格信息 */}
        <div className="fruit-price-section">
          <div className="fruit-price">
            <span className="current-price">¥{priceInfo.currentPrice}</span>
            <span className="weight">/{priceInfo.weight}</span>
          </div>
        </div>

        {/* 查看详情按钮 */}
        <Link to={`/fruit/${fruit.englishName}`} className="view-details-link">
          <button className="view-details-btn">
            查看详情
          </button>
        </Link>
      </div>
    </div>
  )
}

export default FruitCard