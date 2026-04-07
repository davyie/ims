package com.ims.user.application.command;

import com.ims.user.domain.model.UserRole;

public record CreateUserCommand(String username, String email, String password, UserRole role) {
}
