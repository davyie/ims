package com.ims.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ims.user")
public class UserProperties {

    private String jwtSecret = "change-me-in-production-minimum-32-chars";
    private int jwtExpiryMinutes = 60;
    private int passwordMinLength = 12;
    private String kafkaTopicEvents = "ims.user.events";

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public int getJwtExpiryMinutes() {
        return jwtExpiryMinutes;
    }

    public void setJwtExpiryMinutes(int jwtExpiryMinutes) {
        this.jwtExpiryMinutes = jwtExpiryMinutes;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public String getKafkaTopicEvents() {
        return kafkaTopicEvents;
    }

    public void setKafkaTopicEvents(String kafkaTopicEvents) {
        this.kafkaTopicEvents = kafkaTopicEvents;
    }
}
