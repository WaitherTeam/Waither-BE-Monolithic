package com.waither.global.response;

import com.waither.global.response.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

	// 이메일 관련 에러
	INVALID_CODE(HttpStatus.BAD_REQUEST, "EMAIL400_0", "인증번호가 일치하지 않아요. 다시 한 번 확인해주세요."),
	INVALID_ACCOUNT(HttpStatus.BAD_REQUEST, "EMAIL400_1", "인증되지 않은 이메일입니다."),
	AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL400_2", "다시 인증 번호를 요청해주세요."),
	VERIFIED_CHECK_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL400_3", "인증 완료 후 유효기간이 경과하였습니다. 다시 인증 번호를 요청해주세요."),

	UNABLE_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL500_0", "이메일을 전송하는 도중, 에러가 발생했습니다."),
	NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL500_1", "이메일 인증 코드를 생성할 수 없습니다."),

	DATA_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404_0", "해당 데이터를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404_1", "사용자가 없습니다."),
	EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER400_2", "이메일은 필수 입니다."),
	PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER400_3", "비밀번호가 일치하지 않습니다."),
	CURRENT_PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "USER400_4", "현재 비밀번호가 일치하지 않습니다."),
	CURRENT_PASSWORD_EQUAL(HttpStatus.BAD_REQUEST, "USER400_5", "변경하려는 비밀번호가 현재 비밀번호와 일치합니다."),
	USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER400_6", "사용자가 이미 존재합니다."),

	// 세팅 관련 에러
	INACTIVE_CUSTOM_SETTING(HttpStatus.BAD_REQUEST, "SETTING400_1"
			, "사용자 맞춤 서비스 제공을 켜지 않으면 사용할 수 없는 기능입니다."),
	INACTIVE_OUT_ALERT_SETTING(HttpStatus.BAD_REQUEST, "SETTING400_2"
			, "외출 시간 알림 받기을 켜지 않으면 사용할 수 없는 기능입니다."),
	OUT_TIME_NULL(HttpStatus.BAD_REQUEST, "SETTING400_3", "외출 시간이 지정되지 않았습니다."),

	// 설문 관련 에러
	INVALID_SEASON(HttpStatus.BAD_REQUEST, "SURVEY400_1", "정의되지 않은 계절입니다."),
	NO_USER_DATA_FOUND(HttpStatus.BAD_REQUEST, "SURVEY400_2", "해당 사용자와 계절에 대한 UserData를 찾을 수 없습니다."),
	NO_USER_MEDIAN_FOUND(HttpStatus.BAD_REQUEST, "SURVEY400_3", "해당 사용자와 계절에 대한 UserMedian을 찾을 수 없습니다."),
	INVALID_LEVEL_VALUE(HttpStatus.BAD_REQUEST, "SURVEY400_4", "정의되지 않은 Level 입니다."),
	IGNORE_SURVEY_ANSWER(HttpStatus.BAD_REQUEST, "SURVEY400_5", "상위 레벨보다 높거나 하위 레벨보다 낮은 값이 계산되었습니다. 답변을 무시합니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
