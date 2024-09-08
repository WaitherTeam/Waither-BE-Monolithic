package com.waither.domain.user.converter;

import com.waither.domain.user.dto.request.OAuthReqDto;
import com.waither.domain.user.dto.request.UserReqDto;
import com.waither.domain.user.dto.response.KakaoResDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.entity.enums.UserStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConverter {

    public static User toUser(UserReqDto.SignUpRequestDto requestDto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .nickname("추워하는 곰탱이")
                .status(UserStatus.ACTIVE)
                .role("ROLE_USER")
                .custom(true)
                .build();
    }

    public static User toUser(OAuthReqDto.KakaoLoginReqDto kakaoLoginReqDto) {
        return User.builder()
                .authId(kakaoLoginReqDto.authId())
                .nickname(kakaoLoginReqDto.nickname())
                .email(kakaoLoginReqDto.email())
                .status(UserStatus.ACTIVE)
                .custom(true)
                .role("ROLE_USER")
                .build();
    }
}
