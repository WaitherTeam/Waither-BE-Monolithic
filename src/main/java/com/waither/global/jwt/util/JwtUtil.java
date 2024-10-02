package com.waither.global.jwt.util;

import com.waither.global.jwt.dto.JwtDto;
import com.waither.global.jwt.execption.SecurityCustomException;
import com.waither.global.jwt.execption.SecurityErrorCode;
import com.waither.global.jwt.userdetails.CustomUserDetails;
import com.waither.global.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final RedisUtil redisUtil;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            RedisUtil redis) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        accessExpMs = access;
        refreshExpMs = refresh;
        redisUtil = redis;
    }

    // JWT 토큰을 입력으로 받아 토큰의 subject에서 사용자 Id를 추출
    public Long getId(String token) throws SignatureException {
        validateToken(token);
        String idString = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(idString);
    }

    // JWT 토큰을 입력으로 받아 토큰의 claim에서 사용자 이메일(email)을 추출
    public String getEmail(String token) throws SignatureException {
        validateToken(token);
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    // JWT 토큰을 입력으로 받아 토큰의 claim에서 사용자 이름(roll)을 추출
    public String getRoles(String token) throws SignatureException{
        validateToken(token);
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // JWT 토큰의 페이로드에서 만료 시간을 검색, 밀리초 단위의 Long 값으로 반환
    public long getExpTime(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .getTime();
    }

    // Token 발급
    public String tokenProvider(CustomUserDetails customUserDetails, Instant expiration) {
        Instant issuedAt = Instant.now();
        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(customUserDetails.getId().toString()) // ID를 subject로 사용
                .claim("email", customUserDetails.getUsername()) // 이메일을 claim으로 추가 (선택사항)
                .claim("role", authorities)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    // principalDetails 객체에 대해 새로운 JWT 액세스 토큰을 생성
    public String createJwtAccessToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(customUserDetails, expiration);
    }

    // principalDetails 객체에 대해 새로운 JWT 리프레시 토큰을 생성
    public String createJwtRefreshToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(customUserDetails, expiration);

        // 레디스에 저장
        redisUtil.save(
                customUserDetails.getUsername(),
                refreshToken,
                refreshExpMs,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    // 제공된 리프레시 토큰을 기반으로 JwtDto 쌍을 다시 발급
    public JwtDto reissueToken(String refreshToken) throws SignatureException {

        // refreshToken에서 user 정보 뽑아서 새로 재 발금 (발급 시간, 유효 시간(reset)만 새로 적용)
        CustomUserDetails userDetails = new CustomUserDetails(
                getId(refreshToken),
                getEmail(refreshToken),
                null,
                getRoles(refreshToken)
        );
        log.info("[*] Token Reissue");

        // 재발급
        return new JwtDto(
                createJwtAccessToken(userDetails),
                createJwtRefreshToken(userDetails)
        );
    }

    // HTTP 요청의 'Authorization' 헤더에서 JWT 액세스 토큰을 검색
    public String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("[*] No Token in req");
            return null;
        }

        log.info("[*] Token exists");

        return authorization.split(" ")[1];
    }

    // 리프레시 토큰의 유효성을 검사 (is it in Redis?)
    public void isRefreshToken(String refreshToken) {
        String email = getEmail(refreshToken);

        String redisRefreshToken = redisUtil.get(email).toString();

        if (!refreshToken.equals(redisRefreshToken)) {
            log.warn("[*] case : redisRefreshToken does not exist");
            throw new SecurityCustomException(SecurityErrorCode.NO_TOKEN_IN_REDIS);
        }
    }

    public void validateToken(String token) {
        try {
            // 구문 분석 시스템의 시계가 JWT를 생성한 시스템의 시계 오차 고려
            // 약 3분 허용.
            long seconds = 3 *60;
            boolean isExpired = Jwts
                    .parser()
                    .clockSkewSeconds(seconds)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
            if (isExpired) {
                throw new SecurityCustomException(SecurityErrorCode.TOKEN_EXPIRED);
            }
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new SecurityCustomException(SecurityErrorCode.INVALID_TOKEN, e);
        } catch (ExpiredJwtException e) {
            throw new SecurityCustomException(SecurityErrorCode.TOKEN_EXPIRED, e);
        }
    }
}
