package com.waither.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.List;

public class SettingReqDto {

    public record CustomDto(
            boolean custom
    ) { }

    public record OutAlertDto(
            boolean outAlert
    ) { }

    public record OutAlertSetDto(
            List<String> days,

            @Schema(description = "외출 시간", example = "09:00:00", type = "string", pattern = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")
            LocalTime outTime
    ) { }

    public record ClimateAlertDto(
            boolean climateAlert
    ) { }

    public record UserAlertDto(
            boolean userAlert
    ) { }

    public record SnowAlertDto(
            boolean snowAlert
    ) { }

    public record WindDto(
            Boolean windAlert,
            Integer windDegree
    ) { }

    public record DisplayDto(
            Boolean precipitation,
            Boolean wind,
            Boolean dust
    ) {}

    public record RegionDto(
            String regionName,
            double longitude,
            double latitude

    ) { }

    public record RegionReportDto(
            boolean regionReport
    ) { }

    public record WeightDto(
            Double weight
    ) { }

}
