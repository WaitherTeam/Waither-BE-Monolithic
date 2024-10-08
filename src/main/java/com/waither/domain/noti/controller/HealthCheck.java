package com.waither.domain.noti.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/health")
public class HealthCheck {

    @GetMapping("")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("UP");
    }
}
