package com.waither.domain.noti.controller;

import com.waither.global.response.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/health")
public class HealthCheck {

    @GetMapping("")
    public ApiResponse<?> healthCheck() {
        return ApiResponse.onSuccess(null);
    }
}
