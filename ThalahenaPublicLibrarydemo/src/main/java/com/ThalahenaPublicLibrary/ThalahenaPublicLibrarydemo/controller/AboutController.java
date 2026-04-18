package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.AboutDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.About;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.AboutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/about")
public class AboutController {
    @Autowired
    AboutRepository aboutRepository;

    @GetMapping
    public ResponseEntity<AboutDTO> getAbout() {
        return aboutRepository.findById(1L)
                .map(about -> ResponseEntity.ok(convertToDTO(about)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AboutDTO> updateAbout(@RequestBody AboutDTO aboutDetails) {
        About about = aboutRepository.findById(1L).orElse(new About());
        about.setContent(aboutDetails.getContent());
        about.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(convertToDTO(aboutRepository.save(about)));
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
