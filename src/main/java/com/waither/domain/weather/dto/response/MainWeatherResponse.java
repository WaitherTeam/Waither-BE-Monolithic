package com.waither.domain.weather.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MainWeatherResponse(

	String pop,
	String temp,
	String tempMin,
	String tempMax,
	String humidity,
	String windVector,
	String windDegree,
	// 예상 기온
	List<String> expectedTemp,

	// 예상 강수량
	List<String> expectedRain,

	// 예상 강수 형태
	List<String> expectedPty,

	// 예상 하늘 상태
	List<String> expectedSky
) {

	public static MainWeatherResponse from(
		String pop,
		String temp,
		String tempMin,
		String tempMax,
		String humidity,
		String windVector,
		String windDegree,
		// 예상 기온
		List<String> expectedTemp,

		// 예상 강수량
		List<String> expectedRain,

		// 예상 강수 형태
		List<String> expectedPty,

		// 예상 하늘 상태
		List<String> expectedSky
	) {
		return MainWeatherResponse.builder()
			.pop(pop)
			.temp(temp)
			.tempMin(tempMin)
			.tempMax(tempMax)
			.humidity(humidity)
			.windVector(windVector)
			.windDegree(windDegree)
			.expectedTemp(expectedTemp)
			.expectedRain(expectedRain)
			.expectedPty(expectedPty)
			.expectedSky(expectedSky)
			.build();
	}
}
