package com.fintech.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class RoleTestController {

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user")
    public String user() {
        return "USER ACCESS OK";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "ADMIN ACCESS OK";
    }
}
