package com.waither.domain.user.controller;

import com.waither.domain.user.dto.request.OAuthReqDto;
import com.waither.domain.user.service.OAuthService;
import com.waither.global.jwt.dto.JwtDto;
import com.waither.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @Operation(summary = "OAuth 로그인/회원가입")
    @PostMapping("/kakao/login")
    public ApiResponse<?> kakakoLogin(@RequestBody OAuthReqDto.KakaoLoginReqDto loginRequestDto) {
        JwtDto jwtDto = oAuthService.kakaoLogin(loginRequestDto);
        return ApiResponse.onSuccess(jwtDto);
    }
}
