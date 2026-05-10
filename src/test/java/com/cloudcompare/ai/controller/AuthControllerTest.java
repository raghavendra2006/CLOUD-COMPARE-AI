package com.cloudcompare.ai.controller;

import com.cloudcompare.ai.dto.LoginRequest;
import com.cloudcompare.ai.dto.SignupRequest;
import com.cloudcompare.ai.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testSignup() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("wrongpass");

        org.mockito.Mockito.when(userRepository.findByEmail(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockBean
    private com.cloudcompare.ai.security.JwtUtil jwtUtil;

    @MockBean
    private com.cloudcompare.ai.repository.UserRepository userRepository;

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123!");

        com.cloudcompare.ai.entity.UserEntity user = new com.cloudcompare.ai.entity.UserEntity();
        user.setName("Test Name");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        org.mockito.Mockito.when(userRepository.findByEmail(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.of(user));
        org.mockito.Mockito.when(passwordEncoder.matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(true);
        org.mockito.Mockito.when(jwtUtil.generateToken(org.mockito.ArgumentMatchers.any()))
                .thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.token").value("dummy-jwt-token"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Test Name"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }
}
