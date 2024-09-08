package com.waither.domain.user.service;

import com.waither.domain.user.dto.request.OAuthReqDto;
import com.waither.domain.user.dto.response.OAuthResDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.exception.UserErrorCode;
import com.waither.domain.user.exception.UserException;
import com.waither.domain.user.repository.UserRepository;
import com.waither.domain.user.service.commandService.UserService;
import com.waither.global.jwt.dto.JwtDto;
import com.waither.global.jwt.userdetails.CustomUserDetails;
import com.waither.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OAuthService {

    private final UserService userService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String KAKAO_ACCESS_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";

    public JwtDto kakaoLogin(OAuthReqDto.KakaoLoginReqDto kakaoLoginReqDto) {
        // Kakao AccessToken 검증
        validateKakaoToken(kakaoLoginReqDto.accessToken());

        // 사용자 조회 또는 생성
        User user = findOrCreateUser(kakaoLoginReqDto);

        // JWT 토큰 발급
        return provideTokenForOAuth(user.getEmail());
    }

    public void validateKakaoToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    KAKAO_ACCESS_TOKEN_INFO_URL,
                    HttpMethod.GET,
                    entity,
                    OAuthResDto.KakaoTokenInfo.class
            );
        } catch (HttpClientErrorException e) {
            throw new UserException(UserErrorCode.INVALID_KAKAO_TOKEN);
        }
    }


    private User findOrCreateUser(OAuthReqDto.KakaoLoginReqDto kakaoLoginReqDto) {
        Optional<User> optionalUser = userRepository.findByEmail(kakaoLoginReqDto.email());

        if (optionalUser.isPresent()) {
            return validateExistingUser(optionalUser.get(), kakaoLoginReqDto);
        } else {
            return userService.signupForKakao(kakaoLoginReqDto);
        }
    }

    private User validateExistingUser(User user, OAuthReqDto.KakaoLoginReqDto kakaoLoginReqDto) {
        if (user.getAuthId() == null) {
            // 이미 "이메일"로 가입된 경우 예외 발생
            throw new UserException(UserErrorCode.CONFLICT_EXISTING_EMAIL);
        }
        return user;
    }

    public JwtDto provideTokenForOAuth(String email) {
        CustomUserDetails customUserDetails = new CustomUserDetails(email, null, "ROLE_USER");
        return new JwtDto(
                jwtUtil.createJwtAccessToken(customUserDetails),
                jwtUtil.createJwtRefreshToken(customUserDetails));
    }
}