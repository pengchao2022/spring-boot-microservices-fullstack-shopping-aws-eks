// src/pages/admin/AppleManagement.jsx
import React, { useState, useEffect } from 'react';
import './AppleManagement.css';

const AppleManagement = () => {
  const [apples, setApples] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingApple, setEditingApple] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newApple, setNewApple] = useState({
    name: '',
    englishName: '',
    description: '',
    shortDescription: '',
    basePrice: '',
    originalPrice: '',
    weightUnit: '500g',
    mainImageUrl: '',
    origin: '',
    isFeatured: false,
    sortOrder: 0,
    sweetnessLevel: 3,
    crunchinessLevel: 3,
    appleVariety: '',
    harvestSeason: ''
  });

  useEffect(() => {
    fetchApples();
  }, []);

  const fetchApples = async () => {
    try {
      const response = await fetch('/api/products/admin/apple-category');
      if (!response.ok) throw new Error('获取数据失败');
      const result = await response.json();
      if (result.success) {
        setApples(result.data);
      }
    } catch (error) {
      console.error('Error:', error);
      alert('获取苹果数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (id, field, value) => {
    try {
      const response = await fetch(`/api/products/admin/apple-category/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ [field]: value }),
      });
      
      if (!response.ok) throw new Error('更新失败');
      const result = await response.json();
      if (result.success) {
        alert('更新成功');
        fetchApples(); // 刷新数据
      }
    } catch (error) {
      console.error('Error:', error);
      alert('更新失败');
    }
  };

  const handleCreate = async () => {
    try {
      const response = await fetch('/api/products/admin/apple-category', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newApple),
      });
      
      if (!response.ok) throw new Error('创建失败');
      const result = await response.json();
      if (result.success) {
        alert('创建成功');
        setShowCreateForm(false);
        setNewApple({
          name: '',
          englishName: '',
          description: '',
          shortDescription: '',
          basePrice: '',
          originalPrice: '',
          weightUnit: '500g',
          mainImageUrl: '',
          origin: '',
          isFeatured: false,
          sortOrder: 0,
          sweetnessLevel: 3,
          crunchinessLevel: 3,
          appleVariety: '',
          harvestSeason: ''
        });
        fetchApples(); // 刷新数据
      }
    } catch (error) {
      console.error('Error:', error);
      alert('创建失败');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('确定要删除这个产品吗？')) return;
    
    try {
      const response = await fetch(`/api/products/admin/apple-category/${id}`, {
        method: 'DELETE',
      });
      
      if (!response.ok) throw new Error('删除失败');
      const result = await response.json();
      if (result.success) {
        alert('删除成功');
        fetchApples(); // 刷新数据
      }
    } catch (error) {
      console.error('Error:', error);
      alert('删除失败');
    }
  };

  if (loading) {
    return <div className="admin-loading">加载中...</div>;
  }

  return (
    <div className="apple-management">
      <div className="admin-header">
        <h1>🍎 苹果产品管理</h1>
        <button 
          className="btn-create"
          onClick={() => setShowCreateForm(true)}
        >
          添加新产品
        </button>
      </div>

      {/* 创建产品表单 */}
      {showCreateForm && (
        <div className="create-form-overlay">
          <div className="create-form">
            <h2>创建新产品</h2>
            <div className="form-grid">
              <input
                type="text"
                placeholder="产品名称"
                value={newApple.name}
                onChange={(e) => setNewApple({...newApple, name: e.target.value})}
              />
              <input
                type="text"
                placeholder="英文名称"
                value={newApple.englishName}
                onChange={(e) => setNewApple({...newApple, englishName: e.target.value})}
              />
              <input
                type="number"
                placeholder="价格"
                value={newApple.basePrice}
                onChange={(e) => setNewApple({...newApple, basePrice: e.target.value})}
              />
              <input
                type="text"
                placeholder="图片URL"
                value={newApple.mainImageUrl}
                onChange={(e) => setNewApple({...newApple, mainImageUrl: e.target.value})}
              />
              <textarea
                placeholder="简短描述"
                value={newApple.shortDescription}
                onChange={(e) => setNewApple({...newApple, shortDescription: e.target.value})}
              />
              <textarea
                placeholder="详细描述"
                value={newApple.description}
                onChange={(e) => setNewApple({...newApple, description: e.target.value})}
              />
            </div>
            <div className="form-actions">
              <button className="btn-save" onClick={handleCreate}>保存</button>
              <button className="btn-cancel" onClick={() => setShowCreateForm(false)}>取消</button>
            </div>
          </div>
        </div>
      )}

      {/* 产品列表 */}
      <div className="apple-list">
        {apples.map(apple => (
          <div key={apple.id} className="apple-item">
            <div className="apple-image">
              <img src={apple.mainImageUrl} alt={apple.name} />
            </div>
            
            <div className="apple-details">
              <div className="field-group">
                <label>名称:</label>
                <input
                  type="text"
                  value={apple.name}
                  onChange={(e) => handleUpdate(apple.id, 'name', e.target.value)}
                />
              </div>
              
              <div className="field-group">
                <label>价格:</label>
                <input
                  type="number"
                  value={apple.basePrice}
                  onChange={(e) => handleUpdate(apple.id, 'basePrice', parseFloat(e.target.value))}
                />
              </div>
              
              <div className="field-group">
                <label>重量:</label>
                <input
                  type="text"
                  value={apple.weightUnit}
                  onChange={(e) => handleUpdate(apple.id, 'weightUnit', e.target.value)}
                />
              </div>
              
              <div className="field-group">
                <label>甜度:</label>
                <select
                  value={apple.sweetnessLevel || 3}
                  onChange={(e) => handleUpdate(apple.id, 'sweetnessLevel', parseInt(e.target.value))}
                >
                  {[1, 2, 3, 4, 5].map(level => (
                    <option key={level} value={level}>{'⭐'.repeat(level)}</option>
                  ))}
                </select>
              </div>
              
              <div className="field-group">
                <label>脆度:</label>
                <select
                  value={apple.crunchinessLevel || 3}
                  onChange={(e) => handleUpdate(apple.id, 'crunchinessLevel', parseInt(e.target.value))}
                >
                  {[1, 2, 3, 4, 5].map(level => (
                    <option key={level} value={level}>{'⭐'.repeat(level)}</option>
                  ))}
                </select>
              </div>
              
              <div className="field-group full-width">
                <label>描述:</label>
                <textarea
                  value={apple.shortDescription}
                  onChange={(e) => handleUpdate(apple.id, 'shortDescription', e.target.value)}
                />
              </div>
            </div>
            
            <div className="apple-actions">
              <button 
                className={`btn-featured ${apple.isFeatured ? 'active' : ''}`}
                onClick={() => handleUpdate(apple.id, 'isFeatured', !apple.isFeatured)}
              >
                {apple.isFeatured ? '取消推荐' : '设为推荐'}
              </button>
              <button 
                className="btn-delete"
                onClick={() => handleDelete(apple.id)}
              >
                删除
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AppleManagement;