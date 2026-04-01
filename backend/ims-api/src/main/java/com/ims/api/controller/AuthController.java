package com.ims.api.controller;

import com.ims.api.dto.request.LoginRequest;
import com.ims.api.dto.request.RegisterRequest;
import com.ims.api.dto.response.AuthResponse;
import com.ims.api.security.JwtService;
import com.ims.domain.model.User;
import com.ims.domain.port.UserRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${registration.code}")
    private String registrationCode;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserRepositoryPort userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String token = jwtService.generateToken(request.email());
        return ResponseEntity.ok(AuthResponse.of(token, expirationMs, request.email()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (!registrationCode.equals(request.registrationCode())) {
            throw new IllegalArgumentException("Invalid registration code");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }
        User user = User.create(request.email(), passwordEncoder.encode(request.password()), "USER");
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(AuthResponse.of(token, expirationMs, user.getEmail()));
    }
}
