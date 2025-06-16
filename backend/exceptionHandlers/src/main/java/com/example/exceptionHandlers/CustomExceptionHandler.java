package com.example.exceptionHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity doRecovery() {
        logger.info("This is from ExceptionHandler, sending the status {}", HttpStatus.NOT_ACCEPTABLE);
        return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
    }
}
