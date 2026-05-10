package com.cloudcompare.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.cloudcompare.ai.repository.UserRepository userRepository;

    @Test
    void testRegisterUserSuccess() {
        com.cloudcompare.ai.dto.SignupRequest req = new com.cloudcompare.ai.dto.SignupRequest();
        req.setName("Auth Test");
        req.setEmail("authtest@example.com");
        req.setPassword("Password123@");

        com.cloudcompare.ai.entity.UserEntity user = authService.registerUser(req);
        assertNotNull(user);
        assertEquals("Auth Test", user.getName());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        com.cloudcompare.ai.dto.SignupRequest req = new com.cloudcompare.ai.dto.SignupRequest();
        req.setName("Auth Test 2");
        req.setEmail("authtest@example.com");
        req.setPassword("Password123@");

        // First registration
        authService.registerUser(req);

        assertThrows(RuntimeException.class, () -> authService.registerUser(req));
    }

    @Test
    void testRegisterUserWeakPassword() {
        com.cloudcompare.ai.dto.SignupRequest req = new com.cloudcompare.ai.dto.SignupRequest();
        req.setName("Auth Test 3");
        req.setEmail("weak@example.com");
        req.setPassword("weak");

        assertThrows(RuntimeException.class, () -> authService.registerUser(req));
    }
}
