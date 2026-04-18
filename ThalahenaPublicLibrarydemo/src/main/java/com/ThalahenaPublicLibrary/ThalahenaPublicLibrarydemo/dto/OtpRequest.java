package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Format must be a valid email address")
    private String email;

    public OtpRequest() {
    }

    public OtpRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
