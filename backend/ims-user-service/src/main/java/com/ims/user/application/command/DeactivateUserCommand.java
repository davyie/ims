package com.ims.user.application.command;

import java.util.UUID;

public record DeactivateUserCommand(UUID userId) {
}
