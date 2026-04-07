package com.ims.user.application.command;

import com.ims.user.domain.model.UserRole;

import java.util.UUID;

public record AssignRoleCommand(UUID userId, UserRole newRole) {
}
