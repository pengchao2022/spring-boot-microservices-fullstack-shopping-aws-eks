package com.ecommerce.inventoryservice.service.impl;

import com.ecommerce.inventoryservice.dto.InventoryRequest;
import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.ReserveStockRequest;
import com.ecommerce.inventoryservice.dto.StockReservationRequest;
import com.ecommerce.inventoryservice.dto.StockReservationResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.exception.InsufficientStockException;
import com.ecommerce.inventoryservice.exception.InventoryNotFoundException;
import com.ecommerce.inventoryservice.exception.ReservationNotFoundException;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.model.StockReservation;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.repository.StockReservationRepository;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository reservationRepository;

    @Override
    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {
        log.info("Creating inventory for variant ID: {}", request.getVariantId());
        
        // 检查是否已存在
        inventoryRepository.findByVariantId(request.getVariantId())
                .ifPresent(inventory -> {
                    throw new IllegalArgumentException("Inventory already exists for variant ID: " + request.getVariantId());
                });

        Inventory inventory = Inventory.builder()
                .variantId(request.getVariantId())
                .currentStock(request.getCurrentStock())
                .reservedStock(0)
                .availableStock(request.getCurrentStock())
                .minimumStockLevel(request.getMinimumStockLevel())
                .maximumStockLevel(request.getMaximumStockLevel())
                .reorderPoint(request.getReorderPoint())
                .isTracked(request.getIsTracked())
                .notes(request.getNotes())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Created inventory with ID: {} for variant ID: {}", savedInventory.getId(), request.getVariantId());
        
        return InventoryResponse.fromEntity(savedInventory);
    }

    @Override
    public InventoryResponse getInventoryByVariantId(Long variantId) {
        log.debug("Fetching inventory for variant ID: {}", variantId);
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));
                
        return InventoryResponse.fromEntity(inventory);
    }

    @Override
    public List<InventoryResponse> getAllInventories() {
        log.debug("Fetching all inventories");
        
        return inventoryRepository.findAll().stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long variantId, InventoryRequest request) {
        log.info("Updating inventory for variant ID: {}", variantId);
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));

        inventory.setMinimumStockLevel(request.getMinimumStockLevel());
        inventory.setMaximumStockLevel(request.getMaximumStockLevel());
        inventory.setReorderPoint(request.getReorderPoint());
        inventory.setIsTracked(request.getIsTracked());
        inventory.setNotes(request.getNotes());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Updated inventory for variant ID: {}", variantId);
        
        return InventoryResponse.fromEntity(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long variantId) {
        log.info("Deleting inventory for variant ID: {}", variantId);
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));
                
        inventoryRepository.delete(inventory);
        log.info("Deleted inventory for variant ID: {}", variantId);
    }

    @Override
    public List<InventoryResponse> getLowStockItems() {
        log.debug("Fetching low stock items");
        
        return inventoryRepository.findLowStockItems().stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getOutOfStockItems() {
        log.debug("Fetching out of stock items");
        
        return inventoryRepository.findOutOfStockItems().stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getAvailableStock(Long variantId) {
        log.debug("Fetching available stock for variant ID: {}", variantId);
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));
                
        return inventory.getAvailableStock();
    }

    @Override
    @Transactional
    public InventoryResponse increaseStock(Long variantId, StockUpdateRequest request) {
        log.info("Increasing stock for variant ID: {} by {}", variantId, request.getQuantity());
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));

        inventoryRepository.increaseStock(variantId, request.getQuantity());
        
        // 刷新实体
        Inventory updatedInventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));
                
        log.info("Increased stock for variant ID: {}. New stock: {}", variantId, updatedInventory.getCurrentStock());
        
        return InventoryResponse.fromEntity(updatedInventory);
    }

    @Override
    @Transactional
    public InventoryResponse decreaseStock(Long variantId, StockUpdateRequest request) {
        log.info("Decreasing stock for variant ID: {} by {}", variantId, request.getQuantity());
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));

        if (inventory.getAvailableStock() < request.getQuantity()) {
            throw new InsufficientStockException(variantId, request.getQuantity(), inventory.getAvailableStock());
        }

        int updatedRows = inventoryRepository.decreaseStock(variantId, request.getQuantity());
        if (updatedRows == 0) {
            throw new InsufficientStockException(variantId, request.getQuantity(), inventory.getAvailableStock());
        }

        // 刷新实体
        Inventory updatedInventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));
                
        log.info("Decreased stock for variant ID: {}. New stock: {}", variantId, updatedInventory.getCurrentStock());
        
        return InventoryResponse.fromEntity(updatedInventory);
    }

    @Override
    @Transactional
    public InventoryResponse restock(Long variantId, StockUpdateRequest request) {
        log.info("Restocking variant ID: {} with quantity: {}", variantId, request.getQuantity());
        
        return increaseStock(variantId, request);
    }

    @Override
    @Transactional
    public StockReservationResponse reserveStock(StockReservationRequest request) {
        log.info("Reserving stock for order: {}, variant: {}, quantity: {}", 
                 request.getOrderId(), request.getVariantId(), request.getQuantity());
        
        Inventory inventory = inventoryRepository.findByVariantId(request.getVariantId())
                .orElseThrow(() -> new InventoryNotFoundException(request.getVariantId()));

        // 检查可用库存
        if (inventory.getAvailableStock() < request.getQuantity()) {
            throw new InsufficientStockException(request.getVariantId(), request.getQuantity(), inventory.getAvailableStock());
        }

        // 预留库存
        int updatedRows = inventoryRepository.reserveStock(request.getVariantId(), request.getQuantity());
        if (updatedRows == 0) {
            throw new InsufficientStockException(request.getVariantId(), request.getQuantity(), inventory.getAvailableStock());
        }

        // 创建预留记录
        String reservationId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(request.getExpirationMinutes());

        StockReservation reservation = StockReservation.builder()
                .reservationId(reservationId)
                .variantId(request.getVariantId())
                .orderId(request.getOrderId())
                .quantity(request.getQuantity())
                .expiresAt(expiresAt)
                .status(StockReservation.ReservationStatus.PENDING)
                .build();

        StockReservation savedReservation = reservationRepository.save(reservation);
        log.info("Created reservation: {} for order: {}", reservationId, request.getOrderId());
        
        return StockReservationResponse.fromEntity(savedReservation);
    }

    @Override
    @Transactional
    public InventoryResponse reserveStock(ReserveStockRequest request) {
        log.info("Reserving stock for product: {}, quantity: {}, order: {}", 
                 request.getProductId(), request.getQuantity(), request.getOrderNumber());
        
        Long variantId = request.getProductId(); // 使用 productId 作为 variantId
        
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));

        // 检查可用库存
        if (inventory.getAvailableStock() < request.getQuantity()) {
            throw new InsufficientStockException(variantId, request.getQuantity(), inventory.getAvailableStock());
        }

        // 预留库存
        int updatedRows = inventoryRepository.reserveStock(variantId, request.getQuantity());
        if (updatedRows == 0) {
            throw new InsufficientStockException(variantId, request.getQuantity(), inventory.getAvailableStock());
        }

        // 创建预留记录
        String reservationId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30); // 默认30分钟过期

        StockReservation reservation = StockReservation.builder()
                .reservationId(reservationId)
                .variantId(variantId)
                .orderId(request.getOrderNumber() != null ? request.getOrderNumber() : "TEMP_ORDER_" + System.currentTimeMillis())
                .quantity(request.getQuantity())
                .expiresAt(expiresAt)
                .status(StockReservation.ReservationStatus.PENDING)
                .build();

        reservationRepository.save(reservation);
        log.info("Created reservation: {} for order: {}", reservationId, 
                 request.getOrderNumber() != null ? request.getOrderNumber() : "TEMP_ORDER");

        // 刷新实体并返回库存响应
        Inventory updatedInventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new InventoryNotFoundException(variantId));
                
        log.info("Reserved stock for product ID: {}. Reserved: {}, Available: {}", 
                 variantId, updatedInventory.getReservedStock(), updatedInventory.getAvailableStock());
        
        return InventoryResponse.fromEntity(updatedInventory);
    }

    @Override
    @Transactional
    public StockReservationResponse confirmReservation(String reservationId) {
        log.info("Confirming reservation: {}", reservationId);
        
        StockReservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));

        if (reservation.isExpired()) {
            reservation.setStatus(StockReservation.ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            throw new IllegalArgumentException("Reservation has expired: " + reservationId);
        }

        if (reservation.getStatus() != StockReservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Reservation is not in PENDING status: " + reservationId);
        }

        // 确认预留，扣减实际库存
        int updatedRows = inventoryRepository.confirmReservation(reservation.getVariantId(), reservation.getQuantity());
        if (updatedRows == 0) {
            throw new InsufficientStockException(reservation.getVariantId(), reservation.getQuantity(), 0);
        }

        reservation.setStatus(StockReservation.ReservationStatus.CONFIRMED);
        StockReservation confirmedReservation = reservationRepository.save(reservation);
        
        log.info("Confirmed reservation: {}", reservationId);
        return StockReservationResponse.fromEntity(confirmedReservation);
    }

    @Override
    @Transactional
    public StockReservationResponse cancelReservation(String reservationId) {
        log.info("Cancelling reservation: {}", reservationId);
        
        StockReservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));

        if (reservation.getStatus() == StockReservation.ReservationStatus.PENDING) {
            // 释放预留库存
            inventoryRepository.releaseReservedStock(reservation.getVariantId(), reservation.getQuantity());
        }

        reservation.setStatus(StockReservation.ReservationStatus.CANCELLED);
        StockReservation cancelledReservation = reservationRepository.save(reservation);
        
        log.info("Cancelled reservation: {}", reservationId);
        return StockReservationResponse.fromEntity(cancelledReservation);
    }

    @Override
    @Transactional
    public void cancelReservationsByOrderId(String orderId) {
        log.info("Cancelling all reservations for order: {}", orderId);
        
        List<StockReservation> reservations = reservationRepository.findByOrderId(orderId);
        
        for (StockReservation reservation : reservations) {
            if (reservation.getStatus() == StockReservation.ReservationStatus.PENDING) {
                inventoryRepository.releaseReservedStock(reservation.getVariantId(), reservation.getQuantity());
                reservation.setStatus(StockReservation.ReservationStatus.CANCELLED);
            }
        }
        
        reservationRepository.saveAll(reservations);
        log.info("Cancelled {} reservations for order: {}", reservations.size(), orderId);
    }

    @Override
    public List<StockReservationResponse> getReservationsByOrderId(String orderId) {
        log.debug("Fetching reservations for order: {}", orderId);
        
        return reservationRepository.findByOrderId(orderId).stream()
                .map(StockReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> checkStockAvailability(Map<Long, Integer> variantQuantities) {
        log.debug("Checking stock availability for {} variants", variantQuantities.size());
        
        Map<Long, Integer> availability = new HashMap<>();
        
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            Long variantId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            
            try {
                Inventory inventory = inventoryRepository.findByVariantId(variantId)
                        .orElseThrow(() -> new InventoryNotFoundException(variantId));
                        
                availability.put(variantId, 
                    inventory.getAvailableStock() >= requestedQuantity ? requestedQuantity : inventory.getAvailableStock());
            } catch (InventoryNotFoundException e) {
                availability.put(variantId, 0);
            }
        }
        
        return availability;
    }

    @Override
    @Transactional
    public void processBatchReservations(String orderId, Map<Long, Integer> variantQuantities) {
        log.info("Processing batch reservations for order: {}", orderId);
        
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            StockReservationRequest request = StockReservationRequest.builder()
                    .orderId(orderId)
                    .variantId(entry.getKey())
                    .quantity(entry.getValue())
                    .build();
                    
            reserveStock(request);
        }
    }

    @Override
    @Transactional
    public void confirmBatchReservations(String orderId) {
        log.info("Confirming batch reservations for order: {}", orderId);
        
        List<StockReservation> reservations = reservationRepository.findByOrderId(orderId);
        
        for (StockReservation reservation : reservations) {
            if (reservation.getStatus() == StockReservation.ReservationStatus.PENDING) {
                confirmReservation(reservation.getReservationId());
            }
        }
    }

    @Override
    @Transactional
    public void cancelBatchReservations(String orderId) {
        log.info("Cancelling batch reservations for order: {}", orderId);
        cancelReservationsByOrderId(orderId);
    }

    @Override
    @Transactional
    public void cleanupExpiredReservations() {
        log.info("Cleaning up expired reservations");
        
        int expiredCount = reservationRepository.expireOldReservations();
        
        if (expiredCount > 0) {
            // 释放已过期的预留库存
            List<StockReservation> expiredReservations = reservationRepository
                    .findByStatus(StockReservation.ReservationStatus.EXPIRED);
                    
            for (StockReservation reservation : expiredReservations) {
                inventoryRepository.releaseReservedStock(reservation.getVariantId(), reservation.getQuantity());
            }
            
            log.info("Cleaned up {} expired reservations", expiredCount);
        }
    }
}