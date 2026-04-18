package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.FineDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Fine;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.FineStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fine Service - Handles all business logic for fine management
 * 
 * SRP (Single Responsibility Principle):
 * - This service ONLY handles fine management business logic
 * - Does NOT handle HTTP concerns (that's the controller's job)
 * - Does NOT handle database access directly (that's the repository's job)
 * - Single purpose: validate, update, and manage fines
 * 
 * OCP (Open/Closed Principle):
 * - Open for extension: Can add new fine statuses or payment rules
 * - Closed for modification: Existing methods won't need changes for new features
 * - Future: Can add fine waiver logic, partial payments, etc.
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on FineRepository abstraction, not concrete implementation
 * - Spring injects dependencies, we don't create them
 */
@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    /**
     * Get all fines
     */
    public List<FineDTO> getAllFines(String statusFilter) {
        List<Fine> fines;
        
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase("ALL")) {
            FineStatus status = FineStatus.valueOf(statusFilter.toUpperCase());
            fines = fineRepository.findByStatus(status);
        } else {
            fines = fineRepository.findAllWithDetails();
        }
        
        return fines.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get fine by ID
     */
    public FineDTO getFineById(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found with ID: " + fineId));
        return convertToDTO(fine);
    }

    /**
     * Update fine (mark as paid, update payment date, etc.)
     * SRP: Validates and updates fine payment information
     */
    @Transactional
    public FineDTO updateFine(Long fineId, LocalDate paymentDate, FineStatus newStatus) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found with ID: " + fineId));

        // Validation: Status must be valid
        if (newStatus != null) {
            if (newStatus != FineStatus.PAID && newStatus != FineStatus.UNPAID && newStatus != FineStatus.NONE) {
                throw new IllegalArgumentException("Invalid status. Must be PAID, UNPAID, or NONE");
            }
        }

        // Validation: Payment date cannot be before return date
        if (paymentDate != null && fine.getReturnDate() != null) {
            if (paymentDate.isBefore(fine.getReturnDate())) {
                throw new IllegalArgumentException("Payment date cannot be before return date (" + fine.getReturnDate() + ")");
            }
        }

        // If marking as paid and no payment date provided, use today or return date
        if (newStatus == FineStatus.PAID) {
            if (paymentDate == null) {
                paymentDate = fine.getReturnDate() != null ? fine.getReturnDate() : LocalDate.now();
            }
            fine.setPaidDate(paymentDate);
        }

        // Update status
        if (newStatus != null) {
            fine.setStatus(newStatus);
        }

        // If fine amount is 0, status should be NONE
        if (fine.getAmount() != null && fine.getAmount() == 0.0) {
            fine.setStatus(FineStatus.NONE);
        }

        Fine savedFine = fineRepository.save(fine);
        return convertToDTO(savedFine);
    }

    /**
     * Mark fine as paid
     */
    @Transactional
    public FineDTO markAsPaid(Long fineId, LocalDate paymentDate) {
        return updateFine(fineId, paymentDate, FineStatus.PAID);
    }

    /**
     * Soft delete fine (mark status as NONE and set amount to 0)
     * OCP: Can be extended to use is_deleted flag if needed
     */
    @Transactional
    public void deleteFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found with ID: " + fineId));
        
        // Soft delete: Set status to NONE and amount to 0
        fine.setStatus(FineStatus.NONE);
        fine.setAmount(0.0);
        fineRepository.save(fine);
    }

    /**
     * Get fine statistics
     */
    public FineStatsDTO getFineStats() {
        Double totalUnpaid = fineRepository.sumUnpaidFines();
        Double totalPaid = fineRepository.sumPaidFines();
        
        return FineStatsDTO.builder()
                .totalUnpaid(totalUnpaid != null ? totalUnpaid : 0.0)
                .totalPaid(totalPaid != null ? totalPaid : 0.0)
                .totalCollected(totalPaid != null ? totalPaid : 0.0)
                .build();
    }

    /**
     * Convert Fine entity to DTO
     */
    private FineDTO convertToDTO(Fine fine) {
        return FineDTO.builder()
                .id(fine.getId())
                .transactionId(fine.getTransaction() != null ? fine.getTransaction().getId() : null)
                .memberName(fine.getTransaction() != null && fine.getTransaction().getUser() != null 
                        ? fine.getTransaction().getUser().getUsername() : null)
                .bookTitle(fine.getTransaction() != null && fine.getTransaction().getBook() != null 
                        ? fine.getTransaction().getBook().getTitle() : null)
                .issueDate(fine.getTransaction() != null ? fine.getTransaction().getIssueDate() : null)
                .returnDate(fine.getReturnDate())
                .paymentDate(fine.getPaidDate())
                .amount(fine.getAmount())
                .status(fine.getStatus() != null ? fine.getStatus().toString() : null)
                .build();
    }

    /**
     * DTO for fine statistics
     */
    public static class FineStatsDTO {
        private Double totalUnpaid;
        private Double totalPaid;
        private Double totalCollected;

        public static FineStatsDTOBuilder builder() {
            return new FineStatsDTOBuilder();
        }

        public Double getTotalUnpaid() { return totalUnpaid; }
        public void setTotalUnpaid(Double totalUnpaid) { this.totalUnpaid = totalUnpaid; }
        public Double getTotalPaid() { return totalPaid; }
        public void setTotalPaid(Double totalPaid) { this.totalPaid = totalPaid; }
        public Double getTotalCollected() { return totalCollected; }
        public void setTotalCollected(Double totalCollected) { this.totalCollected = totalCollected; }

        public static class FineStatsDTOBuilder {
            private Double totalUnpaid;
            private Double totalPaid;
            private Double totalCollected;

            public FineStatsDTOBuilder totalUnpaid(Double totalUnpaid) {
                this.totalUnpaid = totalUnpaid;
                return this;
            }

            public FineStatsDTOBuilder totalPaid(Double totalPaid) {
                this.totalPaid = totalPaid;
                return this;
            }

            public FineStatsDTOBuilder totalCollected(Double totalCollected) {
                this.totalCollected = totalCollected;
                return this;
            }

            public FineStatsDTO build() {
                FineStatsDTO dto = new FineStatsDTO();
                dto.setTotalUnpaid(this.totalUnpaid);
                dto.setTotalPaid(this.totalPaid);
                dto.setTotalCollected(this.totalCollected);
                return dto;
            }
        }
    }
}
