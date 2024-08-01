package com.waither.domain.weather.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = "DailyWeather", timeToLive = 28800L) // 유효시간: 8시간
public class DailyWeather {

	@Id
	private String id;

	// 강수확률 (%)
	private String pop;
	private String tmp;
	private String tempMin;
	private String tempMax;
	private String humidity;
	private String windVector;
	private String windDegree;

	public String toString() {
		return "DailyWeather{" +
			"pop='" + pop + '\'' +
			", tempMin='" + tempMin + '\'' +
			", tempMax='" + tempMax + '\'' +
			", humidity='" + humidity + '\'' +
			", windVector='" + windVector + '\'' +
			", windDegree='" + windDegree + '\'' +
			'}';
	}
}
