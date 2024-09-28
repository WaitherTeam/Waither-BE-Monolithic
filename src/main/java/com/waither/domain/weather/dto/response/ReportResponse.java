package com.waither.domain.weather.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ReportResponse(
        // 날짜
        LocalDate date,

        // 조언
        List<String> advices,

        // 날씨 변화
        WeatherChange weatherChange,

        // 유저들의 답변
        TemperaturePerception userPerception,

        // 현재 날씨 정보
        String pop,
        String temp,
        String tempMin,
        String tempMax,
        String humidity,
        String windVector,
        String windDegree,

        // 사용자 설정
        UserSetting userSetting
) {
    public record WeatherChange(
            double tempDifference, // 기온 차이
            int windChangeStatus  // 바람 변화 여부 ; -1: 감소, 0: 비슷, 1: 증가
    ) {}

    public record TemperaturePerception(
            Integer ans, // 가장 많은 답변
            int percentage // 해당 인식의 비율
    ) {}

    public record UserSetting(
            boolean precipitation, // 강수량 보기
            boolean wind, // 풍량/풍속 보기
            boolean dust  // 미세먼지 보기
    ) {}
}
