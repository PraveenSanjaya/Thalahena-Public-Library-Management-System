package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ReservationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Book;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Reservation;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.ReservationStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.ReservationRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.IssuanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Reservation Service - Handles all business logic for reservation management
 * 
 * SRP: This service ONLY handles reservation business logic
 * OCP: New workflow actions (auto-issue, expiry) added without modifying existing methods
 * DIP: Depends on repository and IssuanceService abstractions injected by Spring
 */
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private IssuanceService issuanceService;


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
     * Get reservations for a specific user ID
     */
    public List<ReservationDTO> getReservationsByUserId(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdWithDetails(userId);
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new reservation
     */
    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        if (reservationDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (reservationDTO.getBookId() == null) {
            throw new IllegalArgumentException("Book ID cannot be empty");
        }

        User user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reservationDTO.getUserId()));

        Book book = bookRepository.findById(reservationDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + reservationDTO.getBookId()));

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservationDate(java.time.LocalDateTime.now())
                .status(ReservationStatus.PENDING)
                .processed(false)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDTO(savedReservation);
    }

    /**
     * Acknowledge/Process a reservation
     * If status is APPROVED, auto-issues the book (creates borrow transaction)
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
        
        // If reservation is already AVAILABLE, auto‑issue the book
        if (reservation.getStatus() == ReservationStatus.AVAILABLE && issuanceService != null) {
            issuanceService.issueBookForReservation(reservation);
        }
        
        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDTO(savedReservation);
    }

    /**
     * Update reservation status (Approve / Reject / etc.)
     * 
     * Workflow rules:
     * - Cannot modify a COMPLETED or CANCELLED reservation
     * - If status is changed to APPROVED, auto-issue the book
     * - If status is changed to REJECTED or CANCELLED, mark as processed
     * 
     * @param reservationId the reservation ID
     * @param newStatusStr  the new status string
     * @return updated ReservationDTO
     */
    @Transactional
    public ReservationDTO updateStatus(Long reservationId, String newStatusStr) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationId));

        // Map UI-friendly status strings to enum values
        ReservationStatus newStatus;
        switch (newStatusStr.toUpperCase()) {
            case "APPROVED":
            case "AVAILABLE":
                newStatus = ReservationStatus.AVAILABLE;
                break;
            case "REJECTED":
            case "UNAVAILABLE":
                newStatus = ReservationStatus.UNAVAILABLE;
                break;
            case "COMPLETED":
                newStatus = ReservationStatus.COMPLETED;
                break;
            case "CANCELLED":
                newStatus = ReservationStatus.CANCELLED;
                break;
            default:
                throw new IllegalArgumentException("Invalid status value: " + newStatusStr);
        }

        // Prevent modifying finished reservations
        if (reservation.getStatus() == ReservationStatus.COMPLETED ||
                reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify a finished reservation");
        }

        reservation.setStatus(newStatus);
        // Auto‑issue when reservation becomes AVAILABLE
        if (newStatus == ReservationStatus.AVAILABLE) {
            if (issuanceService != null) {
                issuanceService.issueBookForReservation(reservation);
            }
        }
        
        // Rejected/cancelled reservations are marked as processed so they won't appear in pending queue
        if (newStatus == ReservationStatus.UNAVAILABLE || newStatus == ReservationStatus.CANCELLED) {
            reservation.setProcessed(true);
        }
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
                .expiryDate(reservation.getExpiryDate())
                .status(reservation.getStatus() != null ? reservation.getStatus().toString() : null)
                .processed(reservation.getProcessed() != null ? reservation.getProcessed() : false)
                .build();
    }
}
