package com.ims.user.adapter.in.rest.dto;

import com.ims.user.domain.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(@NotNull UserRole newRole) {
}
