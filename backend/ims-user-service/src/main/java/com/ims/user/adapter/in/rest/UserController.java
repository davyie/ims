package com.ims.user.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.user.adapter.in.rest.dto.*;
import com.ims.user.application.command.*;
import com.ims.user.application.query.*;
import com.ims.user.application.service.JwtService;
import com.ims.user.domain.model.User;
import com.ims.user.domain.port.in.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserUseCase userUseCase;
    private final JwtService jwtService;

    public UserController(UserUseCase userUseCase, JwtService jwtService) {
        this.userUseCase = userUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userUseCase.createUser(new CreateUserCommand(
                request.username(), request.email(), request.password(), request.role()
        ));
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userUseCase.authenticate(new AuthenticateUserQuery(request.username(), request.password()));
        UUID userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);
        return ResponseEntity.ok(new AuthResponse(
                token,
                userId,
                com.ims.user.domain.model.UserRole.valueOf(role),
                jwtService.extractExpiry(token)
        ));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.name")
    public UserResponse getUserById(@PathVariable UUID userId) {
        User user = userUseCase.getUserById(new GetUserByIdQuery(userId));
        return UserResponse.from(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<User> users = userUseCase.listUsers(new ListUsersQuery(page, size));
        return PageResponse.of(
                users.content().stream().map(UserResponse::from).toList(),
                users.page(), users.size(), users.totalElements()
        );
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.name")
    public UserResponse updateUser(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        User user = userUseCase.updateUser(new UpdateUserCommand(userId, request.username(), request.email()));
        return UserResponse.from(user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable UUID userId) {
        userUseCase.deactivateUser(new DeactivateUserCommand(userId));
    }

    @PutMapping("/{userId}/password")
    @PreAuthorize("#userId.toString() == authentication.name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable UUID userId, @Valid @RequestBody ChangePasswordRequest request) {
        userUseCase.changePassword(new ChangePasswordCommand(userId, request.currentPassword(), request.newPassword()));
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse assignRole(@PathVariable UUID userId, @Valid @RequestBody AssignRoleRequest request) {
        User user = userUseCase.assignRole(new AssignRoleCommand(userId, request.newRole()));
        return UserResponse.from(user);
    }
}
