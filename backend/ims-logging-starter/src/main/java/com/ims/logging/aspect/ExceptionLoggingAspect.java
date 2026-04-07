package com.ims.logging.aspect;

import com.ims.logging.config.LoggingProperties;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
public class ExceptionLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ExceptionLoggingAspect.class);
    private static final Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");

    private final LoggingProperties properties;

    public ExceptionLoggingAspect(LoggingProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(public * com.ims.*.*(..))")
    public void allPublicMethods() {}

    @AfterThrowing(pointcut = "allPublicMethods()", throwing = "ex")
    public void logException(JoinPoint jp, Throwable ex) {
        if (!properties.isExceptionEnabled()) {
            return;
        }

        String className = jp.getSignature().getDeclaringTypeName();
        String methodName = jp.getSignature().getName();
        String userId = extractUserId();

        Throwable rootCause = getRootCause(ex);
        log.error(EXCEPTION_MARKER,
                "EXCEPTION [{}.{}] user={} exceptionClass={} message={} rootCause={}",
                className, methodName, userId,
                ex.getClass().getName(),
                ex.getMessage(),
                rootCause.getMessage());
    }

    private String extractUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return "anonymous";
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
