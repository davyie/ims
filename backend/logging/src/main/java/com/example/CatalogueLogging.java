package com.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CatalogueLogging {
    private static final Logger logger = LoggerFactory.getLogger(CatalogueLogging.class);

    @Before("execution(* com.example.routes.*.*(..))")
    public void logAllRoutes(JoinPoint joinPoint) {
        logger.info("com.example.routes is working...");
    }

    @Before("execution(* com.example.routes.CommandRoutes.*(..))")
    public void logCommandRoute(JoinPoint joinPoint) {
        logger.info("Running CommandRoutes {}", joinPoint.getArgs());
    }

    @Before("execution(* com.example.commands.CreateProductCommand.*(..)")
    public void logCommand(JoinPoint joinPoint) {
        logger.info("Executing command: {}", joinPoint.getSignature());
    }
}
