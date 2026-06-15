package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Reservation;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.ReservationStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    
    // Count by status
    Long countByStatus(ReservationStatus status);
    
    // Find by status
    List<Reservation> findByStatus(ReservationStatus status);
    
    // Find by processed status
    List<Reservation> findByProcessed(Boolean processed);
    
    // Count unprocessed reservations
    Long countByProcessedFalse();
    
    // Get all reservations with user and book details
    @Query("SELECT r FROM Reservation r JOIN r.user u JOIN r.book b ORDER BY r.reservationDate DESC")
    List<Reservation> findAllWithDetails();

    @Query("SELECT r FROM Reservation r JOIN r.user u JOIN r.book b WHERE u.id = :userId ORDER BY r.reservationDate DESC")
    List<Reservation> findByUserIdWithDetails(@org.springframework.data.repository.query.Param("userId") Long userId);
    
    /**
     * Find expired reservations: status = PENDING and expiryDate has passed
     * Used by scheduled expiry task to auto-cancel stale reservations
     */
    List<Reservation> findByStatusAndExpiryDateBefore(ReservationStatus status, LocalDateTime time);
}
