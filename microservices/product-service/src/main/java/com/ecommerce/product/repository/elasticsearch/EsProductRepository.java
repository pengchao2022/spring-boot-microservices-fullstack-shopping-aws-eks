package com.ecommerce.product.repository.elasticsearch;

import com.ecommerce.product.model.entity.elasticsearch.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, Long> {
    
    // 基础搜索方法
    List<EsProduct> findByNameContaining(String name);
    
    List<EsProduct> findByNameContainingOrDescriptionContaining(String name, String description);
    
    // 支持分页的搜索方法
    Page<EsProduct> findByNameContaining(String name, Pageable pageable);
    
    Page<EsProduct> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    
    // 新增：支持名称、描述、英文名称的多字段搜索（带分页）
    Page<EsProduct> findByNameContainingOrDescriptionContainingOrEnglishNameContaining(
        String name, String description, String englishName, Pageable pageable);
    
    // 新增：支持标签搜索
    Page<EsProduct> findByTagsContaining(String tag, Pageable pageable);
    
    // 新增：综合搜索（名称、描述、英文名称、标签）
    Page<EsProduct> findByNameContainingOrDescriptionContainingOrEnglishNameContainingOrTagsContaining(
        String name, String description, String englishName, String tags, Pageable pageable);
    
    // 新增：搜索 searchText 字段
    Page<EsProduct> findBySearchTextContaining(String searchText, Pageable pageable);
    
    // 新增：综合搜索包含 searchText 字段
    Page<EsProduct> findByNameContainingOrDescriptionContainingOrEnglishNameContainingOrSearchTextContaining(
        String name, String description, String englishName, String searchText, Pageable pageable);
    
    // ==================== 使用 @Query 注解的自定义查询 - 关键修复！ ====================
    
    /**
     * 使用原生 Elasticsearch match 查询搜索 searchText 字段
     * 这是对中文搜索的关键修复！
     */
    @Query("{\"match\": {\"searchText\": \"?0\"}}")
    Page<EsProduct> findBySearchTextUsingMatch(String keyword, Pageable pageable);
    
    /**
     * 使用 multi_match 查询多个字段
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"description\", \"englishName\", \"searchText\", \"tags\", \"origin\"]}}")
    Page<EsProduct> searchByMultipleFields(String keyword, Pageable pageable);
    
    /**
     * 使用 query_string 查询，支持更复杂的搜索语法
     */
    @Query("{\"query_string\": {\"query\": \"?0\", \"fields\": [\"searchText\", \"name^2\", \"description\", \"englishName\", \"tags\"], \"default_operator\": \"AND\"}}")
    Page<EsProduct> searchByQueryString(String keyword, Pageable pageable);
    
    /**
     * 使用 wildcard 查询进行模糊匹配
     */
    @Query("{\"wildcard\": {\"searchText\": \"*?0*\"}}")
    Page<EsProduct> searchByWildcard(String keyword, Pageable pageable);
    
    /**
     * 使用 match_phrase 进行短语匹配
     */
    @Query("{\"match_phrase\": {\"searchText\": \"?0\"}}")
    Page<EsProduct> searchByPhrase(String keyword, Pageable pageable);
    
    /**
     * 综合搜索 - 主要搜索方法
     */
    @Query("{" +
        "\"bool\": {" +
            "\"should\": [" +
                "{\"match\": {\"searchText\": \"?0\"}}," +
                "{\"match\": {\"name\": \"?0\"}}," +
                "{\"match\": {\"description\": \"?0\"}}," +
                "{\"match\": {\"englishName\": \"?0\"}}," +
                "{\"match\": {\"tags\": \"?0\"}}" +
            "]" +
        "}" +
    "}")
    Page<EsProduct> comprehensiveSearch(String keyword, Pageable pageable);
    
    /**
     * 带权重的综合搜索 - 名称和 searchText 有更高权重
     */
    @Query("{" +
        "\"bool\": {" +
            "\"should\": [" +
                "{\"match\": {\"searchText\": {\"query\": \"?0\", \"boost\": 2}}}," +
                "{\"match\": {\"name\": {\"query\": \"?0\", \"boost\": 2}}}," +
                "{\"match\": {\"description\": \"?0\"}}," +
                "{\"match\": {\"englishName\": \"?0\"}}," +
                "{\"match\": {\"tags\": \"?0\"}}" +
            "]" +
        "}" +
    "}")
    Page<EsProduct> weightedSearch(String keyword, Pageable pageable);
    
    // ==================== 分类相关搜索 ====================
    
    List<EsProduct> findByCategoryType(String categoryType);
    
    Page<EsProduct> findByCategoryType(String categoryType, Pageable pageable);
    
    List<EsProduct> findByCategoryTypeAndNameContaining(String categoryType, String name);
    
    Page<EsProduct> findByCategoryTypeAndNameContaining(String categoryType, String name, Pageable pageable);
    
    // ==================== 特色产品 ====================
    
    List<EsProduct> findByIsFeaturedTrue();
    
    Page<EsProduct> findByIsFeaturedTrue(Pageable pageable);
    
    // ==================== 价格范围搜索 ====================
    
    List<EsProduct> findByBasePriceBetween(Double minPrice, Double maxPrice);
    
    Page<EsProduct> findByBasePriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    
    // ==================== 产地搜索 ====================
    
    List<EsProduct> findByOrigin(String origin);
    
    Page<EsProduct> findByOrigin(String origin, Pageable pageable);
    
    // ==================== 按季节搜索 ====================
    
    List<EsProduct> findBySeason(String season);
    
    Page<EsProduct> findBySeason(String season, Pageable pageable);
    
    // ==================== 按苹果品种搜索 ====================
    
    List<EsProduct> findByAppleVarietyContaining(String variety);
    
    Page<EsProduct> findByAppleVarietyContaining(String variety, Pageable pageable);
    
    // ==================== 按猕猴桃品种搜索 ====================
    
    List<EsProduct> findByKiwiVarietyContaining(String variety);
    
    Page<EsProduct> findByKiwiVarietyContaining(String variety, Pageable pageable);
    
    // ==================== 活跃产品搜索 ====================
    
    List<EsProduct> findByIsActiveTrue();
    
    Page<EsProduct> findByIsActiveTrue(Pageable pageable);
    
    // ==================== 按分类和活跃状态搜索 ====================
    
    List<EsProduct> findByCategoryTypeAndIsActiveTrue(String categoryType);
    
    Page<EsProduct> findByCategoryTypeAndIsActiveTrue(String categoryType, Pageable pageable);
    
    // ==================== 综合搜索（包含活跃状态过滤） ====================
    
    Page<EsProduct> findByNameContainingAndIsActiveTrueOrDescriptionContainingAndIsActiveTrueOrEnglishNameContainingAndIsActiveTrue(
        String name, String description, String englishName, Pageable pageable);
    
    // ==================== 获取所有产品（带分页） ====================
    
    Page<EsProduct> findAllBy(Pageable pageable);
    
    // ==================== 统计方法 ====================
    
    long countByCategoryType(String categoryType);
    
    long countByIsFeaturedTrue();
    
    long countByIsActiveTrue();
    
    long countByCategoryTypeAndIsActiveTrue(String categoryType);
}