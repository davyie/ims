package com.example.queries;

import com.example.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderQueryHandler implements QueryHandler<List<OrderDTO>, Query<List<OrderDTO>>> {

    private OrderRepository orderRepository;

    @Autowired
    public OrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDTO> handle(Query<List<OrderDTO>> query) {
        if (query instanceof GetOrderByNameQuery) {
            return orderRepository.getAll().stream().filter(e -> e.getName().equals(((GetOrderByNameQuery) query).getName())).map(OrderMapper::toDTO).toList();
        } else if (query instanceof GetAllQuery){
            return orderRepository.getAll().stream().map(OrderMapper::toDTO).toList();
        } else {
            return null;
        }
    }
}
