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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          AuthService authService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
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

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            UserEntity user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

            logger.info("Login successful for: {}", loginRequest.getEmail());
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "name", user.getName(),
                    "email", user.getEmail()
            ));
        } catch (org.springframework.security.core.AuthenticationException e) {
            logger.error("Login failed: ", e);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        } catch (java.util.NoSuchElementException e) {
            logger.error("Login failed - user not found: ", e);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }
}
