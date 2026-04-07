package com.ims.user.adapter.in.rest.dto;

import com.ims.user.domain.model.User;
import com.ims.user.domain.model.UserRole;
import com.ims.user.domain.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        String email,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
