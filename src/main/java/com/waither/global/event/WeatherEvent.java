package com.waither.global.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.waither.global.event.entity.EventType;
import lombok.Getter;

import java.util.List;

public class WeatherEvent {

    //예상 강수량 이벤트
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpectRain extends WeatherEventAbstract {

        //지역
        private final String region;

        //예상 강수량
        private final List<String> expectRain;

        @JsonCreator //역직렬화
        public ExpectRain(
                @JsonProperty("region") String region,
                @JsonProperty("expectRain") List<String> expectRain) {
            super(EventType.EXPECT_RAIN);
            this.region = region;
            this.expectRain = expectRain;
        }

    }

    //기상 특보 이벤트
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherWarning extends WeatherEventAbstract  {

        //지역
        private final String region;

        //지역 이름
        private final String content;

        @JsonCreator
        public WeatherWarning(
                @JsonProperty("region") String region,
                @JsonProperty("content") String content) {
            super(EventType.WEATHER_WARNING);
            this.region = region;
            this.content = content;
        }
    }

    //바람 세기 이벤트
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindStrength extends WeatherEventAbstract  {

        //지역
        private final String region;

        //바람 세기
        private final Double windStrength;

        @JsonCreator
        public WindStrength(
                @JsonProperty("region") String region,
                @JsonProperty("windStrength") Double windStrength) {
            super(EventType.WIND_STRENGTH);
            this.region = region;
            this.windStrength = windStrength;
        }
    }
}
