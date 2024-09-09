package project.Logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * This function is used to log Hello World.
     * @param joinPoint
     */
    @Before("execution(* com.ims.crochet_v2.Application.Controller.*(..))")
    public void logBeforeHelloWorld(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    // Log after a method has successfully executed
    @After("execution(* com.ims.crochet_v2.Application.*.*(..))")
    public void logAfterController(JoinPoint joinPoint) {
        logger.info("Exiting method: {}", joinPoint.getSignature());
    }

    // Log around method execution (before and after, and potentially log execution time)
    @Around("execution(* com.ims.crochet_v2.Application.*.*(..))")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;

        logger.info("Starting method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
        try {
            result = joinPoint.proceed();  // Execute the method
        } catch (Throwable e) {
            logger.error("Exception in method: {} with message: {}", joinPoint.getSignature(), e.getMessage());
            throw e;
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Method: {} executed in {} ms", joinPoint.getSignature(), timeTaken);

        return result;
    }

    // Log exceptions thrown by any method
    @AfterThrowing(pointcut = "execution(* com.ims.crochet_v2.*.*.*(..))", throwing = "exception")
    public void logExceptions(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), exception.getMessage());
    }

    @Before("execution(* com.ims.crochet_v2.Domain.*.*(..))")
    public void logBeforeDomain(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @Before("execution(* com.ims.crochet_v2.Infrastructure.*.*(..))")
    public void logBeforeInfrastructure(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @Before("execution(* com.ims.crochet_v2.Application.Handlers.*(..))")
    public void logBeforeCommandHandler(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @Before("execution(* com.ims.crochet_v2.Aspect.*.*(..))")
    public void logBeforeRateLimitAspect(JoinPoint joinPoint) {
        logger.info("Entering RateLimitAspect: {} with arguments {}", joinPoint.getSignature(), joinPoint.getArgs());
    }
}
