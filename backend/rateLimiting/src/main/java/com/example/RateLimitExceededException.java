package com.example;

public class RateLimitExceededException extends Exception{
    public RateLimitExceededException(String errorMessage) {
        super(errorMessage);
    }
}
