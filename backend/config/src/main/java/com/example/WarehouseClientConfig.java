package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WarehouseClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
