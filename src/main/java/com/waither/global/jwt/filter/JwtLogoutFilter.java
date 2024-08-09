package com.waither.global.jwt.filter;

import com.waither.global.jwt.execption.SecurityCustomException;
import com.waither.global.jwt.execption.SecurityErrorCode;
import com.waither.global.jwt.util.JwtUtil;
import com.waither.global.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
@Slf4j
public class JwtLogoutFilter implements LogoutHandler {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            log.info("[*] Logout Filter");

            String accessToken = jwtUtil.resolveAccessToken(request);

            String email = jwtUtil.getEmail(accessToken);

            // RefreshToken 삭제
            redisUtil.delete(email);

        } catch (ExpiredJwtException e) {
            log.warn("[*] case : AccessToken expired");
            throw new SecurityCustomException(SecurityErrorCode.TOKEN_EXPIRED);
        }
    }
}
