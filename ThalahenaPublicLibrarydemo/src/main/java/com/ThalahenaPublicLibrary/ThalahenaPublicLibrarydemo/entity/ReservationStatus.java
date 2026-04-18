package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

/**
 * Reservation Status Enum
 * 
 * OCP (Open/Closed Principle):
 * - Open for extension: Can add new statuses (e.g., EXPIRED, ON_HOLD)
 * - Closed for modification: Existing statuses won't change
 * 
 * Values:
 * - PENDING: Reservation is waiting to be fulfilled
 * - AVAILABLE: Book is available for pickup
 * - UNAVAILABLE: Book is not currently available
 * - COMPLETED: Reservation has been fulfilled
 * - CANCELLED: Reservation was cancelled
 */
public enum ReservationStatus {
    PENDING,
    AVAILABLE,
    UNAVAILABLE,
    COMPLETED,
    CANCELLED
}
