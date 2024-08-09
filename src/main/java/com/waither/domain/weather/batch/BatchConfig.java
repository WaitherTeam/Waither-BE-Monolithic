package com.waither.domain.weather.batch;

import com.waither.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final WeatherService weatherService;

	@Bean
	public Job dailyWeatherJob() {
		return new JobBuilder("dailyWeatherJob", jobRepository)
			.start(dailyWeatherStep())
			.build();
	}

	@Bean
	public Step dailyWeatherStep() {
		return new StepBuilder("dailyWeatherStep", jobRepository)
			.tasklet(dailyWeatherTasklet(), transactionManager)
			.build();
	}

	@Bean
	public Tasklet dailyWeatherTasklet() {
		return new DailyWeatherTasklet(weatherService);
	}

	@Bean
	public Job expectedWeatherJob() {
		return new JobBuilder("expectedWeatherJob", jobRepository)
			.start(expectedWeatherStep())
			.build();
	}

	@Bean
	public Step expectedWeatherStep() {
		return new StepBuilder("expectedWeatherStep", jobRepository)
			.tasklet(expectedWeatherTasklet(), transactionManager)
			.build();
	}

	@Bean
	public Tasklet expectedWeatherTasklet() {
		return new ExpectedWeatherTasklet(weatherService);
	}

	@Bean
	public Job weatherAdvisoryJob() {
		return new JobBuilder("weatherAdvisoryJob", jobRepository)
			.start(weatherAdvisoryStep())
			.build();
	}

	@Bean
	public Step weatherAdvisoryStep() {
		return new StepBuilder("weatherAdvisoryStep", jobRepository)
			.tasklet(weatherAdvisoryTasklet(), transactionManager)
			.build();
	}

	@Bean
	public Tasklet weatherAdvisoryTasklet() {
		return new WeatherAdvisoryTasklet(weatherService);
	}
}
