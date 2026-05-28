package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.AboutDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.About;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.AboutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/about")
public class AboutController {
    @Autowired
    private AboutRepository aboutRepository;

    @GetMapping
    public ResponseEntity<List<AboutDTO>> getAllAbout() {
        List<AboutDTO> dtos = aboutRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AboutDTO> createAbout(@RequestBody AboutDTO aboutDetails) {
        About about = new About();
        about.setContent(aboutDetails.getContent());
        about.setUpdatedAt(LocalDateTime.now());
        About saved = aboutRepository.save(about);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AboutDTO> updateAbout(@PathVariable Long id, @RequestBody AboutDTO aboutDetails) {
        About about = aboutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("About statement not found"));
        about.setContent(aboutDetails.getContent());
        about.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(convertToDTO(aboutRepository.save(about)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAbout(@PathVariable Long id) {
        if (!aboutRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        aboutRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Convert About entity to AboutDTO
     */
    private AboutDTO convertToDTO(About about) {
        return AboutDTO.builder()
                .id(about.getId())
                .content(about.getContent())
                .updatedAt(about.getUpdatedAt())
                .build();
    }
}
