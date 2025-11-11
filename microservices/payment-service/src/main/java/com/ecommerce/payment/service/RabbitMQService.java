package com.ecommerce.payment.service;

import com.ecommerce.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void sendPaymentStatusUpdate(Payment payment, String tradeStatus) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderNumber", payment.getOrderNumber());
            event.put("paymentStatus", payment.getStatus());
            event.put("tradeStatus", tradeStatus);
            event.put("alipayTradeNo", payment.getAlipayTradeNo());
            event.put("amount", payment.getAmount());
            event.put("updatedAt", payment.getUpdatedAt());
            
            rabbitTemplate.convertAndSend("payment.exchange", "payment.status.update", event);
            log.info("Payment status update event sent: {}", payment.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to send payment status update event: {}", e.getMessage());
        }
    }
}