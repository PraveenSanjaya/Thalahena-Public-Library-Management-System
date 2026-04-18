package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.RegistrationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/registrations")
@PreAuthorize("hasRole('ADMIN')")
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> getAllStaffAndAdmins() {
        List<User> users = userRepository.findAll().stream()
            .filter(u -> u.getRole() == Role.STAFF || u.getRole() == Role.ADMIN)
            .toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createRegistration(@Valid @RequestBody RegistrationDTO registrationDTO) {
        // Check username uniqueness
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        
        // Check email uniqueness
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        
        // Validate role
        Role role;
        try {
            role = Role.valueOf(registrationDTO.getRole().toUpperCase());
            if (role != Role.STAFF && role != Role.ADMIN) {
                return ResponseEntity.badRequest().body("Error: Role must be STAFF or ADMIN!");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: Invalid role!");
        }
        
        // Create user
        User user = User.builder()
            .firstName(registrationDTO.getFirstName())
            .lastName(registrationDTO.getLastName())
            .username(registrationDTO.getUsername())
            .email(registrationDTO.getEmail())
            .phone(registrationDTO.getPhone())
            .whatsapp(registrationDTO.getWhatsapp())
            .password(passwordEncoder.encode(registrationDTO.getPassword()))
            .role(role)
            .isActive(true)
            .membershipDate(LocalDate.now())
            .build();
        
        User savedUser = userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("role", savedUser.getRole());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegistration(@PathVariable Long id, @Valid @RequestBody RegistrationDTO registrationDTO) {
        return userRepository.findById(id)
            .map(user -> {
                if (user.getRole() != Role.STAFF && user.getRole() != Role.ADMIN) {
                    return ResponseEntity.badRequest().body("Error: Can only update staff or admin users!");
                }
                
                // Check email uniqueness if changed
                if (!user.getEmail().equals(registrationDTO.getEmail()) && 
                    userRepository.existsByEmail(registrationDTO.getEmail())) {
                    return ResponseEntity.badRequest().body("Error: Email is already in use!");
                }
                
                user.setFirstName(registrationDTO.getFirstName());
                user.setLastName(registrationDTO.getLastName());
                user.setEmail(registrationDTO.getEmail());
                user.setPhone(registrationDTO.getPhone());
                user.setWhatsapp(registrationDTO.getWhatsapp());
                
                // Update password if provided
                if (registrationDTO.getPassword() != null && !registrationDTO.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
                }
                
                // Update role if provided
                if (registrationDTO.getRole() != null) {
                    Role newRole = Role.valueOf(registrationDTO.getRole().toUpperCase());
                    if (newRole == Role.STAFF || newRole == Role.ADMIN) {
                        user.setRole(newRole);
                    }
                }
                
                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(updatedUser);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                if (user.getRole() != Role.STAFF && user.getRole() != Role.ADMIN) {
                    return ResponseEntity.badRequest().body("Error: Can only delete staff or admin users!");
                }
                userRepository.delete(user);
                return ResponseEntity.ok("User deleted successfully");
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
