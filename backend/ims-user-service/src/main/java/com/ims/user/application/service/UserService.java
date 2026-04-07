package com.ims.user.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ConflictException;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.common.exception.ValidationException;
import com.ims.user.application.command.*;
import com.ims.user.application.query.*;
import com.ims.user.domain.event.*;
import com.ims.user.domain.model.User;
import com.ims.user.domain.model.UserStatus;
import com.ims.user.domain.port.in.UserUseCase;
import com.ims.user.domain.port.out.TokenCache;
import com.ims.user.domain.port.out.UserEventPublisher;
import com.ims.user.domain.port.out.UserRepository;
import com.ims.user.infrastructure.config.UserProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenCache tokenCache;
    private final UserProperties properties;

    public UserService(UserRepository userRepository,
                       UserEventPublisher eventPublisher,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       TokenCache tokenCache,
                       UserProperties properties) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenCache = tokenCache;
        this.properties = properties;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        if (command.password().length() < properties.getPasswordMinLength()) {
            throw new ValidationException("Password must be at least " + properties.getPasswordMinLength() + " characters");
        }
        if (userRepository.existsByUsername(command.username())) {
            throw new ConflictException("Username already exists: " + command.username());
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new ConflictException("Email already exists: " + command.email());
        }

        User user = User.builder()
                .username(command.username())
                .email(command.email())
                .passwordHash(passwordEncoder.encode(command.password()))
                .role(command.role())
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", saved.getUserId().toString());
        payload.put("username", saved.getUsername());
        payload.put("email", saved.getEmail());
        payload.put("role", saved.getRole().name());

        eventPublisher.publish(EventEnvelope.of("USER_CREATED", "ims-user-service", saved.getUserId(), payload));

        return saved;
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.userId()));

        if (command.username() != null && !command.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(command.username())) {
                throw new ConflictException("Username already exists: " + command.username());
            }
            user.setUsername(command.username());
        }

        if (command.email() != null && !command.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(command.email())) {
                throw new ConflictException("Email already exists: " + command.email());
            }
            user.setEmail(command.email());
        }

        User saved = userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", saved.getUserId().toString());
        payload.put("username", saved.getUsername());
        payload.put("email", saved.getEmail());

        eventPublisher.publish(EventEnvelope.of("USER_UPDATED", "ims-user-service", saved.getUserId(), payload));

        return saved;
    }

    @Override
    public void deactivateUser(DeactivateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.userId()));

        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getUserId().toString());
        payload.put("username", user.getUsername());

        eventPublisher.publish(EventEnvelope.of("USER_DEACTIVATED", "ims-user-service", user.getUserId(), payload));
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect");
        }

        if (command.newPassword().length() < properties.getPasswordMinLength()) {
            throw new ValidationException("New password must be at least " + properties.getPasswordMinLength() + " characters");
        }

        user.setPasswordHash(passwordEncoder.encode(command.newPassword()));
        userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getUserId().toString());
        payload.put("username", user.getUsername());

        eventPublisher.publish(EventEnvelope.of("USER_PASSWORD_CHANGED", "ims-user-service", user.getUserId(), payload));
    }

    @Override
    public User assignRole(AssignRoleCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.userId()));

        user.setRole(command.newRole());
        User saved = userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", saved.getUserId().toString());
        payload.put("username", saved.getUsername());
        payload.put("newRole", command.newRole().name());

        eventPublisher.publish(EventEnvelope.of("USER_ROLE_ASSIGNED", "ims-user-service", saved.getUserId(), payload));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(GetUserByIdQuery query) {
        return userRepository.findById(query.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", query.userId()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> listUsers(ListUsersQuery query) {
        Page<User> page = userRepository.findAll(PageRequest.of(query.page(), query.size()));
        return PageResponse.of(page.getContent(), query.page(), query.size(), page.getTotalElements());
    }

    @Override
    public String authenticate(AuthenticateUserQuery query) {
        User user = userRepository.findByUsername(query.username())
                .orElseThrow(() -> new ValidationException("Invalid credentials"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ValidationException("User account is not active");
        }

        if (!passwordEncoder.matches(query.password(), user.getPasswordHash())) {
            throw new ValidationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        tokenCache.cacheToken(token, user.getUserId(), Duration.ofMinutes(properties.getJwtExpiryMinutes()));

        return token;
    }
}
