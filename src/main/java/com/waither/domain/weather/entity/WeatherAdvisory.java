package com.waither.domain.weather.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = "WeatherAdvisory", timeToLive = 86400L) // 유효시간: 24시간
public class WeatherAdvisory {

	@Id
	private String id;
	private String message;

	public String toString() {
		return "WeatherAdvisory{" +
			"message='" + message + '\'' +
			'}';
	}
}
