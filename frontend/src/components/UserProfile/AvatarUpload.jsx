import React from 'react';
import './AvatarUpload.css';

const AvatarUpload = ({ 
  avatarUrl, 
  onAvatarUpload, 
  getAvatarUrl 
}) => {
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && onAvatarUpload) {
      onAvatarUpload(file);
    }
  };

  return (
    <div className="avatar-section">
      <div className="avatar-upload">
        <img 
          src={getAvatarUrl ? getAvatarUrl(avatarUrl) : avatarUrl} 
          alt="头像" 
          className="avatar"
          onError={(e) => {
            e.target.src = '/images/default-avatar.png';
          }}
        />
        <div className="avatar-actions">
          <input
            type="file"
            id="avatar-upload"
            accept="image/*"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />
          <label htmlFor="avatar-upload" className="upload-btn">
            更换头像
          </label>
        </div>
      </div>
    </div>
  );
};

export default AvatarUpload;