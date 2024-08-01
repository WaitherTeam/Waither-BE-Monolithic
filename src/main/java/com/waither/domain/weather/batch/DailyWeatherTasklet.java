package com.waither.domain.weather.batch;

import com.waither.weatherservice.entity.Region;
import com.waither.weatherservice.exception.WeatherExceptionHandler;
import com.waither.weatherservice.response.WeatherErrorCode;
import com.waither.weatherservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class DailyWeatherTasklet implements Tasklet {

	private final WeatherService weatherService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		LocalDateTime baseTime = LocalDateTime.now();
		String[] dateTime = weatherService.convertLocalDateTimeToString(baseTime).split("_");
		List<Region> regionList = weatherService.getRegionList();
		regionList.stream()
			.forEach(region -> {
				try {
					weatherService.createDailyWeather(region.getStartX(), region.getStartY(), dateTime[0],
						dateTime[1]);
				} catch (URISyntaxException e) {
					throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_URI_ERROR);
				}
			});
		return RepeatStatus.FINISHED;
	}
}
