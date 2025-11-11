package com.ecommerce.product.repository;

import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.model.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByEnglishName(String englishName);
    List<Product> findByCategoryTypeAndIsActiveTrueOrderBySortOrderAsc(ProductCategory categoryType);
    List<Product> findByIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAsc();
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% AND p.isActive = true")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    /**
     * 搜索产品（支持分页，在名称、描述、英文名称、标签中搜索）
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.englishName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "p.isActive = true " +
           "ORDER BY p.isFeatured DESC, p.sortOrder ASC, p.name ASC")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 搜索产品（返回列表，支持分页）
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.englishName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "p.isActive = true " +
           "ORDER BY p.isFeatured DESC, p.sortOrder ASC, p.name ASC")
    List<Product> searchProductsList(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.categoryType = :categoryType AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findFeaturedByCategory(@Param("categoryType") ProductCategory categoryType, Pageable pageable);

    /**
     * 查询所有猕猴桃产品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', '猕猴桃', '%')) OR " +
           "LOWER(p.englishName) LIKE LOWER(CONCAT('%', 'kiwi', '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', '猕猴桃', '%')) OR " +
           "p.kiwiVariety IS NOT NULL) AND " +
           "p.isActive = true " +
           "ORDER BY p.sortOrder ASC")
    List<Product> findKiwis();

    /**
     * 查询推荐的猕猴桃产品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', '猕猴桃', '%')) OR " +
           "LOWER(p.englishName) LIKE LOWER(CONCAT('%', 'kiwi', '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', '猕猴桃', '%')) OR " +
           "p.kiwiVariety IS NOT NULL) AND " +
           "p.isFeatured = true AND " +
           "p.isActive = true " +
           "ORDER BY p.sortOrder ASC")
    List<Product> findFeaturedKiwis();

    /**
     * 根据分类ID查询产品
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findByCategoryIdAndIsActiveTrue(@Param("categoryId") Long categoryId);

    /**
     * 查询猕猴桃分类的产品
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findKiwisByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据产品名称模糊查询猕猴桃
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', '猕猴桃', '%')) OR " +
           "LOWER(p.englishName) LIKE LOWER(CONCAT('%', 'kiwi', '%'))) AND " +
           "p.isActive = true " +
           "ORDER BY p.sortOrder ASC")
    List<Product> findKiwisByName();

    /**
     * 根据猕猴桃品种查询
     */
    @Query("SELECT p FROM Product p WHERE p.kiwiVariety IS NOT NULL AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findKiwisByVariety();

    /**
     * 查询有酸度等级的猕猴桃产品
     */
    @Query("SELECT p FROM Product p WHERE p.acidityLevel IS NOT NULL AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findKiwisWithAcidityLevel();

    /**
     * 根据酸度等级查询猕猴桃
     */
    @Query("SELECT p FROM Product p WHERE p.acidityLevel = :acidityLevel AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findKiwisByAcidityLevel(@Param("acidityLevel") Integer acidityLevel);

    /**
     * 查询有维生素C含量的猕猴桃产品
     */
    @Query("SELECT p FROM Product p WHERE p.vitaminCContent IS NOT NULL AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findKiwisWithVitaminC();

    /**
     * 根据果皮类型查询猕猴桃
     */
    @Query("SELECT p FROM Product p WHERE p.skinType = :skinType AND p.isActive = true ORDER BY p.sortOrder ASC")
    List<Product> findKiwisBySkinType(@Param("skinType") String skinType);

    /**
     * 查询所有有猕猴桃专用字段的产品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(p.acidityLevel IS NOT NULL OR " +
           "p.kiwiVariety IS NOT NULL OR " +
           "p.vitaminCContent IS NOT NULL OR " +
           "p.skinType IS NOT NULL) AND " +
           "p.isActive = true " +
           "ORDER BY p.sortOrder ASC")
    List<Product> findProductsWithKiwiFields();
}