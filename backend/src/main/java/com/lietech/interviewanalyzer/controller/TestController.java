package com.lietech.interviewanalyzer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/public")
    public String publicEndpoint() {
        return "This is a public endpoint";
    }

    @GetMapping("/api/test/secure")
    public String secureEndpoint() {
        return "This is a secured endpoint";
    }
}