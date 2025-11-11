package com.ecommerce.product.service;

import com.ecommerce.product.controller.SearchController.SearchResult;
import com.ecommerce.product.controller.SearchController.SearchStats;
import com.ecommerce.product.controller.SearchController.SearchTermCount;
import com.ecommerce.product.model.dto.ProductResponse;
import com.ecommerce.product.model.entity.elasticsearch.EsProduct;
import com.ecommerce.product.repository.elasticsearch.EsProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final EsProductRepository esProductRepository;

    @Override
    public List<ProductResponse> searchProducts(String query, String category) {
        log.info("Searching products with query: {}, category: {}", query, category);
        
        List<EsProduct> esProducts;
        if (category != null && !category.isEmpty()) {
            esProducts = esProductRepository.findByCategoryTypeAndNameContaining(category, query);
        } else {
            esProducts = esProductRepository.findByNameContainingOrDescriptionContaining(query, query);
        }
        
        return convertToProductResponses(esProducts);
    }

    @Override
    public List<String> getSearchSuggestions(String query) {
        log.info("Getting search suggestions for query: {}", query);
        
        List<EsProduct> esProducts = esProductRepository.findByNameContaining(query);
        
        return esProducts.stream()
            .map(EsProduct::getName)
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }

    @Override
    public List<SearchTermCount> getPopularSearches() {
        log.info("Getting popular searches");
        return List.of();
    }

    @Override
    public List<String> autocomplete(String prefix) {
        log.info("Getting autocomplete suggestions for prefix: {}", prefix);
        return getSearchSuggestions(prefix);
    }

    @Override
    public SearchResult searchProductsPaged(String query, String category, int page, int size) {
        log.info("Searching products paged - query: {}, category: {}, page: {}, size: {}", 
                 query, category, page, size);
        
        // 简化版本：直接调用非分页搜索
        List<ProductResponse> products = searchProducts(query, category);
        
        // 创建 SearchResult 对象
        SearchResult result = new SearchResult();
        return result;
    }

    @Override
    public SearchStats getSearchStats() {
        log.info("Getting search stats");
        return new SearchStats();
    }

    @Override
    public void recordSearchHistory(String query, String userId) {
        log.info("Recording search history - query: {}, userId: {}", query, userId);
    }

    private List<ProductResponse> convertToProductResponses(List<EsProduct> esProducts) {
        return esProducts.stream()
            .map(this::convertToProductResponse)
            .collect(Collectors.toList());
    }

    private ProductResponse convertToProductResponse(EsProduct esProduct) {
        ProductResponse response = new ProductResponse();
        response.setId(esProduct.getId());
        response.setName(esProduct.getName());
        response.setEnglishName(esProduct.getEnglishName());
        response.setDescription(esProduct.getDescription());
        response.setShortDescription(esProduct.getShortDescription());
        response.setCategoryType(esProduct.getCategoryType());
        
        // 修复：将 Double 转换为 BigDecimal
        if (esProduct.getBasePrice() != null) {
            response.setBasePrice(BigDecimal.valueOf(esProduct.getBasePrice()));
        }
        
        response.setWeightUnit(esProduct.getWeightUnit());
        response.setOrigin(esProduct.getOrigin());
        response.setStorageMethod(esProduct.getStorageMethod());
        response.setShelfLife(esProduct.getShelfLife());
        response.setNutritionalInfo(esProduct.getNutritionalInfo());
        response.setMainImageUrl(esProduct.getMainImageUrl());
        
        // 修复：将 List<String> 转换为 String
        if (esProduct.getImageUrls() != null) {
            response.setImageUrls(String.join(",", esProduct.getImageUrls()));
        }
        
        response.setIsFeatured(esProduct.getIsFeatured());
        response.setIsActive(esProduct.getIsActive());
        response.setSortOrder(esProduct.getSortOrder());
        
        // 修复：将 List<String> tags 转换为 String
        if (esProduct.getTags() != null) {
            response.setTags(String.join(",", esProduct.getTags()));
        }
        
        response.setSeason(esProduct.getSeason());
        response.setTasteDescription(esProduct.getTasteDescription());
        response.setGrowingMethod(esProduct.getGrowingMethod());
        response.setCertification(esProduct.getCertification());
        response.setViewCount(esProduct.getViewCount());
        response.setSalesCount(esProduct.getSalesCount());
        
        return response;
    }
}