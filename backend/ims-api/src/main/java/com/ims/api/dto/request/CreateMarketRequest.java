package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Request to create a new market")
public record CreateMarketRequest(
    @NotBlank @Schema(example = "Spring Market 2025") String name,
    @NotBlank @Schema(example = "Berlin") String place,
    @NotNull @Schema(example = "2025-03-01") LocalDate openDate,
    @NotNull @Schema(example = "2025-03-03") LocalDate closeDate
) {}
