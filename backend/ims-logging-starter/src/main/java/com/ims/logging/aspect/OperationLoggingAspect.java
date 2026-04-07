package com.ims.logging.aspect;

import com.ims.logging.config.LoggingProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

@Aspect
public class OperationLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLoggingAspect.class);
    private static final Marker OPERATION = MarkerFactory.getMarker("OPERATION");
    private static final List<String> SENSITIVE_FIELDS = List.of("password", "token", "secret", "credential");

    private final LoggingProperties properties;

    public OperationLoggingAspect(LoggingProperties properties) {
        this.properties = properties;
    }

    @Pointcut("execution(public * com.ims.*.application.command..*(..)) " +
              "|| execution(public * com.ims.*.application.query..*(..)) " +
              "|| execution(public * com.ims.*.adapter.in.rest..*(..))")
    public void operationPointcut() {}

    @Around("operationPointcut()")
    public Object logOperation(ProceedingJoinPoint pjp) throws Throwable {
        if (!properties.isOperationEnabled()) {
            return pjp.proceed();
        }

        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        String userId = extractUserId();

        MDC.put("userId", userId);
        MDC.put("operation", className + "." + methodName);

        long start = System.currentTimeMillis();
        log.info(OPERATION, "ENTER [{}.{}] user={} args={}",
                className, methodName, userId, maskSensitiveArgs(args));

        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info(OPERATION, "EXIT [{}.{}] user={} returnType={} durationMs={}",
                    className, methodName, userId,
                    result != null ? result.getClass().getSimpleName() : "void",
                    duration);
            return result;
        } finally {
            MDC.remove("userId");
            MDC.remove("operation");
        }
    }

    private String extractUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {
            // SecurityContext might not be available
        }
        return "anonymous";
    }

    private String maskSensitiveArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String str = arg.toString().toLowerCase();
                    for (String field : SENSITIVE_FIELDS) {
                        if (str.contains(field)) {
                            return "[MASKED]";
                        }
                    }
                    return arg.toString();
                })
                .toList()
                .toString();
    }
}
