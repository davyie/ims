package com.ims.infrastructure.aop;

import com.ims.infrastructure.config.Monitored;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);
    private static final long THRESHOLD_MS = 2000;

    @Around("@annotation(monitored)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint, Monitored monitored) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed > THRESHOLD_MS) {
            log.warn("Performance threshold exceeded: {} took {}ms (threshold: {}ms)",
                joinPoint.getSignature().toShortString(), elapsed, THRESHOLD_MS);
        }
        return result;
    }
}
