package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Author entity with book information
 * SRP: Only responsible for data transfer, no business logic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    private Long id;
    private String name;
    private String bio;
    private List<BookInfo> books;

    /**
     * Nested class to hold book information
     * Keeps book data separate from author data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {
        private Long bookId;
        private String title;
    }
}
