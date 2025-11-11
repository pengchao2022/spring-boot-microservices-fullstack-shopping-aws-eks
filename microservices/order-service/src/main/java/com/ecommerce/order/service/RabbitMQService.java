package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
    
    public void sendOrderCreatedEvent(Order order) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderNumber", order.getOrderNumber());
            event.put("userId", order.getUserId());
            event.put("guestEmail", order.getGuestEmail());
            event.put("totalAmount", order.getTotalAmount());
            event.put("createdAt", order.getCreatedAt());
            
            rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
            log.info("Order created event sent: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to send order created event: {}", e.getMessage());
        }
    }
    
    public void sendOrderStatusUpdateEvent(Order order, OrderStatus previousStatus) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderNumber", order.getOrderNumber());
            event.put("previousStatus", previousStatus);
            event.put("currentStatus", order.getStatus());
            event.put("updatedAt", order.getUpdatedAt());
            
            rabbitTemplate.convertAndSend("order.exchange", "order.status.update", event);
            log.info("Order status update event sent: {} -> {}", previousStatus, order.getStatus());
        } catch (Exception e) {
            log.error("Failed to send order status update event: {}", e.getMessage());
        }
    }
    
    public void sendOrderCancelledEvent(Order order) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderNumber", order.getOrderNumber());
            event.put("cancellationReason", order.getCancellationReason());
            event.put("cancelledAt", order.getCancelledAt());
            
            rabbitTemplate.convertAndSend("order.exchange", "order.cancelled", event);
            log.info("Order cancelled event sent: {}", order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to send order cancelled event: {}", e.getMessage());
        }
    }
}
