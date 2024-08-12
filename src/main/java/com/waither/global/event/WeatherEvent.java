package com.waither.global.event;

import com.waither.domain.user.entity.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class WeatherEvent {

    //예상 강수량 이벤트
    @RequiredArgsConstructor
    @Getter
    public static class ExpectRain {

        //지역
        private final String region;

        //예상 강수량
        private final List<String> expectRain;
    }

    //기상 특보 이벤트
    @RequiredArgsConstructor
    @Getter
    public static class WeatherWarning {

        //지역
        private final String region;

        //지역 이름
        private final String content;
    }

    //바람 세기 이벤트
    @RequiredArgsConstructor
    @Getter
    public static class WindStrength {

        //지역
        private final String region;

        //바람 세기
        private final Double windStrength;
    }
}
