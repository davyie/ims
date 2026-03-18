package com.ims.infrastructure.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${ims.kafka.topics.items:ims.items}")
    private String itemsTopic;

    @Value("${ims.kafka.topics.markets:ims.markets}")
    private String marketsTopic;

    @Value("${ims.kafka.topics.market-stock:ims.market-stock}")
    private String marketStockTopic;

    @Bean
    public NewTopic itemsTopic() {
        return TopicBuilder.name(itemsTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic marketsTopic() {
        return TopicBuilder.name(marketsTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic marketStockTopic() {
        return TopicBuilder.name(marketStockTopic).partitions(1).replicas(1).build();
    }
}
