package com.waither.domain.weather.response;

import com.waither.weatherservice.response.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

	// 일반적인 ERROR 응답
	BAD_REQUEST_400(HttpStatus.BAD_REQUEST,
		"COMMON400_0",
		HttpStatus.BAD_REQUEST.getReasonPhrase()),
	UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED,
		"COMMON400_1",
		HttpStatus.UNAUTHORIZED.getReasonPhrase()),
	FORBIDDEN_403(HttpStatus.FORBIDDEN,
		"COMMON400_3",
		HttpStatus.FORBIDDEN.getReasonPhrase()),
	NOT_FOUND_404(HttpStatus.NOT_FOUND,
		"COMMON400_4",
		HttpStatus.NOT_FOUND.getReasonPhrase()),
	INTERNAL_SERVER_ERROR_500(
		HttpStatus.INTERNAL_SERVER_ERROR,
		"COMMON500_0",
		HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()),

	// 유효성 검사
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID400_0", "입력값에 대한 검증에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
