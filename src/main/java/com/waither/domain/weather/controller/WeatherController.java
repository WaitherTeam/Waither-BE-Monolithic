package com.waither.domain.weather.controller;

import com.waither.domain.user.entity.User;
import com.waither.domain.weather.dto.request.GetReportRequest;
import com.waither.domain.weather.dto.request.GetWeatherRequest;
import com.waither.domain.weather.dto.response.MainWeatherResponse;
import com.waither.domain.weather.dto.response.ReportResponse;
import com.waither.domain.weather.service.WeatherService;
import com.waither.global.jwt.annotation.CurrentUser;
import com.waither.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {

	private final WeatherService weatherService;

	@Operation(summary = "모든 날씨 정보 가져오기 - 프론트, noti-service 사용",
		description = "{"
			+ "\"latitude\": 37.41,"
			+ "\"longitude\": 126.73"
			+ "}")
	@GetMapping("/main")
	public ApiResponse<MainWeatherResponse> getMainWeather(@ModelAttribute @Valid GetWeatherRequest getWeatherRequest) {
		return ApiResponse.onSuccess(
			weatherService.getMainWeather(getWeatherRequest.latitude(), getWeatherRequest.longitude()));
	}

	@Operation(summary = "위도, 경도 -> 지역 변환",
		description = "{"
			+ "\"latitude\": 37.41,"
			+ "\"longitude\": 126.73"
			+ "}")
	@GetMapping("/region")
	public ApiResponse<String> convertGpsToRegionName(@ModelAttribute @Valid GetWeatherRequest getWeatherRequest) {
		return ApiResponse.onSuccess(
			weatherService.convertGpsToRegionName(getWeatherRequest.latitude(), getWeatherRequest.longitude()));
	}

	@Operation(summary = "날씨 레포트 정보 가져오기")
	@GetMapping("/report")
	public ApiResponse<ReportResponse> getReport(@CurrentUser User currentUser, @ModelAttribute @Valid GetReportRequest getReportRequest) {
		return ApiResponse.onSuccess(
				weatherService.getReport(currentUser, getReportRequest.latitude(), getReportRequest.longitude()));
	}
}
