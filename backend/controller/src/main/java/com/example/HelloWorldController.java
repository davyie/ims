package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/")
    public String HelloWorld() {
        return "Hello World!";
    }

    @RateLimited
    @GetMapping("/home")
    public String GoHome() {
        return "This is home";
    }
}
