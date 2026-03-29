package com.ims.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateMarketRequest(
    @NotBlank String name,
    @NotBlank String place,
    @NotNull LocalDate openDate,
    @NotNull LocalDate closeDate
) {}
