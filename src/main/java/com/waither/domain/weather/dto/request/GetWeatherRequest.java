package com.waither.domain.weather.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record GetWeatherRequest(
	@NotNull(message = "[ERROR] 위도 입력은 필수 입니다.")
	@DecimalMin(value = "33.0", message = "[ERROR] 위도는 33.0도에서 43.0도 사이여야 합니다.")
	@DecimalMax(value = "43.0", message = "[ERROR] 위도는 33.0도에서 43.0도 사이여야 합니다.")
	Double latitude,
	@NotNull(message = "[ERROR] 경도 입력은 필수 입니다.")
	@DecimalMin(value = "124.0", message = "[ERROR] 경도는 124.0도에서 132.0도 사이여야 합니다.")
	@DecimalMax(value = "132.0", message = "[ERROR] 경도는 124.0도에서 132.0도 사이여야 합니다.")
	Double longitude
) {
}
