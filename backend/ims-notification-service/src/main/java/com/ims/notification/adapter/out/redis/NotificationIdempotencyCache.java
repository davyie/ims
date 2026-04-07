package com.ims.notification.adapter.out.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class NotificationIdempotencyCache {

    private static final String PREFIX = "notif:sent:";
    private final StringRedisTemplate redisTemplate;
    private final long ttlSeconds;

    public NotificationIdempotencyCache(StringRedisTemplate redisTemplate,
                                        com.ims.notification.infrastructure.config.NotificationProperties properties) {
        this.redisTemplate = redisTemplate;
        this.ttlSeconds = properties.getIdempotencyTtlSeconds();
    }

    /**
     * Returns true if the notification for this eventId has already been sent (i.e., it is a duplicate).
     * Returns false and marks it as sent if it is new.
     */
    public boolean checkAndMark(UUID eventId) {
        String key = PREFIX + eventId.toString();
        Boolean wasAbsent = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(ttlSeconds));
        return Boolean.FALSE.equals(wasAbsent); // true means key already existed → duplicate
    }
}
