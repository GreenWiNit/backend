package com.example.green.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    @GetMapping(value = "/")
    public String doGetHelloWorld() {
        return "Hello World";
    }
}
