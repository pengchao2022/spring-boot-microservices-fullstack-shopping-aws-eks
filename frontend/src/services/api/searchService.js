const SEARCH_API_BASE = '/api/search';

export const searchService = {
  // 搜索产品
  async searchProducts(query, category = '') {
    try {
      const params = new URLSearchParams();
      params.append('q', query);
      if (category) {
        params.append('category', category);
      }

      const response = await fetch(`${SEARCH_API_BASE}/products?${params}`);
      
      if (!response.ok) {
        throw new Error(`搜索失败: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('搜索服务错误:', error);
      throw error;
    }
  },

  // 搜索建议
  async getSearchSuggestions(query) {
    try {
      const response = await fetch(`${SEARCH_API_BASE}/suggestions?q=${encodeURIComponent(query)}`);
      
      if (!response.ok) {
        throw new Error(`获取搜索建议失败: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('搜索建议服务错误:', error);
      throw error;
    }
  },

  // 热门搜索
  async getPopularSearches() {
    try {
      const response = await fetch(`${SEARCH_API_BASE}/popular`);
      
      if (!response.ok) {
        throw new Error(`获取热门搜索失败: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('热门搜索服务错误:', error);
      throw error;
    }
  }
};