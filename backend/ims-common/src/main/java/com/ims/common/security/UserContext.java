package com.ims.common.security;

import java.util.UUID;

public record UserContext(UUID userId, String username, String role) {
}
