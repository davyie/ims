package com.ims.scheduling.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.common.exception.ValidationException;
import com.ims.scheduling.application.job.MarketScheduleJob;
import com.ims.scheduling.domain.model.MarketSchedule;
import com.ims.scheduling.domain.model.ScheduleAction;
import com.ims.scheduling.domain.model.ScheduleStatus;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class SchedulingService {

    private static final Logger log = LoggerFactory.getLogger(SchedulingService.class);

    private final com.ims.scheduling.adapter.out.persistence.ScheduleJpaRepository scheduleRepository;
    private final Scheduler quartzScheduler;

    public SchedulingService(com.ims.scheduling.adapter.out.persistence.ScheduleJpaRepository scheduleRepository,
                              Scheduler quartzScheduler) {
        this.scheduleRepository = scheduleRepository;
        this.quartzScheduler = quartzScheduler;
    }

    public MarketSchedule createSchedule(UUID marketId, ScheduleAction action, Instant scheduledAt, UUID userId) {
        if (scheduledAt.isBefore(Instant.now())) {
            throw new ValidationException("Scheduled time must be in the future");
        }

        MarketSchedule schedule = MarketSchedule.builder()
                .marketId(marketId)
                .action(action)
                .scheduledAt(scheduledAt)
                .userId(userId)
                .status(ScheduleStatus.PENDING)
                .build();

        MarketSchedule saved = scheduleRepository.save(schedule);

        // Schedule Quartz job
        String jobKey = "market-schedule-" + saved.getScheduleId();

        JobDetail jobDetail = JobBuilder.newJob(MarketScheduleJob.class)
                .withIdentity(jobKey, "market-schedules")
                .usingJobData("scheduleId", saved.getScheduleId().toString())
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobKey + "-trigger", "market-schedules")
                .startAt(Date.from(scheduledAt))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        try {
            quartzScheduler.scheduleJob(jobDetail, trigger);
            saved.setQuartzJobKey(jobKey);
            saved = scheduleRepository.save(saved);
            log.info("Scheduled job {} for market {} at {}", jobKey, marketId, scheduledAt);
        } catch (SchedulerException e) {
            log.error("Failed to schedule Quartz job: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to schedule job", e);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public PageResponse<MarketSchedule> listSchedules(int page, int size) {
        Page<MarketSchedule> result = scheduleRepository.findAll(PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    public void cancelSchedule(UUID scheduleId, UUID requestingUserId) {
        MarketSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("MarketSchedule", scheduleId));

        if (schedule.getStatus() != ScheduleStatus.PENDING) {
            throw new ValidationException("Can only cancel PENDING schedules");
        }

        schedule.setStatus(ScheduleStatus.CANCELLED);
        scheduleRepository.save(schedule);

        if (schedule.getQuartzJobKey() != null) {
            try {
                quartzScheduler.deleteJob(new JobKey(schedule.getQuartzJobKey(), "market-schedules"));
            } catch (SchedulerException e) {
                log.warn("Failed to delete Quartz job {}: {}", schedule.getQuartzJobKey(), e.getMessage());
            }
        }
    }
}
