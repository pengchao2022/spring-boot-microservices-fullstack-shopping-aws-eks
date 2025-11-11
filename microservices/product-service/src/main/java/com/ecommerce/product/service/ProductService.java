package com.ecommerce.product.service;

import com.ecommerce.product.model.dto.CreateAppleRequest;
import com.ecommerce.product.model.dto.KiwiProductResponse;
import com.ecommerce.product.model.dto.ProductDetailDTO;
import com.ecommerce.product.model.dto.ProductResponse;
import com.ecommerce.product.model.dto.ProductVariantResponse;
import com.ecommerce.product.model.dto.UpdateAppleRequest;
import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.model.entity.ProductCategory;
import com.ecommerce.product.model.entity.ProductVariant;
import com.ecommerce.product.model.entity.elasticsearch.EsProduct;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductVariantRepository;
import com.ecommerce.product.repository.elasticsearch.EsProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final EsProductRepository esProductRepository;

    public ProductService(ProductRepository productRepository, 
                         ProductVariantRepository productVariantRepository,
                         EsProductRepository esProductRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.esProductRepository = esProductRepository;
    }

    /**
     * 搜索产品
     */
    public List<ProductResponse> searchProducts(String keyword, int page, int size) {
        try {
            log.info("Searching products with keyword: '{}', page: {}, size: {}", keyword, page, size);
            
            // 首先尝试使用 Elasticsearch 搜索（如果可用）
            if (isElasticsearchAvailable()) {
                try {
                    return searchProductsWithElasticsearch(keyword, page, size);
                } catch (Exception e) {
                    log.warn("Elasticsearch search failed, falling back to database search: {}", e.getMessage());
                }
            }
            
            // 回退到数据库搜索
            return searchProductsWithDatabase(keyword, page, size);
            
        } catch (Exception e) {
            log.error("Error searching products with keyword '{}': {}", keyword, e.getMessage());
            // 搜索失败时返回空列表
            return List.of();
        }
    }

    /**
     * 使用数据库搜索产品
     */
    private List<ProductResponse> searchProductsWithDatabase(String keyword, int page, int size) {
        try {
            log.debug("Using database search for keyword: '{}'", keyword);
            
            // 使用新的支持分页的搜索方法
            List<Product> products = productRepository.searchProductsList(
                keyword.toLowerCase(), 
                PageRequest.of(page, size)
            );
            
            log.info("Database search found {} products for keyword: '{}'", products.size(), keyword);
            return convertToProductResponseList(products);
            
        } catch (Exception e) {
            log.error("Database search failed for keyword '{}': {}", keyword, e.getMessage());
            // 如果分页搜索失败，尝试使用旧的搜索方法
            try {
                log.debug("Trying old search method for keyword: '{}'", keyword);
                List<Product> products = productRepository.searchProducts(keyword.toLowerCase());
                return convertToProductResponseList(products);
            } catch (Exception ex) {
                log.error("All search methods failed for keyword '{}': {}", keyword, ex.getMessage());
                throw new RuntimeException("Search failed: " + ex.getMessage());
            }
        }
    }

    /**
     * 使用 Elasticsearch 搜索产品 - 使用 @Query 修复版本
     */
    private List<ProductResponse> searchProductsWithElasticsearch(String keyword, int page, int size) {
        try {
            log.debug("Using Elasticsearch search for keyword: '{}'", keyword);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<EsProduct> esProductPage = null;
            
            // 方案1：优先使用 @Query 注解的综合搜索
            log.debug("Attempt 1: Trying @Query comprehensive search...");
            try {
                esProductPage = esProductRepository.comprehensiveSearch(keyword, pageable);
                log.debug("@Query comprehensive search returned {} results", esProductPage.getContent().size());
                if (!esProductPage.getContent().isEmpty()) {
                    log.debug("First result: {}", esProductPage.getContent().get(0).getName());
                }
            } catch (Exception e) {
                log.error("❌ @Query comprehensive search FAILED: {}", e.getMessage());
            }
            
            // 方案2：如果综合搜索没有结果，尝试加权搜索
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 2: Trying @Query weighted search...");
                try {
                    esProductPage = esProductRepository.weightedSearch(keyword, pageable);
                    log.debug("@Query weighted search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ @Query weighted search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案3：如果加权搜索没有结果，尝试 searchText 字段搜索
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 3: Trying @Query searchText match...");
                try {
                    esProductPage = esProductRepository.findBySearchTextUsingMatch(keyword, pageable);
                    log.debug("@Query searchText match returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ @Query searchText match FAILED: {}", e.getMessage());
                }
            }
            
            // 方案4：如果 searchText 搜索没有结果，尝试多字段搜索
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 4: Trying @Query multi-field search...");
                try {
                    esProductPage = esProductRepository.searchByMultipleFields(keyword, pageable);
                    log.debug("@Query multi-field search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ @Query multi-field search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案5：如果多字段搜索没有结果，尝试 query_string 搜索
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 5: Trying @Query query_string search...");
                try {
                    esProductPage = esProductRepository.searchByQueryString(keyword, pageable);
                    log.debug("@Query query_string search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ @Query query_string search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案6：如果 query_string 搜索没有结果，尝试通配符搜索
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 6: Trying @Query wildcard search...");
                try {
                    esProductPage = esProductRepository.searchByWildcard(keyword, pageable);
                    log.debug("@Query wildcard search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ @Query wildcard search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案7：如果通配符搜索没有结果，尝试短语匹配
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 7: Trying @Query phrase search...");
                try {
                    esProductPage = esProductRepository.searchByPhrase(keyword, pageable);
                    log.debug("@Query phrase search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ @Query phrase search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案8：如果以上 @Query 方法都失败，回退到原始的 Containing 方法
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 8: Trying original Containing methods...");
                try {
                    esProductPage = esProductRepository.findByNameContainingOrDescriptionContainingOrEnglishNameContaining(
                        keyword, keyword, keyword, pageable);
                    log.debug("Original Containing search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ Original Containing search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案9：如果原始的 Containing 方法失败，尝试 searchText Containing 方法
            if (esProductPage == null || esProductPage.getContent().isEmpty()) {
                log.debug("Attempt 9: Trying searchText Containing method...");
                try {
                    esProductPage = esProductRepository.findBySearchTextContaining(keyword, pageable);
                    log.debug("SearchText Containing search returned {} results", esProductPage.getContent().size());
                } catch (Exception e) {
                    log.error("❌ SearchText Containing search FAILED: {}", e.getMessage());
                }
            }
            
            // 方案10：如果所有方法都失败，记录错误
            if (esProductPage == null) {
                log.error("❌ ALL Elasticsearch search methods failed for keyword: '{}'", keyword);
                throw new RuntimeException("All Elasticsearch search methods failed");
            }
            
            if (esProductPage.getContent().isEmpty()) {
                log.info("No products found in Elasticsearch for keyword: '{}'", keyword);
            }
            
            // 从 Page 对象中获取内容
            List<EsProduct> esProducts = esProductPage.getContent();
            
            // 将 EsProduct 转换为 ProductResponse
            List<ProductResponse> results = esProducts.stream()
                .map(this::convertEsProductToProductResponse)
                .collect(Collectors.toList());
            
            log.info("Elasticsearch search found {} products for keyword: '{}'", results.size(), keyword);
            return results;
            
        } catch (Exception e) {
            log.error("❌ Elasticsearch search completely failed for keyword '{}': {}", keyword, e.getMessage());
            throw new RuntimeException("Elasticsearch search failed: " + e.getMessage());
        }
    }

    /**
     * 检查 Elasticsearch 是否可用
     */
    private boolean isElasticsearchAvailable() {
        try {
            // 检查 Elasticsearch 是否有数据
            long count = esProductRepository.count();
            log.debug("Elasticsearch document count: {}", count);
            return count > 0; // 如果有数据则认为可用
        } catch (Exception e) {
            log.debug("Elasticsearch is not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 将 EsProduct 转换为 ProductResponse
     */
    private ProductResponse convertEsProductToProductResponse(EsProduct esProduct) {
        ProductResponse response = new ProductResponse();
        response.setId(esProduct.getId());
        response.setName(esProduct.getName());
        response.setEnglishName(esProduct.getEnglishName());
        response.setDescription(esProduct.getDescription());
        response.setShortDescription(esProduct.getShortDescription());
        response.setCategoryType(esProduct.getCategoryType());
        
        // 修复：Double 转 BigDecimal
        if (esProduct.getBasePrice() != null) {
            response.setBasePrice(java.math.BigDecimal.valueOf(esProduct.getBasePrice()));
        }
        
        response.setWeightUnit(esProduct.getWeightUnit());
        response.setOrigin(esProduct.getOrigin());
        response.setStorageMethod(esProduct.getStorageMethod());
        response.setShelfLife(esProduct.getShelfLife());
        response.setNutritionalInfo(esProduct.getNutritionalInfo());
        response.setMainImageUrl(esProduct.getMainImageUrl());
        response.setImageUrls(esProduct.getImageUrls() != null ? String.join(",", esProduct.getImageUrls()) : null);
        response.setIsFeatured(esProduct.getIsFeatured());
        response.setIsActive(esProduct.getIsActive());
        response.setSortOrder(esProduct.getSortOrder());
        response.setTags(esProduct.getTags() != null ? String.join(",", esProduct.getTags()) : null);
        response.setSeason(esProduct.getSeason());
        response.setTasteDescription(esProduct.getTasteDescription());
        response.setGrowingMethod(esProduct.getGrowingMethod());
        response.setCertification(esProduct.getCertification());
        response.setViewCount(esProduct.getViewCount());
        response.setSalesCount(esProduct.getSalesCount());
        
        // 修复：LocalDate 转 LocalDateTime（添加时间部分）
        if (esProduct.getCreatedAt() != null) {
            response.setCreatedAt(esProduct.getCreatedAt().atStartOfDay());
        }
        if (esProduct.getUpdatedAt() != null) {
            response.setUpdatedAt(esProduct.getUpdatedAt().atStartOfDay());
        }
        
        // 添加苹果专用字段
        response.setSweetnessLevel(esProduct.getSweetnessLevel());
        response.setCrunchinessLevel(esProduct.getCrunchinessLevel());
        response.setAppleVariety(esProduct.getAppleVariety());
        response.setHarvestSeason(esProduct.getHarvestSeason());
        
        // 注意：ProductResponse 类中没有猕猴桃专用字段的setter方法
        // 所以这里不设置 acidityLevel, kiwiVariety, vitaminCContent, skinType
        
        return response;
    }

    public ProductDetailDTO getProductDetailByEnglishName(String englishName) {
        Product product = productRepository.findByEnglishName(englishName)
            .orElseThrow(() -> new RuntimeException("Product not found: " + englishName));
        
        List<ProductVariant> variants = productVariantRepository.findByProductIdAndIsActiveTrueOrderBySortOrderAsc(product.getId());
        
        ProductDetailDTO detailDTO = new ProductDetailDTO();
        detailDTO.setProduct(convertToProductResponse(product));
        detailDTO.setVariants(convertToVariantDTOList(variants));
        
        if (!variants.isEmpty()) {
            detailDTO.setDefaultVariant(convertToVariantDTO(variants.get(0)));
        }
        
        return detailDTO;
    }

    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findByCategoryTypeAndIsActiveTrueOrderBySortOrderAsc(category);
        return convertToProductResponseList(products);
    }

    public List<ProductResponse> getFeaturedProductsByCategory(ProductCategory category, int limit) {
        List<Product> products = productRepository.findFeaturedByCategory(
            category, PageRequest.of(0, limit)
        );
        return convertToProductResponseList(products);
    }

    /**
     * 获取苹果分类的所有产品
     */
    public List<ProductResponse> getAppleCategoryProducts() {
        List<Product> fruits = productRepository.findByCategoryTypeAndIsActiveTrueOrderBySortOrderAsc(ProductCategory.FRUIT);
        
        // 过滤出苹果产品（根据名称或特定标识）
        List<Product> appleProducts = fruits.stream()
            .filter(this::isAppleProduct)
            .collect(Collectors.toList());
            
        log.info("Found {} apple products", appleProducts.size());
        return convertToProductResponseList(appleProducts);
    }

    /**
     * 获取猕猴桃分类的所有产品
     */
    public List<KiwiProductResponse> getKiwiCategoryProducts() {
        List<Product> fruits = productRepository.findByCategoryTypeAndIsActiveTrueOrderBySortOrderAsc(ProductCategory.FRUIT);
        
        // 过滤出猕猴桃产品（根据名称或特定标识）
        List<Product> kiwiProducts = fruits.stream()
            .filter(this::isKiwiProduct)
            .collect(Collectors.toList());
            
        log.info("Found {} kiwi products", kiwiProducts.size());
        return convertToKiwiProductResponseList(kiwiProducts);
    }

    /**
     * 根据ID获取猕猴桃详情
     */
    public KiwiProductResponse getKiwiProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kiwi product not found with id: " + id));
        
        // 验证是否为猕猴桃产品
        if (!isKiwiProduct(product)) {
            throw new RuntimeException("Product is not a kiwi: " + id);
        }
        
        return convertToKiwiProductResponse(product);
    }

    /**
     * 根据英文名称获取猕猴桃详情
     */
    public KiwiProductResponse getKiwiProductByEnglishName(String englishName) {
        Product product = productRepository.findByEnglishName(englishName)
            .orElseThrow(() -> new RuntimeException("Kiwi product not found: " + englishName));
        
        // 验证是否为猕猴桃产品
        if (!isKiwiProduct(product)) {
            throw new RuntimeException("Product is not a kiwi: " + englishName);
        }
        
        return convertToKiwiProductResponse(product);
    }

    /**
     * 获取推荐的苹果产品
     */
    public List<ProductResponse> getFeaturedApples(int limit) {
        List<Product> featuredFruits = productRepository.findFeaturedByCategory(
            ProductCategory.FRUIT, PageRequest.of(0, limit)
        );
        
        // 过滤出苹果产品
        List<Product> featuredApples = featuredFruits.stream()
            .filter(this::isAppleProduct)
            .collect(Collectors.toList());
            
        log.info("Found {} featured apple products", featuredApples.size());
        return convertToProductResponseList(featuredApples);
    }

    /**
     * 获取推荐的猕猴桃产品
     */
    public List<KiwiProductResponse> getFeaturedKiwis(int limit) {
        List<Product> featuredFruits = productRepository.findFeaturedByCategory(
            ProductCategory.FRUIT, PageRequest.of(0, limit)
        );
        
        // 过滤出猕猴桃产品
        List<Product> featuredKiwis = featuredFruits.stream()
            .filter(this::isKiwiProduct)
            .collect(Collectors.toList());
            
        log.info("Found {} featured kiwi products", featuredKiwis.size());
        return convertToKiwiProductResponseList(featuredKiwis);
    }

    /**
     * 根据英文名称获取苹果详情
     */
    public ProductDetailDTO getAppleDetailByEnglishName(String englishName) {
        Product product = productRepository.findByEnglishName(englishName)
            .orElseThrow(() -> new RuntimeException("Apple product not found: " + englishName));
        
        // 验证是否为苹果产品
        if (!isAppleProduct(product)) {
            throw new RuntimeException("Product is not an apple: " + englishName);
        }
        
        List<ProductVariant> variants = productVariantRepository.findByProductIdAndIsActiveTrueOrderBySortOrderAsc(product.getId());
        
        ProductDetailDTO detailDTO = new ProductDetailDTO();
        detailDTO.setProduct(convertToProductResponse(product));
        detailDTO.setVariants(convertToVariantDTOList(variants));
        
        if (!variants.isEmpty()) {
            detailDTO.setDefaultVariant(convertToVariantDTO(variants.get(0)));
        }
        
        log.info("Retrieved apple detail for: {}", englishName);
        return detailDTO;
    }

    /**
     * 根据英文名称获取猕猴桃详情
     */
    public ProductDetailDTO getKiwiDetailByEnglishName(String englishName) {
        Product product = productRepository.findByEnglishName(englishName)
            .orElseThrow(() -> new RuntimeException("Kiwi product not found: " + englishName));
        
        // 验证是否为猕猴桃产品
        if (!isKiwiProduct(product)) {
            throw new RuntimeException("Product is not a kiwi: " + englishName);
        }
        
        List<ProductVariant> variants = productVariantRepository.findByProductIdAndIsActiveTrueOrderBySortOrderAsc(product.getId());
        
        ProductDetailDTO detailDTO = new ProductDetailDTO();
        detailDTO.setProduct(convertToProductResponse(product));
        detailDTO.setVariants(convertToVariantDTOList(variants));
        
        if (!variants.isEmpty()) {
            detailDTO.setDefaultVariant(convertToVariantDTO(variants.get(0)));
        }
        
        log.info("Retrieved kiwi detail for: {}", englishName);
        return detailDTO;
    }

    /**
     * 后台管理 - 更新苹果产品
     */
    public ProductResponse updateAppleProduct(Long id, UpdateAppleRequest updateRequest) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Apple product not found: " + id));
        
        // 验证是否为苹果产品
        if (!isAppleProduct(product)) {
            throw new RuntimeException("Product is not an apple: " + id);
        }
        
        log.info("Updating apple product: {}, request: {}", id, updateRequest);
        
        // 更新字段
        if (updateRequest.getName() != null) {
            product.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            product.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getShortDescription() != null) {
            product.setShortDescription(updateRequest.getShortDescription());
        }
        if (updateRequest.getBasePrice() != null) {
            product.setBasePrice(updateRequest.getBasePrice());
        }
        if (updateRequest.getWeightUnit() != null) {
            product.setWeightUnit(updateRequest.getWeightUnit());
        }
        if (updateRequest.getMainImageUrl() != null) {
            product.setMainImageUrl(updateRequest.getMainImageUrl());
        }
        if (updateRequest.getOrigin() != null) {
            product.setOrigin(updateRequest.getOrigin());
        }
        if (updateRequest.getIsFeatured() != null) {
            product.setIsFeatured(updateRequest.getIsFeatured());
        }
        if (updateRequest.getSortOrder() != null) {
            product.setSortOrder(updateRequest.getSortOrder());
        }
        if (updateRequest.getSweetnessLevel() != null) {
            product.setSweetnessLevel(updateRequest.getSweetnessLevel());
        }
        if (updateRequest.getCrunchinessLevel() != null) {
            product.setCrunchinessLevel(updateRequest.getCrunchinessLevel());
        }
        if (updateRequest.getAppleVariety() != null) {
            product.setAppleVariety(updateRequest.getAppleVariety());
        }
        if (updateRequest.getHarvestSeason() != null) {
            product.setHarvestSeason(updateRequest.getHarvestSeason());
        }
        if (updateRequest.getStorageMethod() != null) {
            product.setStorageMethod(updateRequest.getStorageMethod());
        }
        if (updateRequest.getShelfLife() != null) {
            product.setShelfLife(updateRequest.getShelfLife());
        }
        if (updateRequest.getNutritionalInfo() != null) {
            product.setNutritionalInfo(updateRequest.getNutritionalInfo());
        }
        if (updateRequest.getTags() != null) {
            product.setTags(updateRequest.getTags());
        }
        if (updateRequest.getSeason() != null) {
            product.setSeason(updateRequest.getSeason());
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        
        log.info("Apple product updated successfully: {}", id);
        return convertToProductResponse(savedProduct);
    }

    /**
     * 后台管理 - 创建苹果产品
     */
    public ProductResponse createAppleProduct(CreateAppleRequest createRequest) {
        log.info("Creating new apple product: {}", createRequest);
        
        // 检查英文名称是否已存在
        if (createRequest.getEnglishName() != null) {
            productRepository.findByEnglishName(createRequest.getEnglishName())
                .ifPresent(existing -> {
                    throw new RuntimeException("Product with english name already exists: " + createRequest.getEnglishName());
                });
        }
        
        Product product = new Product();
        product.setName(createRequest.getName());
        product.setEnglishName(createRequest.getEnglishName());
        product.setDescription(createRequest.getDescription());
        product.setShortDescription(createRequest.getShortDescription());
        product.setCategoryType(ProductCategory.FRUIT);
        product.setBasePrice(createRequest.getBasePrice());
        product.setWeightUnit(createRequest.getWeightUnit() != null ? createRequest.getWeightUnit() : "500g");
        product.setMainImageUrl(createRequest.getMainImageUrl());
        product.setOrigin(createRequest.getOrigin());
        product.setIsFeatured(createRequest.getIsFeatured() != null ? createRequest.getIsFeatured() : false);
        product.setSortOrder(createRequest.getSortOrder() != null ? createRequest.getSortOrder() : 0);
        product.setSweetnessLevel(createRequest.getSweetnessLevel());
        product.setCrunchinessLevel(createRequest.getCrunchinessLevel());
        product.setAppleVariety(createRequest.getAppleVariety());
        product.setHarvestSeason(createRequest.getHarvestSeason());
        product.setStorageMethod(createRequest.getStorageMethod());
        product.setShelfLife(createRequest.getShelfLife());
        product.setNutritionalInfo(createRequest.getNutritionalInfo());
        product.setTags(createRequest.getTags());
        product.setSeason(createRequest.getSeason());
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        
        log.info("Apple product created successfully with id: {}", savedProduct.getId());
        return convertToProductResponse(savedProduct);
    }

    /**
     * 后台管理 - 删除苹果产品（软删除）
     */
    public void deleteAppleProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Apple product not found: " + id));
        
        // 验证是否为苹果产品
        if (!isAppleProduct(product)) {
            throw new RuntimeException("Product is not an apple: " + id);
        }
        
        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        
        log.info("Apple product soft deleted: {}", id);
    }

    /**
     * 判断是否为苹果产品
     */
    private boolean isAppleProduct(Product product) {
        if (product == null) {
            return false;
        }
        
        // 根据产品名称、分类或特定字段判断是否为苹果
        boolean isApple = (product.getName() != null && product.getName().contains("苹果")) || 
               (product.getEnglishName() != null && product.getEnglishName().contains("apple")) ||
               (product.getTags() != null && product.getTags().contains("苹果")) ||
               (product.getAppleVariety() != null && !product.getAppleVariety().isEmpty());
        
        log.debug("Product '{}' is apple: {}", product.getName(), isApple);
        return isApple;
    }

    /**
     * 判断是否为猕猴桃产品
     */
    private boolean isKiwiProduct(Product product) {
        if (product == null) {
            return false;
        }
        
        // 根据产品名称、分类或特定字段判断是否为猕猴桃
        boolean isKiwi = (product.getName() != null && product.getName().contains("猕猴桃")) || 
               (product.getEnglishName() != null && product.getEnglishName().contains("kiwi")) ||
               (product.getTags() != null && product.getTags().contains("猕猴桃")) ||
               (product.getKiwiVariety() != null && !product.getKiwiVariety().isEmpty());
        
        log.debug("Product '{}' is kiwi: {}", product.getName(), isKiwi);
        return isKiwi;
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setEnglishName(product.getEnglishName());
        response.setDescription(product.getDescription());
        response.setShortDescription(product.getShortDescription());
        response.setCategoryType(product.getCategoryType().name());
        response.setBasePrice(product.getBasePrice());
        response.setWeightUnit(product.getWeightUnit());
        response.setOrigin(product.getOrigin());
        response.setStorageMethod(product.getStorageMethod());
        response.setShelfLife(product.getShelfLife());
        response.setNutritionalInfo(product.getNutritionalInfo());
        response.setMainImageUrl(product.getMainImageUrl());
        response.setImageUrls(product.getImageUrls());
        response.setIsFeatured(product.getIsFeatured());
        response.setIsActive(product.getIsActive());
        response.setSortOrder(product.getSortOrder());
        response.setTags(product.getTags());
        response.setSeason(product.getSeason());
        response.setTasteDescription(product.getTasteDescription());
        response.setGrowingMethod(product.getGrowingMethod());
        response.setCertification(product.getCertification());
        response.setViewCount(product.getViewCount());
        response.setSalesCount(product.getSalesCount());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        // 添加苹果专用字段
        response.setSweetnessLevel(product.getSweetnessLevel());
        response.setCrunchinessLevel(product.getCrunchinessLevel());
        response.setAppleVariety(product.getAppleVariety());
        response.setHarvestSeason(product.getHarvestSeason());
        
        // 注意：ProductResponse 类中没有猕猴桃专用字段的setter方法
        // 所以这里不设置 acidityLevel, kiwiVariety, vitaminCContent, skinType
        
        return response;
    }

    private KiwiProductResponse convertToKiwiProductResponse(Product product) {
        KiwiProductResponse response = new KiwiProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setEnglishName(product.getEnglishName());
        response.setDescription(product.getDescription());
        response.setShortDescription(product.getShortDescription());
        response.setCategoryType(product.getCategoryType().name());
        response.setBasePrice(product.getBasePrice());
        response.setWeightUnit(product.getWeightUnit());
        response.setOrigin(product.getOrigin());
        response.setStorageMethod(product.getStorageMethod());
        response.setShelfLife(product.getShelfLife());
        response.setNutritionalInfo(product.getNutritionalInfo());
        response.setMainImageUrl(product.getMainImageUrl());
        response.setImageUrls(product.getImageUrls());
        response.setIsFeatured(product.getIsFeatured());
        response.setIsActive(product.getIsActive());
        response.setSortOrder(product.getSortOrder());
        response.setTags(product.getTags());
        response.setSeason(product.getSeason());
        response.setTasteDescription(product.getTasteDescription());
        response.setGrowingMethod(product.getGrowingMethod());
        response.setCertification(product.getCertification());
        response.setViewCount(product.getViewCount());
        response.setSalesCount(product.getSalesCount());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        // 添加苹果专用字段
        response.setSweetnessLevel(product.getSweetnessLevel());
        response.setCrunchinessLevel(product.getCrunchinessLevel());
        response.setAppleVariety(product.getAppleVariety());
        response.setHarvestSeason(product.getHarvestSeason());
        
        // 添加猕猴桃专用字段
        response.setAcidityLevel(product.getAcidityLevel());
        response.setKiwiVariety(product.getKiwiVariety());
        response.setVitaminCContent(product.getVitaminCContent());
        response.setSkinType(product.getSkinType());
        
        return response;
    }

    private List<ProductResponse> convertToProductResponseList(List<Product> products) {
        return products.stream()
            .map(this::convertToProductResponse)
            .collect(Collectors.toList());
    }

    private List<KiwiProductResponse> convertToKiwiProductResponseList(List<Product> products) {
        return products.stream()
            .map(this::convertToKiwiProductResponse)
            .collect(Collectors.toList());
    }

    private ProductVariantResponse convertToVariantDTO(ProductVariant variant) {
        ProductVariantResponse dto = new ProductVariantResponse();
        dto.setId(variant.getId());
        dto.setVariantName(variant.getVariantName());
        dto.setWeight(variant.getWeight());
        dto.setWeightUnit(variant.getWeightUnit());
        dto.setPrice(variant.getPrice());
        dto.setOriginalPrice(variant.getOriginalPrice());
        dto.setStockQuantity(variant.getStockQuantity());
        dto.setIsInStock(variant.getIsInStock());
        dto.setIsActive(variant.getIsActive());
        dto.setSortOrder(variant.getSortOrder());
        dto.setVariantImageUrl(variant.getVariantImageUrl());
        dto.setCreatedAt(variant.getCreatedAt());
        dto.setUpdatedAt(variant.getUpdatedAt());
        return dto;
    }

    private List<ProductVariantResponse> convertToVariantDTOList(List<ProductVariant> variants) {
        return variants.stream()
            .map(this::convertToVariantDTO)
            .collect(Collectors.toList());
    }
}