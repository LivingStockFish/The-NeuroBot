package com.genuinecoder.aiassistant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/greeting")
    public String greet() {
        return "Hello from NeuroBot API!";
    }

    @GetMapping("/status")
    public String getStatus() {
        return "{\"status\":\"running\", \"service\":\"NeuroBot API\"}";
    }
}
