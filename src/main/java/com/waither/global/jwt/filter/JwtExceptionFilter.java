package com.waither.global.jwt.filter;


import com.waither.userservice.global.jwt.execption.SecurityCustomException;
import com.waither.userservice.global.jwt.execption.SecurityErrorCode;
import com.waither.userservice.global.jwt.util.HttpResponseUtil;
import com.waither.userservice.global.response.ApiResponse;
import com.waither.userservice.global.response.status.BaseErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityCustomException e) {
            log.warn(">>>>> SecurityCustomException : ", e);
            BaseErrorCode errorCode = e.getErrorCode();
            HttpResponseUtil.setErrorResponse(
                    response,
                    errorCode.getHttpStatus(),
                    ApiResponse.onFailure(
                            errorCode.getCode(),
                            errorCode.getMessage(),
                            e.getMessage()
                    )
            );
        } catch (Exception e) {
            log.error(">>>>> Exception : ", e);
            HttpResponseUtil.setErrorResponse(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ApiResponse.onFailure(
                            SecurityErrorCode.INTERNAL_SECURITY_ERROR.getCode(),
                            SecurityErrorCode.INTERNAL_SECURITY_ERROR.getMessage(),
                            e.getMessage()
                    )
            );

        }
    }
}
