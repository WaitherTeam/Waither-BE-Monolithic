package com.waither.domain.weather.dto.request;

public record ForeCastTestRequest(
	int nx,
	int ny,
	String baseDate,
	String baseTime
) {
}
