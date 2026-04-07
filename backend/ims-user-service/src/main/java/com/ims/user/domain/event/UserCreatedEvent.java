package com.ims.user.domain.event;

import com.ims.user.domain.model.UserRole;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, String username, String email, UserRole role) {
}
