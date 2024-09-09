package project.RateLimiting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.requests}")
    @Getter
    @Setter
    private int requests;

    @Value("${rate.limit.seconds}")
    @Getter
    @Setter
    private int seconds;
}
