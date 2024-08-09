package com.waither.domain.weather.repository;

import com.waither.domain.weather.entity.WeatherAdvisory;
import org.springframework.data.repository.CrudRepository;

public interface WeatherAdvisoryRepository extends CrudRepository<WeatherAdvisory, String> {
}
