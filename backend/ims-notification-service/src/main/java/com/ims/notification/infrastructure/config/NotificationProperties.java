package com.ims.notification.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ims.notification")
public class NotificationProperties {

    private String fromEmail = "noreply@ims.local";
    private boolean emailEnabled = true;
    private int maxRetries = 3;
    private long retryBackoffMs = 1000;
    /** Redis TTL in seconds for idempotency keys (24 hours) */
    private long idempotencyTtlSeconds = 86400;
}
