package com.ims.user.domain.event;

import com.ims.user.domain.model.UserRole;

import java.util.UUID;

public record UserRoleAssignedEvent(UUID userId, String username, UserRole newRole) {
}
