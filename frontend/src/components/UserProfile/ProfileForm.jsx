import React from 'react';
import './ProfileForm.css';

const ProfileForm = ({ 
  user, 
  formData, 
  isEditing, 
  onInputChange, 
  onSave, 
  onCancelEdit, 
  onStartEdit 
}) => {
  return (
    <div className="profile-form-container">
      <h2>个人信息</h2>
      <div className="profile-card">
        <div className="profile-form">
          <div className="form-group">
            <label>用户名</label>
            {isEditing ? (
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={onInputChange}
                placeholder="请输入用户名"
              />
            ) : (
              <div className="field-value">{formData.name || '未设置'}</div>
            )}
          </div>

          <div className="form-group">
            <label>手机号</label>
            {isEditing ? (
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={onInputChange}
                placeholder="请输入手机号"
              />
            ) : (
              <div className="field-value">{formData.phone || '未设置'}</div>
            )}
          </div>

          <div className="form-group">
            <label>邮箱</label>
            {isEditing ? (
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={onInputChange}
                placeholder="请输入邮箱"
              />
            ) : (
              <div className="field-value">
                {formData.email || '未设置'}
              </div>
            )}
          </div>

          <div className="form-actions">
            {isEditing ? (
              <>
                <button className="btn-primary" onClick={onSave}>
                  保存
                </button>
                <button 
                  className="btn-secondary" 
                  onClick={onCancelEdit}
                >
                  取消
                </button>
              </>
            ) : (
              <button 
                className="btn-primary" 
                onClick={onStartEdit}
              >
                编辑信息
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileForm;