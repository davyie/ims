package com.ims.user.application.command;

import java.util.UUID;

public record UpdateUserCommand(UUID userId, String username, String email) {
}
