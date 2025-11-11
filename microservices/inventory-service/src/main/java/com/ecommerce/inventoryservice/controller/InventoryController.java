package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.InventoryRequest;
import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.ReserveStockRequest;
import com.ecommerce.inventoryservice.dto.StockReservationRequest;
import com.ecommerce.inventoryservice.dto.StockReservationResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

// 添加这些注解
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // 确保每个方法都有对应的注解
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createInventory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<InventoryResponse> getInventoryByVariantId(@PathVariable Long variantId) {
        InventoryResponse response = inventoryService.getInventoryByVariantId(variantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        List<InventoryResponse> responses = inventoryService.getAllInventories();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/variant/{variantId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long variantId, 
            @Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.updateInventory(variantId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/variant/{variantId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long variantId) {
        inventoryService.deleteInventory(variantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockItems() {
        List<InventoryResponse> responses = inventoryService.getLowStockItems();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryResponse>> getOutOfStockItems() {
        List<InventoryResponse> responses = inventoryService.getOutOfStockItems();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/variant/{variantId}/available")
    public ResponseEntity<Map<String, Integer>> getAvailableStock(@PathVariable Long variantId) {
        Integer availableStock = inventoryService.getAvailableStock(variantId);
        return ResponseEntity.ok(Map.of("availableStock", availableStock));
    }

    @PostMapping("/variant/{variantId}/increase")
    public ResponseEntity<InventoryResponse> increaseStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequest request) {
        InventoryResponse response = inventoryService.increaseStock(variantId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/variant/{variantId}/decrease")
    public ResponseEntity<InventoryResponse> decreaseStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequest request) {
        InventoryResponse response = inventoryService.decreaseStock(variantId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/variant/{variantId}/restock")
    public ResponseEntity<InventoryResponse> restock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockUpdateRequest request) {
        InventoryResponse response = inventoryService.restock(variantId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-availability")
    public ResponseEntity<Map<Long, Integer>> checkStockAvailability(@RequestBody Map<Long, Integer> variantQuantities) {
        Map<Long, Integer> availability = inventoryService.checkStockAvailability(variantQuantities);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredReservations() {
        inventoryService.cleanupExpiredReservations();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        InventoryResponse response = inventoryService.reserveStock(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}