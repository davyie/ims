package com.ims.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UpdateMarketRequest(
    @NotBlank String name,
    @NotBlank String place,
    @NotNull LocalDateTime openDate,
    @NotNull LocalDateTime closeDate
) {}
