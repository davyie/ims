package com.ims.user.domain.event;

import java.util.UUID;

public record UserPasswordChangedEvent(UUID userId, String username) {
}
