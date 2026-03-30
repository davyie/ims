package com.ims.application.command;

import java.time.LocalDateTime;

public record CreateMarketCommand(
    String name,
    String place,
    LocalDateTime openDate,
    LocalDateTime closeDate
) {}
