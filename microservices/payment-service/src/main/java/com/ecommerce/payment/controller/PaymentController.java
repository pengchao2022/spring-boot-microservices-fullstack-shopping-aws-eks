package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.request.CreatePaymentRequest;
import com.ecommerce.payment.dto.request.RefundRequest;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.RefundResponse;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/alipay/callback")
    public ResponseEntity<String> handleAlipayCallback(@RequestParam Map<String, String> params) {
        try {
            paymentService.handleAlipayCallback(params);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("fail");
        }
    }
    
    @PostMapping("/alipay/notify")
    public ResponseEntity<String> handleAlipayNotify(@RequestBody String notifyData) {
        try {
            boolean success = paymentService.handleAlipayNotify(notifyData);
            if (success) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.badRequest().body("fail");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("fail");
        }
    }
    
    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> refundPayment(@Valid @RequestBody RefundRequest request) {
        RefundResponse response = paymentService.refundPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{orderNumber}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String orderNumber) {
        PaymentResponse response = paymentService.getPaymentByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{orderNumber}/status")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String orderNumber) {
        PaymentResponse response = paymentService.getPaymentStatus(orderNumber);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{orderNumber}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String orderNumber) {
        PaymentResponse response = paymentService.cancelPayment(orderNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        Map<String, Object> statistics = paymentService.getPaymentStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "payment-service");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}