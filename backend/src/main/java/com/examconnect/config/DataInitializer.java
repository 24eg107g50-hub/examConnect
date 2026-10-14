package com.examconnect.config;

import com.examconnect.entity.User;
import com.examconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.initial.email:admin@examconnect.com}")
    private String adminEmail;

    @Value("${admin.initial.password}")
    private String adminPassword;

    @Value("${admin.initial.name:System Administrator}")
    private String adminName;

    @Override
    public void run(String... args) {
        // Ensure Admin user exists with correct password encoding
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                admin -> {
                    // Update password if necessary to ensure it matches
                    admin.setPassword(passwordEncoder.encode(adminPassword));
                    admin.setRole("ROLE_ADMIN");
                    admin.setEnabled(true);
                    userRepository.save(admin);
                    log.info("Verified administrative user: {}", adminEmail);
                },
                () -> {
                    User newAdmin = User.builder()
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .fullName(adminName)
                            .role("ROLE_ADMIN")
                            .phone("+91 9876543210")
                            .enabled(true)
                            .build();
                    userRepository.save(newAdmin);
                    log.info("Created initial admin user: {}", adminEmail);
                }
        );

        // Ensure default student demo user has encoded password
        String demoStudentEmail = "student1@examconnect.com";
        userRepository.findByEmail(demoStudentEmail).ifPresentOrElse(
                student -> {
                    student.setPassword(passwordEncoder.encode("Student@123"));
                    userRepository.save(student);
                },
                () -> {
                    User student = User.builder()
                            .email(demoStudentEmail)
                            .password(passwordEncoder.encode("Student@123"))
                            .fullName("Aditya Verma")
                            .role("ROLE_STUDENT")
                            .phone("+91 9123456789")
                            .enabled(true)
                            .build();
                    userRepository.save(student);
                    log.info("Created default demo student: {}", demoStudentEmail);
                }
        );

        // Also ensure seed student rahul.sharma@example.com is properly initialized
        userRepository.findByEmail("rahul.sharma@example.com").ifPresent(student -> {
            student.setPassword(passwordEncoder.encode("Student@123"));
            userRepository.save(student);
        });
    }
}
