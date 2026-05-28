package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Integer age;
    private String gender;
    private String role;
    private LocalDate membershipDate;
    private String whatsapp;
    private String socialMedia;
    private String phone;
    private String profilePicture;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
