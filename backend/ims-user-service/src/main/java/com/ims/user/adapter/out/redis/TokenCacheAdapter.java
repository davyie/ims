package com.ims.user.adapter.out.redis;

import com.ims.user.domain.port.out.TokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class TokenCacheAdapter implements TokenCache {

    private static final Logger log = LoggerFactory.getLogger(TokenCacheAdapter.class);
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String ACTIVE_PREFIX = "token:active:";

    private final RedisTemplate<String, String> redisTemplate;

    public TokenCacheAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void cacheToken(String token, UUID userId, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(ACTIVE_PREFIX + token, userId.toString(), ttl);
        } catch (Exception e) {
            log.warn("Failed to cache token for userId {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void invalidateToken(String token) {
        try {
            redisTemplate.delete(ACTIVE_PREFIX + token);
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", Duration.ofHours(24));
        } catch (Exception e) {
            log.warn("Failed to invalidate token: {}", e.getMessage());
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        } catch (Exception e) {
            log.warn("Failed to check token blacklist: {}", e.getMessage());
            return false;
        }
    }
}
