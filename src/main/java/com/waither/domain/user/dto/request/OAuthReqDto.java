package com.waither.domain.user.dto.request;

public class OAuthReqDto {

    public record KakaoLoginReqDto(
            String accessToken,
            Long authId,
            String email,
            String nickname
    ) { }
}
