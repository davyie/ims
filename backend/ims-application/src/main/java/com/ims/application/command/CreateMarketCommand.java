package com.ims.application.command;

import java.time.LocalDate;

public record CreateMarketCommand(
    String name,
    String place,
    LocalDate openDate,
    LocalDate closeDate
) {}
