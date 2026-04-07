package com.ims.user.domain.event;

import java.util.UUID;

public record UserUpdatedEvent(UUID userId, String username, String email) {
}
