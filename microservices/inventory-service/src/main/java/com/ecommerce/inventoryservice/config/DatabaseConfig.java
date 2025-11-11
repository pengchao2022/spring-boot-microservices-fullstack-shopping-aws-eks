package com.ecommerce.inventoryservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.ecommerce.inventoryservice.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
}
