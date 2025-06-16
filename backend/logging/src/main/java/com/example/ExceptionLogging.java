package com.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLogging {
    private final Logger logger = LoggerFactory.getLogger(ExceptionLogging.class);

    @Before(value = "execution(* com.example.exceptionHandlers.*.*(..)")
    public void logBeforeException(JoinPoint joinPoint) {
        logger.info("Log exception: {}", joinPoint.getSignature());
    }
}
