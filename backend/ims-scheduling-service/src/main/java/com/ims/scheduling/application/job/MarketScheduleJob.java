package com.ims.scheduling.application.job;

import com.ims.common.event.EventEnvelope;
import com.ims.scheduling.domain.model.MarketSchedule;
import com.ims.scheduling.domain.model.ScheduleAction;
import com.ims.scheduling.domain.model.ScheduleStatus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class MarketScheduleJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(MarketScheduleJob.class);
    private static final String MARKET_COMMANDS_TOPIC = "ims.market.commands";

    @Autowired
    private KafkaTemplate<String, EventEnvelope> kafkaTemplate;

    @Autowired
    private com.ims.scheduling.adapter.out.persistence.ScheduleJpaRepository scheduleRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String scheduleIdStr = context.getJobDetail().getJobDataMap().getString("scheduleId");
        if (scheduleIdStr == null) {
            log.error("No scheduleId in job data map");
            return;
        }

        UUID scheduleId = UUID.fromString(scheduleIdStr);
        MarketSchedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (schedule == null) {
            log.error("Schedule not found: {}", scheduleId);
            return;
        }

        if (schedule.getStatus() == ScheduleStatus.CANCELLED) {
            log.info("Schedule {} was cancelled, skipping", scheduleId);
            return;
        }

        try {
            String eventType = schedule.getAction() == ScheduleAction.OPEN
                    ? "OPEN_MARKET_COMMAND"
                    : "CLOSE_MARKET_COMMAND";

            EventEnvelope command = EventEnvelope.of(
                    eventType,
                    "ims-scheduling-service",
                    schedule.getUserId(),
                    Map.of("marketId", schedule.getMarketId().toString())
            );

            kafkaTemplate.send(MARKET_COMMANDS_TOPIC, schedule.getMarketId().toString(), command);

            schedule.setStatus(ScheduleStatus.FIRED);
            scheduleRepository.save(schedule);

            log.info("Fired schedule {} for market {} action {}", scheduleId, schedule.getMarketId(), schedule.getAction());
        } catch (Exception e) {
            log.error("Error firing schedule {}: {}", scheduleId, e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }
}
