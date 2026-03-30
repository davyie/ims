package com.ims.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(UUID id, String name, LocalDateTime createdAt) {}
