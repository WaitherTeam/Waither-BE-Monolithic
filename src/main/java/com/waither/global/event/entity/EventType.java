package com.waither.global.event.entity;

import com.waither.global.event.WeatherEvent;
import com.waither.global.event.WeatherEventAbstract;

public enum EventType {
    EXPECT_RAIN,
    WEATHER_WARNING,
    WIND_STRENGTH;

    public Class<? extends WeatherEventAbstract> getEventClass() {
        return switch (this) {
            case EXPECT_RAIN -> WeatherEvent.ExpectRain.class;
            case WIND_STRENGTH -> WeatherEvent.WindStrength.class;
            case WEATHER_WARNING -> WeatherEvent.WeatherWarning.class;
        };
    }
}
