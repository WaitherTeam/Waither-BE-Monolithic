package com.waither.global.jwt.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}