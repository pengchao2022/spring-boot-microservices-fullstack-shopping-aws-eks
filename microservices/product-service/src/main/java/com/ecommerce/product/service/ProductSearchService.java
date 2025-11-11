package com.ecommerce.product.service;

import com.ecommerce.product.controller.SearchController.SearchResult;
import com.ecommerce.product.controller.SearchController.SearchStats;
import com.ecommerce.product.controller.SearchController.SearchTermCount;
import com.ecommerce.product.model.dto.ProductResponse;

import java.util.List;

public interface ProductSearchService {
    
    /**
     * 搜索产品
     */
    List<ProductResponse> searchProducts(String query, String category);
    
    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(String query);
    
    /**
     * 获取热门搜索
     */
    List<SearchTermCount> getPopularSearches();
    
    /**
     * 自动完成搜索
     */
    List<String> autocomplete(String prefix);
    
    /**
     * 分页搜索产品
     */
    SearchResult searchProductsPaged(String query, String category, int page, int size);
    
    /**
     * 获取搜索统计信息
     */
    SearchStats getSearchStats();
    
    /**
     * 记录搜索历史（可选）
     */
    void recordSearchHistory(String query, String userId);
}