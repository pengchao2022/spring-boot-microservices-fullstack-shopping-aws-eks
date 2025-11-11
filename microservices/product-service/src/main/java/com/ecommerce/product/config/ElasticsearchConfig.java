package com.ecommerce.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.ecommerce.product.repository.elasticsearch")
public class ElasticsearchConfig {
    // Spring Boot 会自动配置，无需额外代码
    // 只需要在 application.yml 中配置连接信息
}