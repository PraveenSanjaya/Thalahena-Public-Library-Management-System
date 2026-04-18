package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.About;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.AboutRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AboutRepository aboutRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userRepository.findByUsername("admin1").orElse(User.builder()
                .username("admin1")
                .email("admin1@gmail.com")
                .role(Role.ADMIN)
                .isActive(true)
                .build());
        admin.setPassword(encoder.encode("admin123"));
        userRepository.save(admin);

        User staff = userRepository.findByUsername("staff1").orElse(User.builder()
                .username("staff1")
                .email("staff1@gmail.com")
                .role(Role.STAFF)
                .isActive(true)
                .build());
        staff.setPassword(encoder.encode("staff123"));
        userRepository.save(staff);

        User user = userRepository.findByUsername("user1").orElse(User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .role(Role.MEMBER)
                .isActive(true)
                .build());
        user.setPassword(encoder.encode("user123"));
        userRepository.save(user);

        if (aboutRepository.count() == 0) {
            About about = About.builder()
                    .content("Thalahena Public Library is dedicated to providing knowledge and resources to the community.")
                    .updatedAt(LocalDateTime.now())
                    .build();
            aboutRepository.save(about);
        }
    }
}
