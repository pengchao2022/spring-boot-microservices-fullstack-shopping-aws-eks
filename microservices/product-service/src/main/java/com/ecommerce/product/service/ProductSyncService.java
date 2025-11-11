package com.ecommerce.product.service;

import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.model.entity.elasticsearch.EsProduct;
import com.ecommerce.product.model.entity.elasticsearch.EsProductConverter;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.elasticsearch.EsProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 添加这行导入

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSyncService {
    
    private final ProductRepository productRepository;
    private final EsProductRepository esProductRepository;
    private final EsProductConverter esProductConverter;
    
    /**
     * 同步单个产品到 Elasticsearch
     */
    @Transactional
    public void syncProductToElasticsearch(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("产品不存在: " + productId));
            
            EsProduct esProduct = esProductConverter.convertToEsProduct(product);
            esProductRepository.save(esProduct);
            
            log.info("同步产品到 Elasticsearch 成功: {}", productId);
        } catch (Exception e) {
            log.error("同步产品到 Elasticsearch 失败: {}, 错误: {}", productId, e.getMessage(), e);
            throw new RuntimeException("同步产品失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量同步所有产品到 Elasticsearch
     */
    @Transactional
    public void syncAllProductsToElasticsearch() {
        try {
            log.info("开始批量同步所有产品到 Elasticsearch...");
            
            List<Product> products = productRepository.findAll();
            List<EsProduct> esProducts = esProductConverter.convertToEsProducts(products);
            
            // 先删除所有现有数据
            esProductRepository.deleteAll();
            
            // 批量保存新数据
            esProductRepository.saveAll(esProducts);
            
            log.info("批量同步完成，共同步 {} 个产品到 Elasticsearch", esProducts.size());
        } catch (Exception e) {
            log.error("批量同步产品到 Elasticsearch 失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量同步失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从 Elasticsearch 删除产品
     */
    public void deleteProductFromElasticsearch(Long productId) {
        try {
            esProductRepository.deleteById(productId);
            log.info("从 Elasticsearch 删除产品成功: {}", productId);
        } catch (Exception e) {
            log.error("从 Elasticsearch 删除产品失败: {}, 错误: {}", productId, e.getMessage(), e);
        }
    }
    
    /**
     * 重建 Elasticsearch 索引
     */
    @Transactional
    public void rebuildElasticsearchIndex() {
        try {
            log.info("开始重建 Elasticsearch 索引...");
            
            // 删除现有索引
            esProductRepository.deleteAll();
            
            // 重新同步所有数据
            syncAllProductsToElasticsearch();
            
            log.info("Elasticsearch 索引重建完成");
        } catch (Exception e) {
            log.error("重建 Elasticsearch 索引失败: {}", e.getMessage(), e);
            throw new RuntimeException("重建索引失败: " + e.getMessage(), e);
        }
    }
}