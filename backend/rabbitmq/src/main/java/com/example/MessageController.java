package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbitmq")
public class MessageController {

    private final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @GetMapping("/send")
    public String message(@RequestParam String message) {
        messageProducer.sendMessage(message);
        return "Message sent to server";
    }

    @GetMapping("/hello")
    public String HelloWorld() {
        return "Hello world from Message Controller";
    }
}
