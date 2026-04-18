package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.FineDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.FineStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Fine Controller - Handles HTTP requests for fine management
 * 
 * SRP: Only handles HTTP request/response, delegates to FineService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on FineService abstraction, not concrete implementation
 */
@RestController
@RequestMapping("/api/staff/fines")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class FineController {

    @Autowired
    private FineService fineService;

    /**
     * GET /api/staff/fines?status=ALL|UNPAID|PAID|NONE
     * Get all fines with optional status filter
     */
    @GetMapping
    public ResponseEntity<List<FineDTO>> getAllFines(
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        List<FineDTO> fines = fineService.getAllFines(status);
        return ResponseEntity.ok(fines);
    }

    /**
     * GET /api/staff/fines/stats
     * Get fine statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getFineStats() {
        FineService.FineStatsDTO stats = fineService.getFineStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/staff/fines/{fineId}
     * Get fine by ID
     */
    @GetMapping("/{fineId}")
    public ResponseEntity<?> getFineById(@PathVariable Long fineId) {
        try {
            FineDTO fine = fineService.getFineById(fineId);
            return ResponseEntity.ok(fine);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/fines/{fineId}
     * Update fine (payment date, status)
     */
    @PutMapping("/{fineId}")
    public ResponseEntity<?> updateFine(
            @PathVariable Long fineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam(required = false) String status) {
        try {
            FineStatus newStatus = status != null ? FineStatus.valueOf(status.toUpperCase()) : null;
            FineDTO updatedFine = fineService.updateFine(fineId, paymentDate, newStatus);
            return ResponseEntity.ok(updatedFine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating fine: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/fines/{fineId}/pay
     * Mark fine as paid
     */
    @PutMapping("/{fineId}/pay")
    public ResponseEntity<?> markFineAsPaid(
            @PathVariable Long fineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate) {
        try {
            FineDTO updatedFine = fineService.markAsPaid(fineId, paymentDate);
            return ResponseEntity.ok(updatedFine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error marking fine as paid: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/staff/fines/{fineId}
     * Soft delete fine
     */
    @DeleteMapping("/{fineId}")
    public ResponseEntity<?> deleteFine(@PathVariable Long fineId) {
        try {
            fineService.deleteFine(fineId);
            return ResponseEntity.ok(new MessageResponse("Fine deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error deleting fine: " + e.getMessage()));
        }
    }
}
