package com.ecommerce.order.service;

import com.ecommerce.order.dto.request.OrderItemRequest;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    
    private final OrderItemRepository orderItemRepository;
    
    public OrderItem createOrderItem(Order order, OrderItemRequest request) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(request.getProductId());
        orderItem.setProductName(request.getProductName());
        orderItem.setSku(request.getSku());
        orderItem.setUnitPrice(request.getUnitPrice());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setImageUrl(request.getImageUrl());
        orderItem.setWeight(request.getWeight());
        orderItem.setIsDigital(request.getIsDigital());
        
        return orderItemRepository.save(orderItem);
    }
}
