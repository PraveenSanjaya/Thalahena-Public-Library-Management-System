package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.PasswordChangeRequest;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ProfileDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.FineRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.TransactionRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * ProfileService - Business logic service for user profiles.
 * 
 * SOLID Principles Applied:
 * - SRP (Single Responsibility Principle): This service is solely responsible for user profile business logic.
 * - DIP (Dependency Injection Principle): All repository and utility dependencies are injected via Spring Autowiring.
 * - OCP (Open/Closed Principle): Role-specific details are handled dynamically using DTOs, allowing extension without modifying the core User structure.
 */
@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String uploadDir = "uploads/profiles/";

    /**
     * Retrieves the profile DTO for the given user ID.
     */
    @Transactional(readOnly = true)
    public ProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        ProfileDTO dto = new ProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        dto.setFullName((first + " " + last).trim());
        
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setMembershipDate(user.getMembershipDate());
        dto.setWhatsapp(user.getWhatsapp());
        dto.setSocialMedia(user.getSocialMedia());

        // Role specific logic
        if (user.getRole() == Role.STAFF) {
            String sId = user.getStaffId();
            if (sId == null || sId.trim().isEmpty()) {
                sId = "STF-" + String.format("%04d", user.getId());
            }
            dto.setStaffId(sId);
            dto.setDepartment(user.getDepartment() != null ? user.getDepartment() : "General Services");
            dto.setPosition(user.getPosition() != null ? user.getPosition() : "Library Assistant");
        } else if (user.getRole() == Role.MEMBER) {
            String mId = user.getMemberId();
            if (mId == null || mId.trim().isEmpty()) {
                mId = "MEM-" + String.format("%04d", user.getId());
            }
            dto.setMemberId(mId);
            dto.setTotalBooksBorrowed(transactionRepository.countByUserId(user.getId()));
            dto.setCurrentBorrowedBooks(transactionRepository.countActiveTransactionsByUserId(user.getId()));
            dto.setOutstandingFines(fineRepository.sumUnpaidFinesByUserId(user.getId()));
        }

        return dto;
    }

    /**
     * Updates the user's profile information.
     */
    @Transactional
    public ProfileDTO updateProfile(Long userId, ProfileDTO updateData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Validate duplicate email
        if (updateData.getEmail() != null && !updateData.getEmail().equalsIgnoreCase(user.getEmail())) {
            Optional<User> existingEmailUser = userRepository.findByEmail(updateData.getEmail());
            if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(userId)) {
                throw new IllegalArgumentException("Error: Email is already in use by another account!");
            }
            user.setEmail(updateData.getEmail().trim());
        }

        if (updateData.getFirstName() != null) user.setFirstName(updateData.getFirstName().trim());
        if (updateData.getLastName() != null) user.setLastName(updateData.getLastName().trim());
        if (updateData.getPhone() != null) user.setPhone(updateData.getPhone().trim());
        if (updateData.getBirthDate() != null) user.setBirthDate(updateData.getBirthDate());
        if (updateData.getGender() != null) user.setGender(updateData.getGender().trim());
        if (updateData.getWhatsapp() != null) user.setWhatsapp(updateData.getWhatsapp().trim());
        if (updateData.getSocialMedia() != null) user.setSocialMedia(updateData.getSocialMedia().trim());

        if (user.getRole() == Role.STAFF) {
            if (updateData.getDepartment() != null) user.setDepartment(updateData.getDepartment().trim());
            if (updateData.getPosition() != null) user.setPosition(updateData.getPosition().trim());
        }

        userRepository.save(user);
        return getProfile(userId);
    }

    /**
     * Changes the user's password securely.
     */
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if current password matches
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Error: Current password is incorrect!");
        }

        // Validate new password and confirmation
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("Error: New password must be at least 6 characters long!");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Error: New password and confirmation do not match!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Uploads and updates the user's profile image.
     */
    @Transactional
    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Path root = Paths.get(uploadDir);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(fileName));

        String relativePath = "/uploads/profiles/" + fileName;
        user.setProfilePicture(relativePath);
        userRepository.save(user);

        return relativePath;
    }
}
