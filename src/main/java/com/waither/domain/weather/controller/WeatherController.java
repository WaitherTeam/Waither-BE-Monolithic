package com.waither.domain.weather.controller;

import com.waither.weatherservice.dto.request.GetWeatherRequest;
import com.waither.weatherservice.dto.response.MainWeatherResponse;
import com.waither.weatherservice.response.ApiResponse;
import com.waither.weatherservice.service.WeatherService;
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
}
