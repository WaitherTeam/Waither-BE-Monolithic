package com.waither.domain.user.dto.response;

import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

public class SettingResDto {

    @Builder
    public record CustomDto(
            boolean custom
    ) { }

    @Builder
    public record RegionNameDto(
            String regionName
    ) { }

    @Builder
    public record NotificationDto(
            boolean outAlert,
            List<String> days,
            LocalTime outTime,
            boolean climateAlert,
            boolean userAlert,
            boolean snowAlert
    ) { }

    @Builder
    public record WindDto(
            boolean windAlert,
            Integer windDegree
    ) { }

    @Builder
    public record DisplayDto(
            boolean precipitation,
            boolean wind,
            boolean dust
    ) { }

    @Builder
    public record WeightDto(
            Double weight
    ) { }

    @Builder
    public record UserInfoDto(
            String email,
            String nickname
    ) { }

}
