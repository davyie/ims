package com.example.commands;

import com.example.Command;
import com.example.OrderDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateOrderCommand extends OrderDTO implements Command<CreateOrderResult>  {
    public CreateOrderCommand() {
        super();
    }
}
