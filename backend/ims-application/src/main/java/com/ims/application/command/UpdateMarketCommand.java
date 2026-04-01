package com.ims.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateMarketCommand(
    UUID userId,
    UUID marketId,
    String name,
    String place,
    LocalDateTime openDate,
    LocalDateTime closeDate
) {}
