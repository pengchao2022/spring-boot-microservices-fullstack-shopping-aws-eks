package com.ecommerce.product.controller;

import com.ecommerce.product.model.dto.ApiResponse;
import com.ecommerce.product.model.dto.ProductResponse;
import com.ecommerce.product.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {
    
    private final ProductSearchService productSearchService;
    
    /**
     * 搜索产品
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false) String category) {
        try {
            log.info("搜索产品 - 关键词: {}, 分类: {}", q, category);
            
            List<ProductResponse> results = productSearchService.searchProducts(q, category);
            
            log.info("搜索完成，找到 {} 个结果", results.size());
            return ResponseEntity.ok(ApiResponse.success("搜索成功", results));
        } catch (Exception e) {
            log.error("搜索产品失败 - 关键词: {}, 错误: {}", q, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @RequestParam String q) {
        try {
            log.info("获取搜索建议 - 关键词: {}", q);
            
            List<String> suggestions = productSearchService.getSearchSuggestions(q);
            
            log.info("获取到 {} 个搜索建议", suggestions.size());
            return ResponseEntity.ok(ApiResponse.success("获取建议成功", suggestions));
        } catch (Exception e) {
            log.error("获取搜索建议失败 - 关键词: {}, 错误: {}", q, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取建议失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取热门搜索
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<SearchTermCount>>> getPopularSearches() {
        try {
            log.info("获取热门搜索");
            
            List<SearchTermCount> popularSearches = productSearchService.getPopularSearches();
            
            log.info("获取到 {} 个热门搜索", popularSearches.size());
            return ResponseEntity.ok(ApiResponse.success("获取热门搜索成功", popularSearches));
        } catch (Exception e) {
            log.error("获取热门搜索失败 - 错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取热门搜索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 自动完成搜索
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<String>>> autocomplete(
            @RequestParam String prefix) {
        try {
            log.info("自动完成搜索 - 前缀: {}", prefix);
            
            List<String> completions = productSearchService.autocomplete(prefix);
            
            log.info("获取到 {} 个自动完成建议", completions.size());
            return ResponseEntity.ok(ApiResponse.success("自动完成成功", completions));
        } catch (Exception e) {
            log.error("自动完成搜索失败 - 前缀: {}, 错误: {}", prefix, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("自动完成失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索产品（支持分页）
     */
    @GetMapping("/products/paged")
    public ResponseEntity<ApiResponse<SearchResult>> searchProductsPaged(
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("分页搜索产品 - 关键词: {}, 分类: {}, 页码: {}, 大小: {}", q, category, page, size);
            
            SearchResult result = productSearchService.searchProductsPaged(q, category, page, size);
            
            log.info("分页搜索完成，找到 {} 个结果，总记录数: {}", 
                    result.getProducts().size(), result.getTotalCount());
            return ResponseEntity.ok(ApiResponse.success("搜索成功", result));
        } catch (Exception e) {
            log.error("分页搜索产品失败 - 关键词: {}, 错误: {}", q, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<SearchStats>> getSearchStats() {
        try {
            log.info("获取搜索统计信息");
            
            SearchStats stats = productSearchService.getSearchStats();
            
            log.info("搜索统计信息获取成功");
            return ResponseEntity.ok(ApiResponse.success("获取搜索统计成功", stats));
        } catch (Exception e) {
            log.error("获取搜索统计信息失败 - 错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取搜索统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索词统计DTO
     */
    public static class SearchTermCount {
        private String term;
        private Long count;
        
        public SearchTermCount() {}
        
        public SearchTermCount(String term, Long count) {
            this.term = term;
            this.count = count;
        }
        
        // Getters and Setters
        public String getTerm() {
            return term;
        }
        
        public void setTerm(String term) {
            this.term = term;
        }
        
        public Long getCount() {
            return count;
        }
        
        public void setCount(Long count) {
            this.count = count;
        }
    }
    
    /**
     * 搜索结果DTO（分页）
     */
    public static class SearchResult {
        private List<ProductResponse> products;
        private long totalCount;
        private int currentPage;
        private int totalPages;
        
        public SearchResult() {}
        
        public SearchResult(List<ProductResponse> products, long totalCount, int currentPage, int totalPages) {
            this.products = products;
            this.totalCount = totalCount;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
        
        // Getters and Setters
        public List<ProductResponse> getProducts() {
            return products;
        }
        
        public void setProducts(List<ProductResponse> products) {
            this.products = products;
        }
        
        public long getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
    
    /**
     * 搜索统计DTO
     */
    public static class SearchStats {
        private long totalSearches;
        private long totalProducts;
        private List<SearchTermCount> topSearches;
        private List<CategoryCount> searchesByCategory;
        
        public SearchStats() {}
        
        public SearchStats(long totalSearches, long totalProducts, 
                          List<SearchTermCount> topSearches, 
                          List<CategoryCount> searchesByCategory) {
            this.totalSearches = totalSearches;
            this.totalProducts = totalProducts;
            this.topSearches = topSearches;
            this.searchesByCategory = searchesByCategory;
        }
        
        // Getters and Setters
        public long getTotalSearches() {
            return totalSearches;
        }
        
        public void setTotalSearches(long totalSearches) {
            this.totalSearches = totalSearches;
        }
        
        public long getTotalProducts() {
            return totalProducts;
        }
        
        public void setTotalProducts(long totalProducts) {
            this.totalProducts = totalProducts;
        }
        
        public List<SearchTermCount> getTopSearches() {
            return topSearches;
        }
        
        public void setTopSearches(List<SearchTermCount> topSearches) {
            this.topSearches = topSearches;
        }
        
        public List<CategoryCount> getSearchesByCategory() {
            return searchesByCategory;
        }
        
        public void setSearchesByCategory(List<CategoryCount> searchesByCategory) {
            this.searchesByCategory = searchesByCategory;
        }
    }
    
    /**
     * 分类统计DTO
     */
    public static class CategoryCount {
        private String category;
        private Long count;
        
        public CategoryCount() {}
        
        public CategoryCount(String category, Long count) {
            this.category = category;
            this.count = count;
        }
        
        // Getters and Setters
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public Long getCount() {
            return count;
        }
        
        public void setCount(Long count) {
            this.count = count;
        }
    }
}