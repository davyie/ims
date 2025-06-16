package com.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingMarket {

    private final Logger logger = LoggerFactory.getLogger(LoggingMarket.class);

    @Before(value = "execution(* com.example.market.routes.*.*(..))")
    public void logRoutes(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.toShortString();

        logger.info("Calling method: {}", methodName);
        for (Object arg : args) {
            logger.info("Arg: {}", arg); // toString() is called here
        }
    }

    @Before(value = "execution(* com.example.market.commands.*.*(..))")
    public void logCommands(JoinPoint joinPoint) {
        logger.info("Running following method: {}", joinPoint.getSignature());
    }
}
