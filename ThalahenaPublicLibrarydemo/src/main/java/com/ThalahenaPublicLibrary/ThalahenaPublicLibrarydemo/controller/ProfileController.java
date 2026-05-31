package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.PasswordChangeRequest;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ProfileDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.security.UserDetailsImpl;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ProfileController - API endpoints for managing the logged-in user's profile.
 * 
 * SOLID Principles Applied:
 * - SRP (Single Responsibility Principle): This controller handles HTTP request routing, parameters extraction, and maps the response models.
 * - DIP (Dependency Injection Principle): It depends on the high-level ProfileService abstraction rather than concrete repository classes.
 * - OCP (Open/Closed Principle): New profile fields or actions can be introduced here without altering user authentication filters.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Helper to retrieve the current logged-in user's ID from security context.
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        }
        throw new IllegalStateException("User is not authenticated");
    }

    /**
     * GET /api/profile
     * Returns logged-in user's profile details.
     */
    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile() {
        Long userId = getCurrentUserId();
        ProfileDTO profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/profile
     * Updates profile details of the logged-in user.
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDTO updateData) {
        try {
            Long userId = getCurrentUserId();
            ProfileDTO updatedProfile = profileService.updateProfile(userId, updateData);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("An error occurred while updating profile."));
        }
    }

    /**
     * PUT /api/profile/change-password
     * Changes password securely for the logged-in user.
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            Long userId = getCurrentUserId();
            profileService.changePassword(userId, passwordChangeRequest);
            return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("An error occurred while changing password."));
        }
    }

    /**
     * POST /api/profile/upload-image
     * Uploads/updates profile picture for the logged-in user.
     */
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = getCurrentUserId();
            String path = profileService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok(new MessageResponse(path));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Failed to save profile picture: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("An error occurred during file upload."));
        }
    }
}
