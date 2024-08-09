package com.waither.domain.noti.controller;

import com.waither.domain.noti.dto.request.TokenDto;
import com.waither.domain.noti.service.AlarmService;
import com.waither.global.annotation.AuthUser;
import com.waither.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/noti")
@RestController
public class TokenContoller {

    private final AlarmService alarmService;

    @Operation(summary = "Firebase Token 업데이트", description = "Request Body에 발급한 FCM토큰 값을 넣어서 주시면 됩니다.")
    @PostMapping("/token")
    public ApiResponse<?> updateToken(@AuthUser String email, @RequestBody TokenDto tokenDto) {
        alarmService.updateToken(email, tokenDto);
        return ApiResponse.onSuccess("토큰 업로드가 완료되었습니다.");
    }
}
