// hooks/useSearch.js
import { useState, useEffect } from 'react';
import { searchService } from '../services/api/searchService';

export const useSearch = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [popularSearches, setPopularSearches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 搜索产品
  const searchProducts = async (query, category = '') => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }

    setLoading(true);
    setError(null);
    
    try {
      const result = await searchService.searchProducts(query, category);
      setSearchResults(result.data || []);
    } catch (err) {
      setError(err.message);
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  };

  // 获取搜索建议
  const getSuggestions = async (query) => {
    if (!query.trim()) {
      setSuggestions([]);
      return;
    }

    try {
      const result = await searchService.getSearchSuggestions(query);
      setSuggestions(result.data || []);
    } catch (err) {
      console.error('获取搜索建议失败:', err);
      setSuggestions([]);
    }
  };

  // 获取热门搜索
  const loadPopularSearches = async () => {
    try {
      const result = await searchService.getPopularSearches();
      setPopularSearches(result.data || []);
    } catch (err) {
      console.error('获取热门搜索失败:', err);
      setPopularSearches([]);
    }
  };

  // 清除搜索结果
  const clearResults = () => {
    setSearchResults([]);
    setError(null);
  };

  useEffect(() => {
    loadPopularSearches();
  }, []);

  return {
    searchResults,
    suggestions,
    popularSearches,
    loading,
    error,
    searchProducts,
    getSuggestions,
    clearResults
  };
};