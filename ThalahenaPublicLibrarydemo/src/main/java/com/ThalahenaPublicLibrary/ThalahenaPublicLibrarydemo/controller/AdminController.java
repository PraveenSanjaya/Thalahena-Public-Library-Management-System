package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.AdminStatsDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.FeedbackDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Feedback;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.FeedbackRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getAdminStats() {
        // Total users
        Long totalUsers = userRepository.count();
        
        // Staff and admin counts
        Long staffCount = userRepository.countByRole(Role.STAFF);
        Long adminCount = userRepository.countByRole(Role.ADMIN);
        
        // Active/Inactive members
        Long activeMembers = userRepository.countByRoleAndIsActive(Role.MEMBER, true);
        Long inactiveMembers = userRepository.countByRoleAndIsActive(Role.MEMBER, false);
        
        // Gender distribution
        List<User> allUsers = userRepository.findAll();
        Map<String, Long> genderDistribution = allUsers.stream()
            .filter(u -> u.getGender() != null)
            .collect(Collectors.groupingBy(User::getGender, Collectors.counting()));
        
        // Age distribution (≤18 vs >18)
        long under18 = 0;
        long over18 = 0;
        LocalDate today = LocalDate.now();
        
        for (User user : allUsers) {
            if (user.getBirthDate() != null) {
                int age = Period.between(user.getBirthDate(), today).getYears();
                if (age <= 18) {
                    under18++;
                } else {
                    over18++;
                }
            }
        }
        
        Map<String, Long> ageDistribution = new HashMap<>();
        ageDistribution.put("≤18", under18);
        ageDistribution.put(">18", over18);
        
        // Recent feedback
        List<Feedback> recentFeedback = feedbackRepository.findTop5ByOrderByCreatedAtDesc();
        List<FeedbackDTO> feedbackDTOs = recentFeedback.stream()
            .map(f -> new FeedbackDTO(
                f.getId(),
                f.getMessage(),
                f.getUser() != null ? f.getUser().getUsername() : "Anonymous",
                f.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
        AdminStatsDTO stats = AdminStatsDTO.builder()
            .totalUsers(totalUsers)
            .staffCount(staffCount)
            .adminCount(adminCount)
            .activeMembers(activeMembers)
            .inactiveMembers(inactiveMembers)
            .genderDistribution(genderDistribution)
            .ageDistribution(ageDistribution)
            .recentFeedback(feedbackDTOs)
            .build();
        
        return ResponseEntity.ok(stats);
    }
}
