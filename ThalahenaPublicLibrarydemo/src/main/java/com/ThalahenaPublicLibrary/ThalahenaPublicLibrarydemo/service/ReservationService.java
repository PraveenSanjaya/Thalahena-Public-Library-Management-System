package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ReservationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Reservation;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Reservation Service - Handles all business logic for reservation management
 * 
 * SRP (Single Responsibility Principle):
 * - This service ONLY handles reservation business logic
 * - Does NOT handle HTTP concerns (that's the controller's job)
 * - Does NOT handle database access directly (that's the repository's job)
 * - Single purpose: manage reservations and staff acknowledgment
 * 
 * OCP (Open/Closed Principle):
 * - Open for extension: Can add new reservation workflows
 * - Closed for modification: Existing methods won't need changes for new features
 * - Future: Can add reservation expiry, notifications, etc.
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on ReservationRepository abstraction, not concrete implementation
 * - Spring injects dependencies, we don't create them
 */
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Get all reservations (read-only view for staff)
     */
    public List<ReservationDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithDetails();
        
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Acknowledge/Process a reservation
     * SRP: Updates processed flag to mark reservation as handled
     */
    @Transactional
    public ReservationDTO acknowledgeReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        // Validation: Prevent double processing
        if (reservation.getProcessed()) {
            throw new IllegalStateException("Reservation has already been processed");
        }

        // Mark as processed
        reservation.setProcessed(true);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDTO(savedReservation);
    }

    /**
     * Get reservation by ID
     */
    public ReservationDTO getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));
        return convertToDTO(reservation);
    }

    /**
     * Convert Reservation entity to DTO
     */
    private ReservationDTO convertToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .memberName(reservation.getUser() != null ? reservation.getUser().getUsername() : null)
                .memberEmail(reservation.getUser() != null ? reservation.getUser().getEmail() : null)
                .bookId(reservation.getBook() != null ? reservation.getBook().getId() : null)
                .bookTitle(reservation.getBook() != null ? reservation.getBook().getTitle() : null)
                .bookIsbn(reservation.getBook() != null ? reservation.getBook().getIsbn() : null)
                .reservationDate(reservation.getReservationDate())
                .status(reservation.getStatus() != null ? reservation.getStatus().toString() : null)
                .processed(reservation.getProcessed() != null ? reservation.getProcessed() : false)
                .build();
    }
}
