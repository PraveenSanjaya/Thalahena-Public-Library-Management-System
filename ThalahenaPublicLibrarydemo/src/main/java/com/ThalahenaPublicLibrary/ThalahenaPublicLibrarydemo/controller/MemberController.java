package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MemberDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/members")
@PreAuthorize("hasRole('ADMIN')")
public class MemberController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers(
            @RequestParam(required = false) String search) {
        List<User> members;
        
        if (search != null && !search.isEmpty()) {
            members = userRepository.searchMembers(search);
        } else {
            members = userRepository.findAllMembers();
        }
        
        List<MemberDTO> memberDTOs = members.stream()
            .map(this::convertToMemberDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(memberDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                if (user.getRole() != Role.MEMBER) {
                    return ResponseEntity.notFound().<MemberDTO>build();
                }
                return ResponseEntity.ok(convertToMemberDTO(user));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        
        user.setRole(Role.MEMBER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setMembershipDate(LocalDate.now());
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(convertToMemberDTO(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
            .map(user -> {
                if (user.getRole() != Role.MEMBER) {
                    return ResponseEntity.badRequest().body("Error: User is not a member!");
                }
                
                user.setFirstName(userDetails.getFirstName());
                user.setLastName(userDetails.getLastName());
                user.setEmail(userDetails.getEmail());
                user.setBirthDate(userDetails.getBirthDate());
                user.setGender(userDetails.getGender());
                user.setWhatsapp(userDetails.getWhatsapp());
                user.setSocialMedia(userDetails.getSocialMedia());
                user.setPhone(userDetails.getPhone());
                user.setActive(userDetails.isActive());
                
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }
                
                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(convertToMemberDTO(updatedUser));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                if (user.getRole() != Role.MEMBER) {
                    return ResponseEntity.badRequest().body("Error: User is not a member!");
                }
                userRepository.delete(user);
                return ResponseEntity.ok("Member deleted successfully");
            })
            .orElse(ResponseEntity.notFound().build());
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
