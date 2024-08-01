package com.waither.global.jwt.execption;

import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.status.BaseErrorCode;
import lombok.Getter;

@Getter
// 💡 주의: Filter 단에서 돌아가는 코드에 사용하지 말 것
public class SecurityCustomException extends CustomException {

    private final Throwable cause;

    public SecurityCustomException(BaseErrorCode errorCode) {
        super(errorCode);
        this.cause = null;
    }

    public SecurityCustomException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode);
        this.cause = cause;
    }
}