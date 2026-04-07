package com.ims.gateway.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ims.gateway")
public class GatewayProperties {

    private String jwtSecret = "change-me-in-production-minimum-32-chars";

    private String userServiceUrl = "http://localhost:8081";
    private String warehouseServiceUrl = "http://localhost:8082";
    private String marketServiceUrl = "http://localhost:8083";
    private String transferServiceUrl = "http://localhost:8084";
    private String schedulingServiceUrl = "http://localhost:8085";
    private String reportingServiceUrl = "http://localhost:8086";
    private String transactionServiceUrl = "http://localhost:8087";
}
