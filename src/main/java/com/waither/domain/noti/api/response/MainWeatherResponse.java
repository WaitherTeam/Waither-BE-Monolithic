package com.waither.domain.noti.api.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MainWeatherResponse(

	//현재 강수 확률
	String pop,
	//현재 온도
	String temp,
	//최저 온도
	String tempMin,
	//최고 온도
	String tempMax,
	//현재 습도
	String humidity,
	//풍향
	String windVector,
	//풍
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
}
