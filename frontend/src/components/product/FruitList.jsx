import React from 'react'
import FruitCard from './FruitCard'
import './FruitList.css'

const FruitList = ({ fruits, title, description }) => {
  if (!fruits || fruits.length === 0) {
    return (
      <div className="no-fruits">
        <h3>暂无水果产品</h3>
        <p>请稍后再试或联系客服</p>
      </div>
    )
  }

  return (
    <div className="fruit-list-page">
      {(title || description) && (
        <div className="page-header">
          {title && <h1>{title}</h1>}
          {description && <p>{description}</p>}
        </div>
      )}
      
      <div className="fruits-grid">
        {fruits.map(fruit => (
          <FruitCard key={fruit.id} fruit={fruit} />
        ))}
      </div>
    </div>
  )
}

export default FruitList