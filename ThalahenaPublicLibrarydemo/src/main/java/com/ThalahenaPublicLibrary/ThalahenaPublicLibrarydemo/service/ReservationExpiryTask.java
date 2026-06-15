package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Reservation;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.ReservationStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReservationExpiryTask - Scheduled job that auto-cancels expired reservations
 * 
 * Runs every hour and marks PENDING reservations whose expiryDate has passed as CANCELLED.
 * This prevents stale reservations from sitting indefinitely.
 * 
 * SRP: Only handles expiry logic, nothing else
 * OCP: Expiry period is configurable via Reservation.prePersist() (default: 3 days)
 */
@Component
public class ReservationExpiryTask {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryTask.class);

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Runs every hour (cron: "0 0 * * * *" = top of every hour)
     * Finds all PENDING reservations whose expiryDate is in the past,
     * and marks them as CANCELLED + processed.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cancelExpired() {
        List<Reservation> expired = reservationRepository
                .findByStatusAndExpiryDateBefore(ReservationStatus.PENDING, LocalDateTime.now());

        if (expired.isEmpty()) {
            return; // Nothing to do
        }

        log.info("ReservationExpiryTask: Found {} expired reservation(s)", expired.size());

        for (Reservation r : expired) {
            r.setStatus(ReservationStatus.CANCELLED);
            r.setProcessed(true);
        }

        reservationRepository.saveAll(expired);
        log.info("ReservationExpiryTask: Cancelled {} expired reservation(s)", expired.size());
    }
}
