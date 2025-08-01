package com.example;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "example.queue";
    public static final String EXCHANGE_NAME = "example.exchange";
    public static final String ROUTING_KEY = "example.routingkey";

    public static final String OTHER_QUEUE_NAME = "other.queue";
    public static final String OTHER_ROUTING_KEY = "other.routingKey";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // durable
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    /**
     * Other queue which use the same exchange but other routingkey.
     * @return
     */
    @Bean
    public Queue otherQueue() {
        return new Queue(OTHER_QUEUE_NAME, true);
    }

    @Bean
    public Binding otherBinding(Queue otherQueue, TopicExchange exchange) {
        return BindingBuilder.bind(otherQueue).to(exchange).with(OTHER_ROUTING_KEY);
    }
}
