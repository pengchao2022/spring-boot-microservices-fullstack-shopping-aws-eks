package com.ecommerce.order.controller;

import com.ecommerce.order.dto.request.AddToCartRequest;
import com.ecommerce.order.dto.request.UpdateCartItemRequest;
import com.ecommerce.order.dto.response.CartResponse;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Transactional
public class CartController {
    private final CartService cartService;
    
    @GetMapping("/items")
    public ResponseEntity<CartResponse> getCartItems(@RequestHeader("X-User-Id") Long userId) {
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cart);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(cart);
    }
    
    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}