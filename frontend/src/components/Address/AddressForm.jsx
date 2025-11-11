import React, { useState, useEffect } from 'react';
import { getProvinces, getCities, getAreas } from '../../utils/regionUtils';
import './AddressForm.css';

const AddressForm = ({ 
  initialData = {}, 
  onSubmit, 
  onCancel, 
  submitButtonText = '保存地址',
  cancelButtonText = '取消',
  title = '添加新地址'
}) => {
  const [provinces, setProvinces] = useState([]);
  const [cities, setCities] = useState([]);
  const [areas, setAreas] = useState([]);
  
  const [formData, setFormData] = useState({
    recipient: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false,
    ...initialData
  });

  const [errors, setErrors] = useState({});

  // 初始化省份数据
  useEffect(() => {
    setProvinces(getProvinces());
  }, []);

  // 当省份改变时更新城市列表
  useEffect(() => {
    if (formData.province) {
      setCities(getCities(formData.province));
      setFormData(prev => ({ ...prev, city: '', district: '' }));
    } else {
      setCities([]);
      setAreas([]);
    }
  }, [formData.province]);

  // 当城市改变时更新区县列表
  useEffect(() => {
    if (formData.city) {
      setAreas(getAreas(formData.city));
      setFormData(prev => ({ ...prev, district: '' }));
    } else {
      setAreas([]);
    }
  }, [formData.city]);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    
    // 清除对应字段的错误
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.recipient.trim()) {
      newErrors.recipient = '请输入收货人姓名';
    }
    
    if (!formData.phone.trim()) {
      newErrors.phone = '请输入手机号';
    } else if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
      newErrors.phone = '请输入有效的手机号';
    }
    
    if (!formData.province) {
      newErrors.province = '请选择省份';
    }
    
    if (!formData.city) {
      newErrors.city = '请选择城市';
    }
    
    if (!formData.district) {
      newErrors.district = '请选择区县';
    }
    
    if (!formData.detail.trim()) {
      newErrors.detail = '请输入详细地址';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  const handleCancel = () => {
    onCancel();
  };

  return (
    <div className="address-form">
      <h3>{title}</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="recipient">收货人 *</label>
            <input
              type="text"
              id="recipient"
              name="recipient"
              value={formData.recipient}
              onChange={handleInputChange}
              placeholder="请输入收货人姓名"
              className={errors.recipient ? 'error' : ''}
            />
            {errors.recipient && <span className="error-message">{errors.recipient}</span>}
          </div>
          
          <div className="form-group">
            <label htmlFor="phone">手机号 *</label>
            <input
              type="tel"
              id="phone"
              name="phone"
              value={formData.phone}
              onChange={handleInputChange}
              placeholder="请输入手机号"
              className={errors.phone ? 'error' : ''}
            />
            {errors.phone && <span className="error-message">{errors.phone}</span>}
          </div>
        </div>
        
        {/* 省市区三级联动 */}
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="province">省份 *</label>
            <select
              id="province"
              name="province"
              value={formData.province}
              onChange={handleInputChange}
              className={errors.province ? 'error' : ''}
            >
              <option value="">请选择省份</option>
              {provinces.map(prov => (
                <option key={prov.value} value={prov.value}>
                  {prov.label}
                </option>
              ))}
            </select>
            {errors.province && <span className="error-message">{errors.province}</span>}
          </div>
          
          <div className="form-group">
            <label htmlFor="city">城市 *</label>
            <select
              id="city"
              name="city"
              value={formData.city}
              onChange={handleInputChange}
              disabled={!formData.province}
              className={errors.city ? 'error' : ''}
            >
              <option value="">请选择城市</option>
              {cities.map(city => (
                <option key={city.value} value={city.value}>
                  {city.label}
                </option>
              ))}
            </select>
            {errors.city && <span className="error-message">{errors.city}</span>}
          </div>
          
          <div className="form-group">
            <label htmlFor="district">区县 *</label>
            <select
              id="district"
              name="district"
              value={formData.district}
              onChange={handleInputChange}
              disabled={!formData.city}
              className={errors.district ? 'error' : ''}
            >
              <option value="">请选择区县</option>
              {areas.map(area => (
                <option key={area.value} value={area.value}>
                  {area.label}
                </option>
              ))}
            </select>
            {errors.district && <span className="error-message">{errors.district}</span>}
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="detail">详细地址 *</label>
          <input
            type="text"
            id="detail"
            name="detail"
            value={formData.detail}
            onChange={handleInputChange}
            placeholder="请输入详细地址（街道、门牌号等）"
            className={errors.detail ? 'error' : ''}
          />
          {errors.detail && <span className="error-message">{errors.detail}</span>}
        </div>

        <div className="form-checkbox">
          <label>
            <input
              type="checkbox"
              name="isDefault"
              checked={formData.isDefault}
              onChange={handleInputChange}
            />
            设为默认地址
          </label>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn-primary">
            {submitButtonText}
          </button>
          <button type="button" className="btn-secondary" onClick={handleCancel}>
            {cancelButtonText}
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddressForm;