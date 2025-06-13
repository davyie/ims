package com.example.routes;

import com.example.DTO.ProductDTO;
import com.example.RateLimited;
import com.example.queries.GetAllProductQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/query")
public class QueryRoutes {

    private GetAllProductQuery getAllProductQuery;

    @Autowired
    public QueryRoutes(GetAllProductQuery gapq) {
        this.getAllProductQuery = gapq;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/get/all")
    @RateLimited
    public List<ProductDTO> getAllProducts() {
        return getAllProductQuery.run();
    }
}
