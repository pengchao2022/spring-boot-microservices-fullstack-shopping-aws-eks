package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.StockReservationRequest;
import com.ecommerce.inventoryservice.dto.StockReservationResponse;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/reservations")
@RequiredArgsConstructor
public class StockReservationController {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ResponseEntity<StockReservationResponse> reserveStock(@Valid @RequestBody StockReservationRequest request) {
        StockReservationResponse response = inventoryService.reserveStock(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<StockReservationResponse> confirmReservation(@PathVariable String reservationId) {
        StockReservationResponse response = inventoryService.confirmReservation(reservationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<StockReservationResponse> cancelReservation(@PathVariable String reservationId) {
        StockReservationResponse response = inventoryService.cancelReservation(reservationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<StockReservationResponse>> getReservationsByOrderId(@PathVariable String orderId) {
        List<StockReservationResponse> responses = inventoryService.getReservationsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> cancelReservationsByOrderId(@PathVariable String orderId) {
        inventoryService.cancelReservationsByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch/reserve")
    public ResponseEntity<Void> processBatchReservations(
            @RequestParam String orderId,
            @RequestBody Map<Long, Integer> variantQuantities) {
        inventoryService.processBatchReservations(orderId, variantQuantities);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/confirm")
    public ResponseEntity<Void> confirmBatchReservations(@RequestParam String orderId) {
        inventoryService.confirmBatchReservations(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/cancel")
    public ResponseEntity<Void> cancelBatchReservations(@RequestParam String orderId) {
        inventoryService.cancelBatchReservations(orderId);
        return ResponseEntity.ok().build();
    }
}