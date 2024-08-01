package com.waither.domain.user.dto.request;

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
