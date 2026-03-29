package com.ims.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateMarketCommand(
    UUID marketId,
    String name,
    String place,
    LocalDate openDate,
    LocalDate closeDate
) {}
