package com.ecommerce.order.service;

import com.ecommerce.order.config.InventoryConfig;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.dto.request.CreateOrderRequest;
import com.ecommerce.order.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.model.*;
import com.ecommerce.order.model.enums.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final OrderItemService orderItemService;
    private final RabbitMQService rabbitMQService;
    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    private final InventoryConfig inventoryConfig;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}, guest: {}", request.getUserId(), request.getGuestEmail());
        
        if (request.getUserId() == null && request.getGuestEmail() == null) {
            throw new RuntimeException("Either user ID or guest email is required");
        }
        
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setGuestEmail(request.getGuestEmail());
        order.setShippingAddress(request.getShippingAddress());
        
        if (request.getBillingAddress() != null) {
            order.setBillingAddress(request.getBillingAddress());
        } else {
            order.setBillingAddress(request.getShippingAddress());
        }
        
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingMethod(request.getShippingMethod());
        order.setNotes(request.getNotes());
        order.setCurrency(request.getCurrency());
        order.setShippingAmount(request.getShippingAmount());
        order.setTaxAmount(request.getTaxAmount());
        order.setDiscountAmount(request.getDiscountAmount());
        
        calculateOrderTotals(order, request.getItems());
        Order savedOrder = orderRepository.save(order);
        
        for (var itemRequest : request.getItems()) {
            OrderItem orderItem = orderItemService.createOrderItem(savedOrder, itemRequest);
            savedOrder.addOrderItem(orderItem);
        }
        
        addStatusHistory(savedOrder, OrderStatus.PENDING, "订单创建成功");
        
        if (inventoryConfig.isReservationAllowed()) {
            log.debug("Inventory reservation is enabled, proceeding with inventory operations");
            reserveInventory(savedOrder);
        } else {
            log.info("Inventory reservation is disabled via configuration, skipping inventory operations");
        }
        
        initializePayment(savedOrder);
        
        log.info("Order created successfully: {}", savedOrder.getOrderNumber());
        return mapToOrderResponse(savedOrder);
    }
    
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }
    
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        return mapToOrderResponse(order);
    }
    
    public Page<OrderResponse> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToOrderResponse);
    }
    
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(this::mapToOrderResponse);
    }
    
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToOrderResponse);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(request.getStatus());
        updateOrderTimestamps(order, request.getStatus());
        
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }
        
        Order updatedOrder = orderRepository.save(order);
        addStatusHistory(updatedOrder, request.getStatus(), request.getNotes());
        
        log.info("Order status updated: {} -> {}", previousStatus, request.getStatus());
        return mapToOrderResponse(updatedOrder);
    }
    
    @Transactional
    public OrderResponse cancelOrder(Long id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.cancel(reason);
        Order cancelledOrder = orderRepository.save(order);
        addStatusHistory(cancelledOrder, OrderStatus.CANCELLED, "订单取消: " + reason);
        
        if (inventoryConfig.isInventoryOperationsEnabled()) {
            releaseInventory(cancelledOrder);
        } else {
            log.debug("Inventory operations disabled, skipping inventory release");
        }
        
        cancelPayment(cancelledOrder);
        
        log.info("Order cancelled: {}", cancelledOrder.getOrderNumber());
        return mapToOrderResponse(cancelledOrder);
    }
    
    public Map<String, Object> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalRevenue = orderRepository.getTotalRevenueByDateRange(startDate, endDate);
        Long totalOrders = orderRepository.countOrdersByDateRange(startDate, endDate);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        stats.put("totalOrders", totalOrders != null ? totalOrders : 0L);
        stats.put("startDate", startDate);
        stats.put("endDate", endDate);
        
        return stats;
    }
    
    private void calculateOrderTotals(Order order, java.util.List<com.ecommerce.order.dto.request.OrderItemRequest> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var item : items) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }
        order.setSubtotalAmount(subtotal);
        BigDecimal total = subtotal.add(order.getTaxAmount()).add(order.getShippingAmount()).subtract(order.getDiscountAmount());
        order.setTotalAmount(total);
    }
    
    private void addStatusHistory(Order order, OrderStatus status, String notes) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setNotes(notes);
        statusHistoryRepository.save(history);
    }
    
    private void updateOrderTimestamps(Order order, OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case CONFIRMED: order.setConfirmedAt(now); break;
            case SHIPPED: order.setShippedAt(now); break;
            case DELIVERED: order.setDeliveredAt(now); break;
        }
    }
    
    private void reserveInventory(Order order) {
        if (!inventoryConfig.isReservationAllowed()) {
            log.debug("Inventory reservation is disabled, skipping");
            return;
        }
        
        log.info("Reserving inventory for order: {}", order.getOrderNumber());
        
        for (OrderItem item : order.getOrderItems()) {
            try {
                Map<String, Object> request = new HashMap<>();
                request.put("productId", item.getProductId());
                request.put("quantity", item.getQuantity());
                request.put("orderNumber", order.getOrderNumber());
                
                inventoryClient.reserveStock(request);
                log.debug("Successfully reserved inventory for product: {}", item.getProductId());
                
            } catch (Exception e) {
                log.error("Failed to reserve inventory for product {}: {}", item.getProductId(), e.getMessage());
                
                if (inventoryConfig.isValidationEnabled()) {
                    throw new RuntimeException("Inventory reservation failed for product: " + item.getProductId());
                } else {
                    log.warn("Inventory validation is disabled, continuing despite reservation failure");
                }
            }
        }
    }
    
    private void releaseInventory(Order order) {
        if (!inventoryConfig.isInventoryOperationsEnabled()) {
            log.debug("Inventory operations are disabled, skipping release");
            return;
        }
        
        log.info("Releasing inventory for order: {}", order.getOrderNumber());
        
        for (OrderItem item : order.getOrderItems()) {
            try {
                Map<String, Object> request = new HashMap<>();
                request.put("productId", item.getProductId());
                request.put("quantity", item.getQuantity());
                request.put("orderNumber", order.getOrderNumber());
                
                inventoryClient.releaseStock(request);
                log.debug("Successfully released inventory for product: {}", item.getProductId());
                
            } catch (Exception e) {
                log.error("Failed to release inventory for product {}: {}", item.getProductId(), e.getMessage());
            }
        }
    }
    
    private void initializePayment(Order order) {
        try {
            Map<String, Object> paymentRequest = new HashMap<>();
            paymentRequest.put("orderNumber", order.getOrderNumber());
            paymentRequest.put("amount", order.getTotalAmount());
            paymentRequest.put("subject", "果蔬订单支付 - " + order.getOrderNumber());
            paymentClient.createPayment(paymentRequest);
        } catch (Exception e) {
            log.error("Failed to initialize payment for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }
    
    private void cancelPayment(Order order) {
        try {
            paymentClient.cancelPayment(order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to cancel payment for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setGuestEmail(order.getGuestEmail());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setSubtotalAmount(order.getSubtotalAmount());
        response.setTaxAmount(order.getTaxAmount());
        response.setShippingAmount(order.getShippingAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setCurrency(order.getCurrency());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentId(order.getPaymentId());
        response.setShippingMethod(order.getShippingMethod());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setNotes(order.getNotes());
        response.setCancellationReason(order.getCancellationReason());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setConfirmedAt(order.getConfirmedAt());
        response.setShippedAt(order.getShippedAt());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCancelledAt(order.getCancelledAt());
        response.setGuestOrder(order.isGuestOrder());
        return response;
    }
}