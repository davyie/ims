package com.ims.logging.config;

import com.ims.logging.aspect.ExceptionLoggingAspect;
import com.ims.logging.aspect.OperationLoggingAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@AutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OperationLoggingAspect operationLoggingAspect(LoggingProperties properties) {
        return new OperationLoggingAspect(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLoggingAspect exceptionLoggingAspect(LoggingProperties properties) {
        return new ExceptionLoggingAspect(properties);
    }
}
