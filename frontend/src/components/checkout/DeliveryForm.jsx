import React, { useState, useEffect } from 'react';
import { getProvinces, getCities, getAreas } from '../../utils/regionUtils';
import './DeliveryForm.css';

const DeliveryForm = ({ cartData, onDeliverySubmit, loading }) => {
  const [formData, setFormData] = useState({
    shippingName: '',
    shippingPhone: '',
    selectedProvince: '',
    selectedCity: '',
    selectedArea: '',
    shippingAddress: '', // 🔥 修改：将 shippingAddressLine1 改为 shippingAddress
    shippingMethod: 'standard',
    deliveryTime: 'anytime',
    deliveryNote: ''
  });

  const [provinces, setProvinces] = useState([]);
  const [cities, setCities] = useState([]);
  const [areas, setAreas] = useState([]);
  const [errors, setErrors] = useState({});

  // 初始化省份数据
  useEffect(() => {
    try {
      const provincesList = getProvinces() || [];
      console.log('✅ Loaded provinces:', provincesList.length);
      setProvinces(provincesList);
    } catch (error) {
      console.error('❌ Error loading provinces:', error);
      setProvinces([]);
    }
  }, []);

  // 当省份改变时更新城市列表
  useEffect(() => {
    if (formData.selectedProvince) {
      try {
        console.log('🔄 Getting cities for province:', formData.selectedProvince);
        const citiesList = getCities(formData.selectedProvince) || [];
        console.log('✅ Loaded cities:', citiesList);
        setCities(citiesList);
      } catch (error) {
        console.error('❌ Error loading cities:', error);
        setCities([]);
      }
      setFormData(prev => ({
        ...prev,
        selectedCity: '',
        selectedArea: ''
      }));
      setAreas([]);
    } else {
      setCities([]);
      setAreas([]);
    }
  }, [formData.selectedProvince]);

  // 当城市改变时更新区县列表
  useEffect(() => {
    if (formData.selectedCity) {
      try {
        console.log('🔄 Getting areas for city:', formData.selectedCity);
        const areasList = getAreas(formData.selectedCity) || [];
        console.log('✅ Loaded areas:', areasList);
        setAreas(areasList);
      } catch (error) {
        console.error('❌ Error loading areas:', error);
        setAreas([]);
      }
      setFormData(prev => ({
        ...prev,
        selectedArea: ''
      }));
    } else {
      setAreas([]);
    }
  }, [formData.selectedCity]);

  // 如果用户已登录，可以预填充一些信息
  useEffect(() => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        setFormData(prev => ({
          ...prev,
          shippingName: user.name || user.firstName || ''
        }));
      } catch (error) {
        console.error('Error parsing user info:', error);
      }
    }
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleProvinceChange = (e) => {
    const value = e.target.value;
    console.log('🌍 Province changed to:', value);
    setFormData(prev => ({
      ...prev,
      selectedProvince: value,
      selectedCity: '',
      selectedArea: ''
    }));
  };

  const handleCityChange = (e) => {
    const value = e.target.value;
    console.log('🏙️ City changed to:', value);
    setFormData(prev => ({
      ...prev,
      selectedCity: value,
      selectedArea: ''
    }));
  };

  const handleAreaChange = (e) => {
    const value = e.target.value;
    setFormData(prev => ({
      ...prev,
      selectedArea: value
    }));
  };

  const getSelectedProvinceName = () => {
    const provinceObj = provinces.find(p => p.value === formData.selectedProvince);
    return provinceObj ? provinceObj.label : '';
  };

  const getSelectedCityName = () => {
    const cityObj = cities.find(c => c.value === formData.selectedCity);
    return cityObj ? cityObj.label : '';
  };

  const getSelectedAreaName = () => {
    const areaObj = areas.find(a => a.value === formData.selectedArea);
    return areaObj ? areaObj.label : '';
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.shippingName.trim()) {
      newErrors.shippingName = '请输入姓名';
    }
    
    if (!formData.shippingPhone.trim()) {
      newErrors.shippingPhone = '请输入手机号码';
    } else if (!/^1[3-9]\d{9}$/.test(formData.shippingPhone)) {
      newErrors.shippingPhone = '请输入正确的手机号码';
    }
    
    if (!formData.selectedProvince) {
      newErrors.selectedProvince = '请选择省份';
    }
    
    if (!formData.selectedCity) {
      newErrors.selectedCity = '请选择城市';
    }
    
    if (!formData.selectedArea) {
      newErrors.selectedArea = '请选择区县';
    }

    if (!formData.shippingAddress.trim()) { // 🔥 修改：使用 shippingAddress
      newErrors.shippingAddress = '请输入详细地址';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validateForm()) {
      const completeFormData = {
        ...formData,
        shippingProvinceName: getSelectedProvinceName(),
        shippingCityName: getSelectedCityName(),
        shippingAreaName: getSelectedAreaName(),
        shippingState: formData.selectedProvince,
        shippingCity: formData.selectedCity,
        shippingArea: formData.selectedArea
      };
      console.log('📤 Submitting form data:', completeFormData);
      onDeliverySubmit(completeFormData);
    }
  };

  return (
    <div className="delivery-form">
      <h2>配送信息</h2>
      
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <h3>收货人信息</h3>
          <div className="form-row">
            <div className="form-group">
              <label>姓名 *</label>
              <input
                type="text"
                name="shippingName"
                value={formData.shippingName}
                onChange={handleInputChange}
                placeholder="请输入姓名"
                className={errors.shippingName ? 'error' : ''}
              />
              {errors.shippingName && <span className="error-message">{errors.shippingName}</span>}
            </div>
            
            <div className="form-group">
              <label>手机号码 *</label>
              <input
                type="tel"
                name="shippingPhone"
                value={formData.shippingPhone}
                onChange={handleInputChange}
                placeholder="请输入手机号码"
                className={errors.shippingPhone ? 'error' : ''}
              />
              {errors.shippingPhone && <span className="error-message">{errors.shippingPhone}</span>}
            </div>
          </div>
        </div>

        <div className="form-section">
          <h3>配送地址</h3>

          <div className="form-row">
            <div className="form-group">
              <label>省份 *</label>
              <select
                name="selectedProvince"
                value={formData.selectedProvince}
                onChange={handleProvinceChange}
                className={errors.selectedProvince ? 'error' : ''}
              >
                <option value="">请选择省份</option>
                {(provinces || []).map(province => (
                  <option key={province.value} value={province.value}>
                    {province.label}
                  </option>
                ))}
              </select>
              {errors.selectedProvince && <span className="error-message">{errors.selectedProvince}</span>}
            </div>
            
            <div className="form-group">
              <label>城市 *</label>
              <select
                name="selectedCity"
                value={formData.selectedCity}
                onChange={handleCityChange}
                disabled={!formData.selectedProvince}
                className={errors.selectedCity ? 'error' : ''}
              >
                <option value="">请选择城市</option>
                {(cities || []).map(city => (
                  <option key={city.value} value={city.value}>
                    {city.label}
                  </option>
                ))}
              </select>
              {errors.selectedCity && <span className="error-message">{errors.selectedCity}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>区县 *</label>
              <select
                name="selectedArea"
                value={formData.selectedArea}
                onChange={handleAreaChange}
                disabled={!formData.selectedCity}
                className={errors.selectedArea ? 'error' : ''}
              >
                <option value="">请选择区县</option>
                {(areas || []).map(area => (
                  <option key={area.value} value={area.value}>
                    {area.label}
                  </option>
                ))}
              </select>
              {errors.selectedArea && <span className="error-message">{errors.selectedArea}</span>}
            </div>
            
            <div className="form-group">
              <label>&nbsp;</label>
              <div style={{height: '42px'}}></div>
            </div>
          </div>

          <div className="form-group">
            <label>详细地址 *</label>
            <input
              type="text"
              name="shippingAddress" // 🔥 修改：将 shippingAddressLine1 改为 shippingAddress
              value={formData.shippingAddress}
              onChange={handleInputChange}
              placeholder="请输入街道地址、小区、楼号等"
              className={errors.shippingAddress ? 'error' : ''} // 🔥 修改：错误字段名
            />
            {errors.shippingAddress && <span className="error-message">{errors.shippingAddress}</span>} {/* 🔥 修改：错误字段名 */}
          </div>
        </div>

        <div className="form-section">
          <h3>配送选项</h3>
          
          <div className="form-group">
            <label>配送方式</label>
            <select
              name="shippingMethod"
              value={formData.shippingMethod}
              onChange={handleInputChange}
            >
              <option value="standard">标准配送 (免费)</option>
              <option value="express">加急配送 (+¥15)</option>
            </select>
          </div>

          <div className="form-group">
            <label>配送时间</label>
            <select
              name="deliveryTime"
              value={formData.deliveryTime}
              onChange={handleInputChange}
            >
              <option value="anytime">任意时间</option>
              <option value="morning">上午 (9:00-12:00)</option>
              <option value="afternoon">下午 (14:00-18:00)</option>
              <option value="evening">晚上 (18:00-21:00)</option>
            </select>
          </div>

          <div className="form-group">
            <label>配送备注（可选）</label>
            <textarea
              name="deliveryNote"
              value={formData.deliveryNote}
              onChange={handleInputChange}
              placeholder="例如：放门口、电话联系等"
              rows="3"
            />
          </div>
        </div>

        <div className="form-actions">
          <button 
            type="submit" 
            className="continue-btn"
            disabled={loading}
          >
            {loading ? '处理中...' : '继续到订单确认'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default DeliveryForm;