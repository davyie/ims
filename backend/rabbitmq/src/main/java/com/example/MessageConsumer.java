package com.example;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.stereotype.Service;


@Service
public class MessageConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println(String.format("Message received! %s", message));
    }
}
