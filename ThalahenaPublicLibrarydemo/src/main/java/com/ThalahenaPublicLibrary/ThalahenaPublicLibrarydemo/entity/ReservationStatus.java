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
 * - APPROVED: Staff has approved the reservation, auto-issue triggered
 * - AVAILABLE: Book is available for pickup
 * - UNAVAILABLE: Book is not currently available
 * - REJECTED: Staff has rejected the reservation
 * - COMPLETED: Reservation has been fulfilled (book issued)
 * - CANCELLED: Reservation was cancelled (manually or by expiry)
 */
public enum ReservationStatus {
    PENDING,
    APPROVED,
    AVAILABLE,
    UNAVAILABLE,
    REJECTED,
    COMPLETED,
    CANCELLED
}
