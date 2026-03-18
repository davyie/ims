package com.ims.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.ims.application..*(..)) || execution(* com.ims.domain..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String method = joinPoint.getSignature().toShortString();
        log.debug("Entering: {}", method);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.debug("Exiting: {} ({}ms)", method, elapsed);
            return result;
        } catch (Throwable t) {
            log.warn("Exception in {}: {}", method, t.getMessage());
            throw t;
        }
    }
}
