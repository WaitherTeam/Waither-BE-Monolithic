package com.waither.domain.weather.scheduler;// package com.waither.weatherservice.scheduler;
//
// import java.net.URISyntaxException;
// import java.time.LocalDateTime;
// import java.util.List;
//
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.scheduling.annotation.Scheduled;
//
// import com.waither.weatherservice.entity.Region;
// import com.waither.weatherservice.exception.WeatherExceptionHandler;
// import com.waither.weatherservice.response.WeatherErrorCode;
// import com.waither.weatherservice.service.WeatherService;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @RequiredArgsConstructor
// @ConfigurationProperties
// public class SchedulerConfig {
//
// 	private final WeatherService weatherService;
//
// 	@Scheduled(cron = "0 0 2,5,8,11,14,17,20,23 * * *") // 3시간 마다
// 	public void createDailyWeather() {
//
// 		LocalDateTime now = LocalDateTime.now();
// 		String[] dateTime = weatherService.convertLocalDateTimeToString(now).split("_");
// 		List<Region> regionList = weatherService.getRegionList();
// 		regionList.stream()
// 			.forEach(region -> {
// 				try {
// 					weatherService.createDailyWeather(region.getStartX(), region.getStartY(), dateTime[0],
// 						dateTime[1]);
// 				} catch (URISyntaxException e) {
// 					throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_URI_ERROR);
// 				}
// 			});
// 	}
//
// 	@Scheduled(cron = "0 0 * * * *") // 1시간 마다
// 	public void createExpectedWeather() {
//
// 		LocalDateTime now = LocalDateTime.now();
// 		String[] dateTime = weatherService.convertLocalDateTimeToString(now).split("_");
// 		List<Region> regionList = weatherService.getRegionList();
// 		regionList.stream()
// 			.forEach(region -> {
// 				try {
// 					weatherService.createExpectedWeather(region.getStartX(), region.getStartY(), dateTime[0],
// 						dateTime[1]);
// 				} catch (URISyntaxException e) {
// 					throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_URI_ERROR);
// 				}
// 			});
// 	}
// }
