package com.example.routes;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cqrs/api")
public class Routes {

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/")
    public String HelloWorld() {
        return "This is from the CQRS pattern";
    }
}
