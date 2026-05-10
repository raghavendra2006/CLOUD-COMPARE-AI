package com.cloudcompare.ai.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testTokenLifecycle() {
        UserDetails userDetails = new User("test@example.com", "password", new ArrayList<>());
        String token = jwtUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertEquals("test@example.com", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }
}
