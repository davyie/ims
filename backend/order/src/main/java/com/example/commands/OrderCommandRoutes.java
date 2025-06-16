package com.example.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/command")
public class OrderCommandRoutes {

    private OrderCommandHandler handler;

    @Autowired
    public OrderCommandRoutes(OrderCommandHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/")
    public ResponseEntity<CreateOrderResult> createOrder(@RequestBody CreateOrderCommand command) {
        return ok(handler.handle(command));
    }
}
