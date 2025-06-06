package com.example.commands;

import com.example.CommandHandler;
import com.example.OrderEntity;
import com.example.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandHandler implements CommandHandler<CreateOrderResult, CreateOrderCommand> {

    private OrderRepository orderRepository;

    @Autowired
    public OrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CreateOrderResult handle(CreateOrderCommand command) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setName(command.getName());
        orderEntity.setQuantity(command.getQuantity());
        orderEntity.setAmount(command.getAmount());
        orderEntity.setStatus(command.getStatus());
        orderRepository.save(orderEntity);
        // Add event sourcing here.
        return new CreateOrderResult(orderEntity.getName());
    }
}
