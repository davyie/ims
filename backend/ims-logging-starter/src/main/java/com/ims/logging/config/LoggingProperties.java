package com.ims.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ims.logging")
public class LoggingProperties {

    private boolean operationEnabled = true;
    private boolean exceptionEnabled = true;

    public boolean isOperationEnabled() {
        return operationEnabled;
    }

    public void setOperationEnabled(boolean operationEnabled) {
        this.operationEnabled = operationEnabled;
    }

    public boolean isExceptionEnabled() {
        return exceptionEnabled;
    }

    public void setExceptionEnabled(boolean exceptionEnabled) {
        this.exceptionEnabled = exceptionEnabled;
    }
}
