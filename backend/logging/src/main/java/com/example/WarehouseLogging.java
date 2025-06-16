package com.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WarehouseLogging {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseLogging.class);

    @Before("execution(* com.example.warehouse.routes.WarehouseController.*(..))")
    public void logController(JoinPoint joinPoint) {
        logger.info("This logs before running the following method: {} ", joinPoint.getSignature());
        logger.info("The arguments are: {}", joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.example.warehouse.routes.WarehouseController.*(..))", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        logger.info("After running method: {}, we return {}", joinPoint.getSignature().getName(), result);
    }
}
