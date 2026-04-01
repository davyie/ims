package com.ims.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMarketCommand(
    UUID userId,
    String name,
    String place,
    LocalDateTime openDate,
    LocalDateTime closeDate
) {}
