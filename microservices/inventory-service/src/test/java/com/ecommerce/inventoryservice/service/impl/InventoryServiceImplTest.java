package com.ecommerce.inventoryservice.service.impl;

import com.ecommerce.inventoryservice.dto.InventoryRequest;
import com.ecommerce.inventoryservice.dto.StockReservationRequest;
import com.ecommerce.inventoryservice.exception.InsufficientStockException;
import com.ecommerce.inventoryservice.exception.InventoryNotFoundException;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.repository.StockReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockReservationRepository reservationRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;
    private InventoryRequest inventoryRequest;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id(1L)
                .variantId(100L)
                .currentStock(50)
                .reservedStock(10)
                .availableStock(40)
                .minimumStockLevel(5)
                .maximumStockLevel(100)
                .reorderPoint(10)
                .isTracked(true)
                .build();

        inventoryRequest = InventoryRequest.builder()
                .variantId(100L)
                .currentStock(50)
                .minimumStockLevel(5)
                .maximumStockLevel(100)
                .reorderPoint(10)
                .isTracked(true)
                .build();
    }

    @Test
    void createInventory_Success() {
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        var response = inventoryService.createInventory(inventoryRequest);

        assertNotNull(response);
        assertEquals(100L, response.getVariantId());
        assertEquals(50, response.getCurrentStock());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void getInventoryByVariantId_Success() {
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.of(inventory));

        var response = inventoryService.getInventoryByVariantId(100L);

        assertNotNull(response);
        assertEquals(100L, response.getVariantId());
        verify(inventoryRepository).findByVariantId(100L);
    }

    @Test
    void getInventoryByVariantId_NotFound() {
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.getInventoryByVariantId(100L);
        });
    }

    @Test
    void reserveStock_Success() {
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.reserveStock(100L, 5)).thenReturn(1);

        var request = StockReservationRequest.builder()
                .orderId("ORDER123")
                .variantId(100L)
                .quantity(5)
                .build();

        var response = inventoryService.reserveStock(request);

        assertNotNull(response);
        assertNotNull(response.getReservationId());
        verify(inventoryRepository).reserveStock(100L, 5);
    }

    @Test
    void reserveStock_InsufficientStock() {
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.of(inventory));

        var request = StockReservationRequest.builder()
                .orderId("ORDER123")
                .variantId(100L)
                .quantity(100) // 超过可用库存
                .build();

        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.reserveStock(request);
        });
    }
}
