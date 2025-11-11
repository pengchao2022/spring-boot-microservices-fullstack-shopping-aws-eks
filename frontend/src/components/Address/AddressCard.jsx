import React from 'react';
import './AddressCard.css';

const AddressCard = ({ 
  address, 
  onSetDefault, 
  onEdit, 
  onDelete,
  showActions = true 
}) => {
  const {
    id,
    recipient,
    phone,
    province,
    city,
    district,
    detail,
    isDefault
  } = address;

  const handleSetDefault = () => {
    if (onSetDefault && !isDefault) {
      onSetDefault(id);
    }
  };

  const handleEdit = () => {
    if (onEdit) {
      onEdit(address);
    }
  };

  const handleDelete = () => {
    if (onDelete) {
      onDelete(id);
    }
  };

  return (
    <div className={`address-card ${isDefault ? 'default' : ''}`}>
      <div className="address-info">
        <div className="address-header">
          <span className="recipient">{recipient}</span>
          <span className="phone">{phone}</span>
          {isDefault && <span className="default-badge">默认</span>}
        </div>
        <div className="address-detail">
          {province} {city} {district} {detail}
        </div>
      </div>
      
      {showActions && (
        <div className="address-actions">
          <button 
            className="btn-link"
            onClick={handleSetDefault}
            disabled={isDefault}
          >
            设为默认
          </button>
          <button 
            className="btn-link"
            onClick={handleEdit}
          >
            编辑
          </button>
          <button 
            className="btn-link text-danger"
            onClick={handleDelete}
          >
            删除
          </button>
        </div>
      )}
    </div>
  );
};

export default AddressCard;