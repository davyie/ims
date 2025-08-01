package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class MessageConsumer {

    private Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        logger.info(String.format("Message %s received from queue %s ", message, RabbitMQConfig.QUEUE_NAME));
    }

    @RabbitListener(queues = RabbitMQConfig.OTHER_QUEUE_NAME)
    public void receiveOtherMessage(String message) {
        logger.info(String.format("Message %s received from queue %s", message, RabbitMQConfig.OTHER_QUEUE_NAME));
    }
}
