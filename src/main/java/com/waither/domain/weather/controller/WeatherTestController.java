package com.waither.domain.weather.controller;

import com.waither.weatherservice.dto.request.AccuweatherTestRequest;
import com.waither.weatherservice.dto.request.AdvisoryRequest;
import com.waither.weatherservice.dto.request.AirTestRequest;
import com.waither.weatherservice.dto.request.ForeCastTestRequest;
import com.waither.weatherservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/weather-test")
public class WeatherTestController {

	private final WeatherService weatherService;

	@PostMapping("/short")
	public void createExpectedWeatherTest(@RequestBody ForeCastTestRequest request) throws URISyntaxException {
		weatherService.createExpectedWeather(request.nx(), request.ny(), request.baseDate(), request.baseTime());
	}

	@PostMapping("/daily")
	public void createDailyWeatherTest(@RequestBody ForeCastTestRequest request) throws URISyntaxException {
		weatherService.createDailyWeather(request.nx(), request.ny(), request.baseDate(), request.baseTime());
	}

	@PostMapping("/advisory")
	public void createWeatherAdvisory(@RequestBody AdvisoryRequest request) throws URISyntaxException, IOException {
		weatherService.createWeatherAdvisory(request.latitude(), request.longitude());
	}

	@PostMapping("/air")
	public void airKoreaTest(@RequestBody AirTestRequest request) throws URISyntaxException {
		weatherService.createAirKorea(request.searchDate());
	}

	@PostMapping("/accuweather")
	public void accuweatherTest(@RequestBody AccuweatherTestRequest request) throws URISyntaxException, IOException {
		weatherService.convertLocation(request.latitude(), request.longitude());
	}

	@GetMapping("/converㅅ")
	public LocalDateTime convertTest() {

		LocalDateTime specificDateTime = LocalDateTime.of(2024, 7, 5, 23, 0);
		log.info("Changed : {}", specificDateTime);
		return weatherService.convertLocalDateTimeToDailyWeatherTime(specificDateTime);
	}
}
