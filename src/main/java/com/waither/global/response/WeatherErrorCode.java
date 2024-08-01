package com.waither.global.response;

import com.waither.global.response.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WeatherErrorCode implements BaseErrorCode {

	WEATHER_EXAMPLE_ERROR(HttpStatus.BAD_REQUEST, "WEAT400_0", "날씨 에러입니다."),
	WEATHER_OPENAPI_ERROR(HttpStatus.BAD_REQUEST, "WEAT400_3", "OpenApi 관련 오류입니다."),
	WEATHER_MAIN_ERROR(HttpStatus.BAD_REQUEST, "WEAT400_2", "잘못된 위도, 경도입니다."), // 레디스에 캐싱 데이터가 없는 경우
	WEATHER_URI_ERROR(HttpStatus.BAD_REQUEST, "WEAT400_3", "URI 변환에 실패하였습니다."),
	REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "WEAT400_4", "지역명을 찾을 수 없습니다."),
	DAILY_NOT_FOUND(HttpStatus.NOT_FOUND, "WEAT400_5", "하루 날씨 정보를 찾을 수 없습니다."),
	EXPECTED_NOT_FOUND(HttpStatus.NOT_FOUND, "WEAT400_6", "예상 날씨 정보를 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
