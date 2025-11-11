package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "payment-service", url = "${app.payment-service.url}")
public interface PaymentClient {
    
    @PostMapping("/api/payments/create")
    Map<String, Object> createPayment(@RequestBody Map<String, Object> paymentRequest);
    
    @PostMapping("/api/payments/{orderNumber}/cancel")
    Map<String, Object> cancelPayment(@RequestBody String orderNumber);
}
