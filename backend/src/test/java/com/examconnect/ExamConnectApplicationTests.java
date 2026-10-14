package com.examconnect;

import com.examconnect.dto.LoginRequest;
import com.examconnect.dto.RegisterRequest;
import com.examconnect.exception.BadRequestException;
import com.examconnect.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "jwt.secret=test-only-jwt-signing-key-not-for-use-outside-tests",
        "admin.initial.password=TestOnly@123"
})
@Transactional
class ExamConnectApplicationTests {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        assertNotNull(authService);
    }

    @Test
    @DisplayName("User registration and login works with BCrypt and JWT")
    void testUserRegistrationAndLogin() {
        RegisterRequest registerReq = RegisterRequest.builder()
                .fullName("Test Student")
                .email("test.student.unique@example.com")
                .password("Password@123")
                .confirmPassword("Password@123")
                .phone("+91 9999988888")
                .termsAccepted(true)
                .build();

        var authRes = authService.register(registerReq);
        assertNotNull(authRes);
        assertNotNull(authRes.getToken());
        assertEquals("test.student.unique@example.com", authRes.getEmail());
        assertEquals("ROLE_STUDENT", authRes.getRole());

        // Test login
        LoginRequest loginReq = LoginRequest.builder()
                .email("test.student.unique@example.com")
                .password("Password@123")
                .build();

        var loginRes = authService.login(loginReq);
        assertNotNull(loginRes);
        assertNotNull(loginRes.getToken());
        assertEquals(authRes.getId(), loginRes.getId());
    }

    @Test
    @DisplayName("Duplicate email registration is rejected")
    void testDuplicateEmailRejection() {
        RegisterRequest req1 = RegisterRequest.builder()
                .fullName("User One")
                .email("duplicate@example.com")
                .password("Pass@123")
                .confirmPassword("Pass@123")
                .termsAccepted(true)
                .build();
        authService.register(req1);

        RegisterRequest req2 = RegisterRequest.builder()
                .fullName("User Two")
                .email("duplicate@example.com")
                .password("Pass@456")
                .confirmPassword("Pass@456")
                .termsAccepted(true)
                .build();

        assertThrows(BadRequestException.class, () -> authService.register(req2));
    }
}
