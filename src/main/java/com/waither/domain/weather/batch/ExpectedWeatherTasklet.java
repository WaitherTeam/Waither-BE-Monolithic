package com.waither.domain.weather.batch;

import com.waither.domain.weather.entity.Region;
import com.waither.domain.weather.service.WeatherService;
import com.waither.global.exception.CustomException;
import com.waither.global.response.WeatherErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ExpectedWeatherTasklet implements Tasklet {

	private final WeatherService weatherService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		LocalDateTime baseTime = LocalDateTime.now().minusHours(1);
		String[] dateTime = weatherService.convertLocalDateTimeToString(baseTime).split("_");
		List<Region> regionList = weatherService.getRegionList();
		regionList.stream()
			.forEach(region -> {
				try {
					weatherService.createExpectedWeather(region.getStartX(), region.getStartY(), dateTime[0],
						dateTime[1]);
				} catch (URISyntaxException e) {
					throw new CustomException(WeatherErrorCode.WEATHER_URI_ERROR);
				}
			});
		return RepeatStatus.FINISHED;
	}
}
