package com.waither.global.jwt.filter;

import com.waither.global.jwt.userdetails.CustomUserDetails;
import com.waither.global.jwt.util.HttpResponseUtil;
import com.waither.global.jwt.util.JwtUtil;
import com.waither.global.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = jwtUtil.resolveAccessToken(request);
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            //logout 처리된 accessToken
            if (redisUtil.get(accessToken) != null && redisUtil.get(accessToken).equals("logout")) {
                logger.info("[*] Logout accessToken");
                filterChain.doFilter(request, response);
                return;
            }

            authenticateAccessToken(accessToken);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            try {
                HttpResponseUtil.setErrorResponse(response, HttpStatus.UNAUTHORIZED, "엑세스 토큰이 유효하지 않습니다.");
            } catch (IOException ex) {
                log.error("IOException occurred while setting error response: {}", ex.getMessage());
            }
            log.warn("[*] case : accessToken Expired");
        }

    }

    private void authenticateAccessToken(String accessToken) {

        //AccessToken 유효성 검증
        jwtUtil.validateToken(accessToken);

        //CustomUserDetail 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                jwtUtil.getId(accessToken),
                jwtUtil.getEmail(accessToken),
                null,
                jwtUtil.getRoles(accessToken)
        );

        // Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        // SecurityContextHolder 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}