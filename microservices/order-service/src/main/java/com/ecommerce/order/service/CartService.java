package com.ecommerce.order.service;

import com.ecommerce.order.dto.request.AddToCartRequest;
import com.ecommerce.order.dto.request.UpdateCartItemRequest;
import com.ecommerce.order.dto.response.CartItemResponse;
import com.ecommerce.order.dto.response.CartResponse;
import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 在类级别添加事务注解
public class CartService {
    private final CartRepository cartRepository;
    // 移除了未使用的 cartItemRepository
    
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId);
                    return cartRepository.save(newCart);
                });
    }
    
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToResponse(cart);
    }
    
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        
        // 检查商品是否已存在
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // 更新数量
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // 添加新商品
            CartItem newItem = new CartItem(
                cart,
                request.getProductId(),
                request.getProductName(),
                request.getImageUrl(),
                request.getPrice(),
                request.getQuantity(),
                request.getWeight()
            );
            cart.addItem(newItem);
        }
        
        Cart savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }
    
    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        if (request.getQuantity() <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(request.getQuantity());
        }
        
        Cart savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }
    
    public CartResponse removeCartItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            Cart savedCart = cartRepository.save(cart);
            return convertToResponse(savedCart);
        }
        throw new RuntimeException("购物车商品不存在");
    }
    
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
    private CartResponse convertToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());
        
        Integer totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        return new CartResponse(
            cart.getId(),
            cart.getUserId(),
            itemResponses,
            totalItems
        );
    }
    
    private CartItemResponse convertToItemResponse(CartItem item) {
        return new CartItemResponse(
            item.getId(),
            item.getProductId(),
            item.getProductName(),
            item.getImageUrl(),
            item.getPrice(),
            item.getQuantity(),
            item.getWeight()
        );
    }
}