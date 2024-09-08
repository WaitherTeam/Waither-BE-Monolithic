package com.waither.domain.user.dto.response;

import lombok.Getter;

public class OAuthResDto {

    @Getter
    public static class KakaoTokenInfo {
        private long id;
        private int expiresIn;
        private int appId;
    }
}