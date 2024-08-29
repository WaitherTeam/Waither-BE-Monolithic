package com.waither.global.response;

import com.waither.global.response.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotiErrorCode implements BaseErrorCode {

	// 데이터 관련 에러
	USER_MEDIAN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_0", "사용자 설정값이 존재하지 않습니다."),
	USER_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "사용자 데이터 값이 존재하지 않습니다."),
	USER_SETTINGS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_2", "사용자 설정 값이 존재하지 않습니다."),

	//통신 과정 에러
	COMMUNICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500_1", "통신 과정에서 문제가 발생했습니다."),

	//FirebaseError
	FIREBASE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FB404", "푸시알림 토큰이 존재하지 않습니다."),
	FIREBASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FB500", "Firebase 메세지 전송 오류가 발생했습니다."),

	//SQS Error
	SQS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SQS500", "SQS 메세지 전송 과정에서 에러가 발생했습니다.")

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
