package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffStatsDTO {
    private Long totalBooks;
    private Long booksBorrowed;
    private Long activeReservations;
    private Double totalFines;
    private List<CategoryCountDTO> categoryCounts;
    private List<BookBorrowCountDTO> top5Books;
}
