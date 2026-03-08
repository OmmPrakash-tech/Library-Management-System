package com.example.service.impl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.domain.UserRole;
import com.example.model.User;
import com.example.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {

        String adminEmail = "ommprakashdebata39@gmail.com";
        String adminPassword = "ommprakash";

        if (!userRepository.existsByEmail(adminEmail)) {

            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .fullName("Code With Zosh")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            userRepository.save(adminUser);

            System.out.println("Admin user created successfully!");
        }
    }
}