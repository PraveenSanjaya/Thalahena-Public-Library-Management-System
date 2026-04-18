package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.BookCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Transaction entity
 * SRP: Only responsible for data transfer, no business logic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long userId;
    private String memberName;
    private String memberEmail;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private Double fineAmount;
    private BookCondition bookCondition;
    private String conditionNotes;
}
