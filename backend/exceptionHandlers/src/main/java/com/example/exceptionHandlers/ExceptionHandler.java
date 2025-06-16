package com.example.exceptionHandlers;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @AfterThrowing(
            pointcut = "com.example.warehouse.domain.WarehouseItemQuantity.decrementQuantity(Integer value)",
    throwing = "IllegalStateException")
    public void doRecovery() {
        logger.info("This is from ExceptionHandler");
    }
}
