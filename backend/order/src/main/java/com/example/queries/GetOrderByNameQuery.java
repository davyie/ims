package com.example.queries;


import com.example.OrderDTO;
import com.example.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderByNameQuery implements Query<List<OrderDTO>> {
    private String name;
}
