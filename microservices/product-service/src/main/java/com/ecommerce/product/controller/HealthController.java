package com.ecommerce.product.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "*")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.application.name:product-service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            healthInfo.put("application", applicationName);
            healthInfo.put("port", serverPort);
            healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            healthInfo.put("status", "UP");
            
            boolean dbConnected = checkDatabaseConnection();
            healthInfo.put("database", dbConnected ? "CONNECTED" : "DISCONNECTED");
            
            log.info("Health check passed - application: {}", applicationName);
            
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
        }

        return healthInfo;
    }

    @GetMapping("/ready")
    public Map<String, Object> readiness() {
        Map<String, Object> readiness = new HashMap<>();
        
        try {
            boolean dbReady = checkDatabaseConnection();
            
            readiness.put("status", dbReady ? "READY" : "NOT_READY");
            readiness.put("database", dbReady ? "READY" : "NOT_READY");
            readiness.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            log.info("Readiness check: {}", readiness.get("status"));

        } catch (Exception e) {
            log.error("Readiness check failed: {}", e.getMessage());
            readiness.put("status", "NOT_READY");
            readiness.put("error", e.getMessage());
        }

        return readiness;
    }

    @GetMapping("/live")
    public Map<String, Object> liveness() {
        Map<String, Object> liveness = new HashMap<>();
        
        try {
            liveness.put("status", "ALIVE");
            liveness.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            liveness.put("uptime", "N/A");

            log.debug("Liveness check passed");

        } catch (Exception e) {
            log.error("Liveness check failed: {}", e.getMessage());
            liveness.put("status", "DEAD");
            liveness.put("error", e.getMessage());
        }

        return liveness;
    }

    private boolean checkDatabaseConnection() {
        try {
            Boolean result = jdbcTemplate.execute("SELECT 1", (PreparedStatementCallback<Boolean>) preparedStatement -> {
                try (var resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            });
            return result != null && result;
        } catch (Exception e) {
            log.warn("Database connection check failed: {}", e.getMessage());
            return false;
        }
    }
}