package com.example.queries;

import com.example.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/orders")
public class OrderQueryRoutes {

    private OrderQueryHandler handler;

    @Autowired
    public OrderQueryRoutes(OrderQueryHandler orderQueryHandler) {
        this.handler = orderQueryHandler;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ok(handler.handle(new GetAllQuery()));
    }

    @GetMapping("/name")
    public ResponseEntity<List<OrderDTO>> getOrderByName(@RequestParam String name) {
        return ok(handler.handle(new GetOrderByNameQuery(name)));
    }

    @GetMapping("/helloworld")
    public String HelloWorld() {
        return "Hello World";
    }
}
