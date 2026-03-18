package com.ims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.ims.domain",
    "com.ims.application",
    "com.ims.infrastructure",
    "com.ims.api",
    "com.ims"
})
public class ImsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImsApplication.class, args);
    }
}
