package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Book entity - Prevents circular references with Author
 * SRP: Only responsible for data transfer, no business logic
 * 
 * This DTO breaks the circular reference between Book and Author:
 * - Book contains lightweight author info only (id and name)
 * - Author's full book list is not included here
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private String category;
    private String description;
    private String publisher;
    private LocalDate dateReceived;
    private int totalCopies;
    private int availableCopies;
    private String coverImage;
    
    // Lightweight author info to prevent circular references
    private AuthorInfo author;

    /**
     * Nested class for author info
     * Contains only essential author fields, NOT the full books list
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long id;
        private String name;
        private String bio;
    }
}
