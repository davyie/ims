package com.ims.user.adapter.in.rest.dto;

import com.ims.user.domain.model.UserRole;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(String token, UUID userId, UserRole role, Instant expiresAt) {
}
