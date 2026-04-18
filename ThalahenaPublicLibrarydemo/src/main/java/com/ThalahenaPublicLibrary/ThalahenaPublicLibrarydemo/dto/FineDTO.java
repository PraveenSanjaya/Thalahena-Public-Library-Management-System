package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Fine entity
 * SRP: Only responsible for data transfer, no business logic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineDTO {
    private Long id;
    private Long transactionId;
    private String memberName;
    private String bookTitle;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private LocalDate paymentDate;
    private Double amount;
    private String status;
}
