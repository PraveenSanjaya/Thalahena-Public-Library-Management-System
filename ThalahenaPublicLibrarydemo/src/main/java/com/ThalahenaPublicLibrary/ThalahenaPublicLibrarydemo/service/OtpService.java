package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.OtpToken;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpTokenRepository otpRepository;

    @Autowired
    private EmailService emailService;

    // Generate, save, and send a 6-digit OTP
    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepository.save(otpToken);

        String message = "Your Thalahena Library OTP is: " + otp + ". Valid for 5 minutes.";
        System.out.println("OTP requested for: " + email + ", OTP: " + otp); // For dev mock purposes
        
        try {
             emailService.sendEmail(email, "Your Login OTP", message);
        } catch (Exception e) {
             System.err.println("Error sending email: " + e.getMessage());
             // Proceed regardless for dev mock purposes
        }
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otpString) {
        Optional<OtpToken> tokenOpt = otpRepository.findFirstByEmailAndUsedFalseOrderByExpiryTimeDesc(email);
        
        if (tokenOpt.isPresent()) {
            OtpToken token = tokenOpt.get();
            if (token.getOtp().equals(otpString) && token.getExpiryTime().isAfter(LocalDateTime.now())) {
                token.setUsed(true);
                otpRepository.save(token);
                return true;
            }
        }
        return false;
    }
}
