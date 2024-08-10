package com.waither.domain.weather.repository;

import com.waither.domain.weather.entity.ExpectedWeather;
import org.springframework.data.repository.CrudRepository;

public interface ExpectedWeatherRepository extends CrudRepository<ExpectedWeather, String> {
}
