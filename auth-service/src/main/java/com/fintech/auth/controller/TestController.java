package com.fintech.auth.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/test/secure")
    public String secure() {
        return "SECURE API WORKING";
    }
}
