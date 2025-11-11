import React from 'react'
import './FruitFilter.css'

const FruitFilter = ({ 
  categories = [], 
  selectedCategory, 
  onCategoryChange,
  sortBy,
  onSortChange,
  priceRange,
  onPriceRangeChange
}) => {
  return (
    <div className="fruit-filter">
      <h3>筛选条件</h3>
      
      {/* 分类筛选 */}
      <div className="filter-section">
        <label>水果分类</label>
        <select 
          value={selectedCategory} 
          onChange={(e) => onCategoryChange(e.target.value)}
        >
          <option value="">全部水果</option>
          {categories.map(category => (
            <option key={category.value} value={category.value}>
              {category.label}
            </option>
          ))}
        </select>
      </div>

      {/* 排序 */}
      <div className="filter-section">
        <label>排序方式</label>
        <select 
          value={sortBy} 
          onChange={(e) => onSortChange(e.target.value)}
        >
          <option value="featured">推荐排序</option>
          <option value="price_asc">价格从低到高</option>
          <option value="price_desc">价格从高到低</option>
          <option value="sweetness">甜度最高</option>
          <option value="newest">最新上架</option>
        </select>
      </div>

      {/* 价格范围 */}
      <div className="filter-section">
        <label>价格范围</label>
        <select 
          value={priceRange} 
          onChange={(e) => onPriceRangeChange(e.target.value)}
        >
          <option value="">全部价格</option>
          <option value="0-20">¥0 - ¥20</option>
          <option value="20-50">¥20 - ¥50</option>
          <option value="50-100">¥50 - ¥100</option>
          <option value="100+">¥100以上</option>
        </select>
      </div>
    </div>
  )
}

export default FruitFilter