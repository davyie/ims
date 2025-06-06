package com.example.routes;

import com.example.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cqrs/api")
public class Routes {

    @GetMapping("/")
    public String HelloWorld() {
        return "This is from the CQRS pattern";
    }
}
