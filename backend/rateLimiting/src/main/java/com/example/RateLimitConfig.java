package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties("rate")
public class RateLimitConfig {

    private int requests;

    private int seconds;

}
