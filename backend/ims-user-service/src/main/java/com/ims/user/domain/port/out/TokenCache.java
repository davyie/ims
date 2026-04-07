package com.ims.user.domain.port.out;

import java.time.Duration;
import java.util.UUID;

public interface TokenCache {

    void cacheToken(String token, UUID userId, Duration ttl);

    void invalidateToken(String token);

    boolean isTokenBlacklisted(String token);
}
