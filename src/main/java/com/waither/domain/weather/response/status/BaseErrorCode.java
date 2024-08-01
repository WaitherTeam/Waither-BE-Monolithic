package com.waither.domain.weather.response.status;

import com.waither.weatherservice.response.ApiResponse;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

	HttpStatus getHttpStatus();

	String getCode();

	String getMessage();

	ApiResponse<Void> getErrorResponse();
}
