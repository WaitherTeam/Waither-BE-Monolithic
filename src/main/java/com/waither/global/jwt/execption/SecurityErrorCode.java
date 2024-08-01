package com.waither.global.jwt.execption;

import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.global.response.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements BaseErrorCode {

    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SEC400_0", "잘못된 형식의 토큰입니다."),
    NO_TOKEN_IN_REDIS(HttpStatus.BAD_REQUEST, "SEC400_0","Redis에 해당하는 RefreshToken이 없습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SEC401_0", "승인되지 않은 사용자 입니다."),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "SEC401_1", "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해주세요."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "SEC401_1", "토큰이 만료되었습니다."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "SEC403_0", "권한이 없습니다."),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404_0", "존재하지 않는 계정입니다. 회원가입 후 로그인해주세요."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "SEC404_1", "토큰이 존재하지 않습니다."),

    INTERNAL_SECURITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC500_0", "인증 처리 중 서버 에러가 발생했습니다."),
    INTERNAL_TOKEN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC500_1", "토큰 처리 중 서버 에러가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
