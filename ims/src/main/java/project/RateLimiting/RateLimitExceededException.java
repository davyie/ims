package project.RateLimiting;

public class RateLimitExceededException extends Exception{
    public RateLimitExceededException(String errorMessage) {
        super(errorMessage);
    }
}
