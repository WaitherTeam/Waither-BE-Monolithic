package com.waither.domain.user.exception;

import com.waither.global.exception.CustomException;
import lombok.Getter;


@Getter
public class UserException extends CustomException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}