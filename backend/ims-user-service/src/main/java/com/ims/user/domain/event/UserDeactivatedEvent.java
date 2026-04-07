package com.ims.user.domain.event;

import java.util.UUID;

public record UserDeactivatedEvent(UUID userId, String username) {
}
