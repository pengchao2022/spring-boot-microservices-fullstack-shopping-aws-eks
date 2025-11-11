// src/utils/regionUtils.js - 修复版本
import { province, city, county } from 'china-region-data';

// 立即检查数据结构
console.log('🔍 === 彻底检查 china-region-data 数据结构 ===');
console.log('省份数据类型:', Array.isArray(province) ? '数组' : typeof province);
console.log('省份数量:', province.length);
console.log('城市数据类型:', Array.isArray(city) ? '数组' : typeof city);
console.log('区县数据类型:', Array.isArray(county) ? '数组' : typeof county);

// 检查城市和区县的对象结构
console.log('🏙️ 城市对象键数量:', Object.keys(city).length);
console.log('📍 区县对象键数量:', Object.keys(county).length);

// 检查前几个省份的完整结构
console.log('📋 前3个省份的完整结构:');
province.slice(0, 3).forEach((p, i) => {
  console.log(`省份 ${i + 1}:`, p);
  console.log(`  所有属性:`, Object.keys(p));
});

// 检查城市对象的结构
console.log('🏙️ 城市对象示例:');
const firstProvinceId = province[0]?.id;
if (firstProvinceId && city[firstProvinceId]) {
  console.log(`省份 ${firstProvinceId} 的城市:`, city[firstProvinceId]);
  if (city[firstProvinceId].length > 0) {
    console.log('第一个城市结构:', city[firstProvinceId][0]);
    console.log('城市对象所有键:', Object.keys(city[firstProvinceId][0]));
  }
}

/**
 * 获取所有省份列表
 */
export const getProvinces = () => {
  console.log('🔄 getProvinces 被调用');
  
  const result = province.map(p => ({
    value: p.id,  // 使用 id 作为值
    label: p.name // 使用 name 作为显示文本
  }));
  
  console.log('📤 getProvinces 返回:', result.slice(0, 3));
  return result;
};

/**
 * 根据省份获取城市列表
 */
export const getCities = (provinceId) => {
  console.log('🔍 getCities 被调用，省份ID:', provinceId);
  
  if (!provinceId) {
    console.log('❌ 省份ID为空');
    return [];
  }
  
  // 直接从 city 对象中获取该省份的城市列表
  const cities = city[provinceId] || [];
  console.log(`📊 找到 ${cities.length} 个城市，省份ID: ${provinceId}`);
  
  const result = cities.map(c => ({
    value: c.id,   // 使用 id 作为值
    label: c.name  // 使用 name 作为显示文本
  }));
  
  console.log('📤 getCities 返回:', result);
  return result;
};

/**
 * 根据城市获取区县列表
 */
export const getAreas = (cityId) => {
  console.log('🔍 getAreas 被调用，城市ID:', cityId);
  
  if (!cityId) {
    console.log('❌ 城市ID为空');
    return [];
  }
  
  // 直接从 county 对象中获取该城市的区县列表
  const areas = county[cityId] || [];
  console.log(`📊 找到 ${areas.length} 个区县，城市ID: ${cityId}`);
  
  const result = areas.map(a => ({
    value: a.id,   // 使用 id 作为值
    label: a.name  // 使用 name 作为显示文本
  }));
  
  console.log('📤 getAreas 返回:', result);
  return result;
};

/**
 * 获取完整地区名称
 */
export const getFullRegionName = (provinceId, cityId, areaId) => {
  const prov = province.find(p => p.id === provinceId);
  if (!prov) return '';
  
  const cities = city[provinceId] || [];
  const cit = cities.find(c => c.id === cityId);
  if (!cit) return prov.name;
  
  const areas = county[cityId] || [];
  const ar = areas.find(a => a.id === areaId);
  if (!ar) return `${prov.name} ${cit.name}`;
  
  return `${prov.name} ${cit.name} ${ar.name}`;
};

/**
 * 获取级联选择器数据
 */
export const getChinaRegions = () => {
  return province.map(prov => ({
    value: prov.id,
    label: prov.name,
    children: (city[prov.id] || []).map(cityItem => ({
      value: cityItem.id,
      label: cityItem.name,
      children: (county[cityItem.id] || []).map(areaItem => ({
        value: areaItem.id,
        label: areaItem.name
      }))
    }))
  }));
};

/**
 * 调试函数
 */
export const debugRegions = () => {
  console.log('=== 完整调试信息 ===');
  console.log('省份数量:', province.length);
  console.log('城市对象键:', Object.keys(city).slice(0, 5));
  console.log('区县对象键:', Object.keys(county).slice(0, 5));
  
  // 显示第一个省份的完整信息
  if (province.length > 0) {
    const firstProvince = province[0];
    console.log('第一个省份:', firstProvince);
    console.log('该省份的城市:', city[firstProvince.id]);
    
    if (city[firstProvince.id] && city[firstProvince.id].length > 0) {
      const firstCity = city[firstProvince.id][0];
      console.log('第一个城市的区县:', county[firstCity.id]);
    }
  }
  
  return { province, city, county };
};

export default {
  getProvinces,
  getCities,
  getAreas,
  getFullRegionName,
  getChinaRegions,
  debugRegions
};