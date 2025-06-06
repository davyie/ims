package com.example.routes;

import com.example.DTO.ProductDTO;
import com.example.commands.CreateProductCommand;
import com.example.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commands")
public class CommandRoutes {

    private CreateProductCommand cpc;

    @Autowired
    public CommandRoutes(CreateProductCommand cpc) {
        this.cpc = cpc;
    }

    @PostMapping("/")
    public ProductDTO addProduct(@RequestBody ProductDTO productdto) {
        return cpc.createProduct(productdto);
    }
}
