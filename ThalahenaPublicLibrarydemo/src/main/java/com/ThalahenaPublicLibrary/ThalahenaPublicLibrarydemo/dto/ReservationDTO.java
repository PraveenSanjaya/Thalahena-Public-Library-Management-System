package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Reservation entity
 * SRP: Only responsible for data transfer, no business logic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long userId;
    private String memberName;
    private String memberEmail;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private String status;
    private Boolean processed;
}
