package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ReservationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Reservation Controller - Handles HTTP requests for reservation management
 * 
 * SRP: Only handles HTTP request/response, delegates to ReservationService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on ReservationService abstraction, not concrete implementation
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff/reservations")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;

    /**
     * GET /api/staff/reservations
     * Fetch all reservations (read-only list for staff)
     */
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/staff/reservations/{id}
     * Get single reservation by ID
     */
    @GetMapping("/{id}")
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
     */
    @PatchMapping("/{id}/acknowledge")
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
}
