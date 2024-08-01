package com.waither.domain.weather.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job dailyWeatherJob;
	private final Job expectedWeatherJob;
	private final Job weatherAdvisoryJob;

	@Scheduled(cron = "0 0 2,5,8,11,14,17,20,23 * * *") // 3시간마다
	public void runDailyWeatherJob() {
		log.info("[ Scheduler ] DailyWeather Api");
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("executedTime", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(dailyWeatherJob, jobParameters);
		} catch (JobExecutionException e) {
			log.error("Error executing dailyWeatherJob: ", e);
		}
	}

	@Scheduled(cron = "0 0 * * * *") // 1시간마다
	public void runExpectedWeatherJob() {
		log.info("[ Scheduler ] ExpectedWeather Api");
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("executedTime", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(expectedWeatherJob, jobParameters);
		} catch (JobExecutionException e) {
			log.error("Error executing expectedWeatherJob: ", e);
		}
	}

	@Scheduled(cron = "0 0 * * * *") // 1시간마다
	public void runWeatherAdvisoryJob() {
		log.info("[ Scheduler ] WeatherAdvisory Api");
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("executedTime", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(weatherAdvisoryJob, jobParameters);
		} catch (JobExecutionException e) {
			log.error("Error executing expectedWeatherJob: ", e);
		}
	}
}
