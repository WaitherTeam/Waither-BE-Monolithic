package com.waither.domain.weather.repository;

import com.waither.domain.weather.entity.DailyWeather;
import org.springframework.data.repository.CrudRepository;

public interface DailyWeatherRepository extends CrudRepository<DailyWeather, String> {
}
