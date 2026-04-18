package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for About entity
 * SRP: Only responsible for data transfer, no business logic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutDTO {
    private Long id;
    private String content;
    private LocalDateTime updatedAt;
}
