package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.BookBorrowCountDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.CategoryCountDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MemberDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.StaffStatsDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.ReservationStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.TransactionStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class StaffController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<StaffStatsDTO> getStaffDashboardStats() {
        // Total books
        Long totalBooks = bookRepository.count();
        
        // Borrowed books (issued but not returned)
        Long booksBorrowed = transactionRepository.countByStatus(TransactionStatus.ISSUED);
        
        // Active reservations (PENDING or CONFIRMED)
        Long activeReservations = reservationRepository.countByStatus(ReservationStatus.PENDING);
        
        // Total unpaid fines
        Double totalFines = fineRepository.sumUnpaidFines();
        
        // Category distribution with Dewey Decimal codes
        List<Object[]> categoryData = bookRepository.getCategoryDistribution();
        List<CategoryCountDTO> categoryCounts = categoryData.stream()
            .map(row -> {
                String category = (String) row[0];
                Long count = (Long) row[1];
                String code = getDeweyDecimalCode(category);
                return new CategoryCountDTO(category, count, code);
            })
            .collect(Collectors.toList());
        
        // Top 5 borrowed books
        List<Object[]> topBooksData = bookRepository.getTopBorrowedBooks();
        List<BookBorrowCountDTO> top5Books = topBooksData.stream()
            .limit(5)
            .map(row -> new BookBorrowCountDTO(
                (Long) row[0],
                (String) row[1],
                (Long) row[2]
            ))
            .collect(Collectors.toList());
        
        StaffStatsDTO stats = StaffStatsDTO.builder()
            .totalBooks(totalBooks)
            .booksBorrowed(booksBorrowed)
            .activeReservations(activeReservations)
            .totalFines(totalFines != null ? totalFines : 0.0)
            .categoryCounts(categoryCounts)
            .top5Books(top5Books)
            .build();
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Map category name to Dewey Decimal classification code
     */
    private String getDeweyDecimalCode(String category) {
        if (category == null) return "000";
        
        String lowerCategory = category.toLowerCase();
        
        if (lowerCategory.contains("general") || lowerCategory.contains("computer")) return "000";
        if (lowerCategory.contains("philosophy") || lowerCategory.contains("psychology")) return "100";
        if (lowerCategory.contains("religion") || lowerCategory.contains("theology")) return "200";
        if (lowerCategory.contains("social") || lowerCategory.contains("political") || lowerCategory.contains("economics")) return "300";
        if (lowerCategory.contains("language") || lowerCategory.contains("linguistics")) return "400";
        if (lowerCategory.contains("science") || lowerCategory.contains("mathematics") || lowerCategory.contains("nature")) return "500";
        if (lowerCategory.contains("technology") || lowerCategory.contains("engineering") || lowerCategory.contains("medicine")) return "600";
        if (lowerCategory.contains("art") || lowerCategory.contains("music") || lowerCategory.contains("recreation")) return "700";
        if (lowerCategory.contains("literature") || lowerCategory.contains("fiction") || lowerCategory.contains("poetry")) return "800";
        if (lowerCategory.contains("history") || lowerCategory.contains("geography") || lowerCategory.contains("biography")) return "900";
        
        return "000"; // Default to Generalities
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = userRepository.findAllMembers().stream()
                .map(this::convertToMemberDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/search")
    public ResponseEntity<List<MemberDTO>> searchMembers(@RequestParam("query") String query) {
        List<MemberDTO> members = userRepository.searchMembers(query).stream()
                .map(this::convertToMemberDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/{id}/borrow-history")
    public ResponseEntity<List<?>> getMemberBorrowHistory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionRepository.findByUserIdOrderByIssueDateDesc(id));
    }

    private MemberDTO convertToMemberDTO(User user) {
        Integer age = null;
        if (user.getBirthDate() != null) {
            age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        }
        return MemberDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .age(age)
                .gender(user.getGender())
                .role(user.getRole().name())
                .membershipDate(user.getMembershipDate())
                .whatsapp(user.getWhatsapp())
                .socialMedia(user.getSocialMedia())
                .phone(user.getPhone())
                .profilePicture(user.getProfilePicture())
                .isActive(user.isActive())
                .build();
    }
}
