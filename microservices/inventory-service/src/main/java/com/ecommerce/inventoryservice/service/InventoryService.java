package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.InventoryRequest;
import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.StockReservationRequest;
import com.ecommerce.inventoryservice.dto.StockReservationResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.dto.ReserveStockRequest;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    
    // 库存管理
    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse getInventoryByVariantId(Long variantId);
    List<InventoryResponse> getAllInventories();
    InventoryResponse updateInventory(Long variantId, InventoryRequest request);
    void deleteInventory(Long variantId);
    
    // 库存查询
    List<InventoryResponse> getLowStockItems();
    List<InventoryResponse> getOutOfStockItems();
    Integer getAvailableStock(Long variantId);
    
    // 库存操作
    InventoryResponse increaseStock(Long variantId, StockUpdateRequest request);
    InventoryResponse decreaseStock(Long variantId, StockUpdateRequest request);
    InventoryResponse restock(Long variantId, StockUpdateRequest request);
    
    // 库存预留管理
    StockReservationResponse reserveStock(StockReservationRequest request);
    InventoryResponse reserveStock(ReserveStockRequest request);
    StockReservationResponse confirmReservation(String reservationId);
    StockReservationResponse cancelReservation(String reservationId);
    void cancelReservationsByOrderId(String orderId);
    List<StockReservationResponse> getReservationsByOrderId(String orderId);
    
    // 批量操作
    Map<Long, Integer> checkStockAvailability(Map<Long, Integer> variantQuantities);
    void processBatchReservations(String orderId, Map<Long, Integer> variantQuantities);
    void confirmBatchReservations(String orderId);
    void cancelBatchReservations(String orderId);
    
    // 维护任务
    void cleanupExpiredReservations();
}