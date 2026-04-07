package com.ims.scheduling.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.scheduling.application.service.SchedulingService;
import com.ims.scheduling.domain.model.MarketSchedule;
import com.ims.scheduling.domain.model.ScheduleAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final SchedulingService schedulingService;

    public ScheduleController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    record CreateScheduleRequest(@NotNull UUID marketId, @NotNull ScheduleAction action,
                                  @NotNull Instant scheduledAt) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketSchedule createSchedule(@Valid @RequestBody CreateScheduleRequest request,
                                          Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return schedulingService.createSchedule(request.marketId(), request.action(), request.scheduledAt(), userId);
    }

    @GetMapping
    public PageResponse<MarketSchedule> listSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return schedulingService.listSchedules(page, size);
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelSchedule(@PathVariable UUID scheduleId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        schedulingService.cancelSchedule(scheduleId, userId);
    }
}
