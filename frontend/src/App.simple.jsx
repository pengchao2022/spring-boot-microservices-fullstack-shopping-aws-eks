import React from 'react'

function SimpleApp() {
  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h1>测试应用 - 基础功能</h1>
      <p>如果这个能显示，说明 React 基础正常</p>
      <p>当前时间: {new Date().toLocaleString()}</p>
    </div>
  )
}

export default SimpleApp