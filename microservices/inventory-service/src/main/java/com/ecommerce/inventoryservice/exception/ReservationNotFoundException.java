package com.ecommerce.inventoryservice.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
    
    public ReservationNotFoundException(String reservationId, String orderId) {
        super(String.format("Reservation not found for reservation ID: %s and order ID: %s", 
                           reservationId, orderId));
    }
}
