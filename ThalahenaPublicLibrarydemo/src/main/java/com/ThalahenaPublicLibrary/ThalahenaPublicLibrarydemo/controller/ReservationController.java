package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ReservationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.ReservationStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Reservation Controller - Handles HTTP requests for reservation management
 * 
 * SRP: Only handles HTTP request/response, delegates to ReservationService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on ReservationService abstraction, not concrete implementation
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/api/reservations", "/api/staff/reservations"})
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;

    /**
     * GET /api/staff/reservations or /api/reservations
     * Fetch all reservations (read-only list for staff)
     */
    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/staff/reservations/{id}
     * Get single reservation by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            ReservationDTO reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * PATCH /api/staff/reservations/{id}/acknowledge
     * Mark reservation as processed (acknowledged by staff)
     * If status is APPROVED, auto-issues the book
     */
    @PatchMapping("/{id}/acknowledge")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> acknowledgeReservation(@PathVariable Long id) {
        try {
            ReservationDTO reservation = reservationService.acknowledgeReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error acknowledging reservation: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/staff/reservations/{id}/status
     * Change reservation status (Approve / Reject / Available / Unavailable)
     * 
     * Payload: { "status": "APPROVED" | "REJECTED" | "AVAILABLE" | "UNAVAILABLE" }
     * 
     * Rules:
     * - APPROVED triggers auto-issuing (creates borrow transaction)
     * - REJECTED marks as processed without issuing
     * - Cannot modify COMPLETED or CANCELLED reservations
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> changeReservationStatus(@PathVariable Long id,
                                                      @RequestBody Map<String, String> payload) {
        try {
            String statusStr = payload.get("status");
            if (statusStr == null || statusStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Status field is required"));
            }

            // Pass the raw status string to the service, which handles validation and conversion
            ReservationDTO updated = reservationService.updateStatus(id, statusStr);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid status: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating reservation status: " + e.getMessage()));
        }
    }

    /**
     * POST /api/reservations
     * Create a new reservation (accessible to all authenticated users)
     */
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationDTO reservationDTO) {
        try {
            ReservationDTO created = reservationService.createReservation(reservationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating reservation: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/user/{userId}
     * Fetch reservations for a specific user (accessible to all authenticated users)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUserId(@PathVariable Long userId) {
        try {
            List<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching reservations: " + e.getMessage()));
        }
    }
}
