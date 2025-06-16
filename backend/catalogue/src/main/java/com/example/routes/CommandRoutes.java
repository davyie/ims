package com.example.routes;

import com.example.DTO.ProductDTO;
import com.example.commands.CreateProductCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/com/example/commands")
public class CommandRoutes {

    private CreateProductCommand cpc;

    @Autowired
    public CommandRoutes(CreateProductCommand cpc) {
        this.cpc = cpc;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/")
    public ProductDTO addProduct(@RequestBody ProductDTO productdto) {
        return cpc.createProduct(productdto);
    }
}
