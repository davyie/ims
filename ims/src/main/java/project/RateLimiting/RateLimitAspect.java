package project.RateLimiting;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private RateLimitConfig rateLimitConfig;

    private RateLimiter rateLimiter;

    @Autowired
    public RateLimitAspect(RateLimitConfig rateLimitConfig, RateLimiter rateLimiter) {
        this.rateLimitConfig = rateLimitConfig;
        this.rateLimiter = rateLimiter;
    }

    @Around("@annotation(RateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = getKey(joinPoint);
        System.out.println(key);
        if (!rateLimiter.tryAcquire(key,
                rateLimitConfig.getRequests(),
                rateLimitConfig.getSeconds())) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }
        return joinPoint.proceed();
    }

    private String getKey(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }
}
