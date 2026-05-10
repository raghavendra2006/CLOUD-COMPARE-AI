package com.cloudcompare.ai.controller;

import com.cloudcompare.ai.dto.LoginRequest;
import com.cloudcompare.ai.dto.SignupRequest;
import com.cloudcompare.ai.entity.UserEntity;
import com.cloudcompare.ai.repository.UserRepository;
import com.cloudcompare.ai.security.JwtUtil;
import com.cloudcompare.ai.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("Processing signup for: {}", signupRequest.getEmail());
        authService.registerUser(signupRequest);
        return ResponseEntity.ok(Map.of("message", "User registration successful. Identity established."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login request received for email: {}", loginRequest.getEmail());

            // Find user in database
            UserEntity user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
            if (user == null) {
                logger.warn("Login failed - user not found: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
            }

            // Verify password using BCrypt
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.warn("Login failed - wrong password for: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
            }

            // Generate JWT token
            UserDetails userDetails = User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(new ArrayList<>())
                    .build();
            String jwt = jwtUtil.generateToken(userDetails);

            logger.info("Login successful for: {}", loginRequest.getEmail());
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "name", user.getName(),
                    "email", user.getEmail()
            ));
        } catch (Exception e) {
            logger.error("Login error for {}: [{}] {}", loginRequest.getEmail(), e.getClass().getName(), e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Login failed. Please try again."));
        }
    }
}
