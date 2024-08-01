package com.waither.domain.user.converter;

import com.waither.domain.user.dto.response.SettingResDto;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingConverter {

    // Setting을 기본값으로 설정
    public static Setting createSetting() {
        // Setting을 기본값으로 설정
        return Setting.builder()
                .climateAlert(true)
                .userAlert(true)
                .snowAlert(true)
                .windAlert(true)
                .windDegree(10)
                .regionReport(true)
                .precipitation(true)
                .wind(true)
                .dust(true)
                .weight(0.0)
                .build();
    }

    public static SettingResDto.CustomDto toCustomDto(User user) {
        return SettingResDto.CustomDto.builder()
                .custom(user.isCustom())
                .build();
    }

    public static SettingResDto.RegionNameDto toRegionNameDto(Setting setting) {
        return SettingResDto.RegionNameDto.builder()
                .regionName(setting.getRegion().getRegionName())
                .build();
    }

    public static SettingResDto.NotificationDto toNotificationDto(Setting setting) {
        List<String> days = setting.getDays().stream()
                .map(Enum::toString)
                .toList();

        return SettingResDto.NotificationDto.builder()
                .outAlert(setting.isOutAlert())
                .days(days)
                .outTime(setting.getOutTime())
                .climateAlert(setting.isClimateAlert())
                .userAlert(setting.isUserAlert())
                .snowAlert(setting.isSnowAlert())
                .build();
    }

    public static SettingResDto.WindDto toWindDto(Setting setting) {
        return SettingResDto.WindDto.builder()
                .windAlert(setting.isWindAlert())
                .windDegree(setting.getWindDegree())
                .build();
    }

    public static SettingResDto.DisplayDto toDisplayDto(Setting setting) {
        return SettingResDto.DisplayDto.builder()
                .precipitation(setting.isPrecipitation())
                .wind(setting.isWind())
                .dust(setting.isDust())
                .build();
    }

    public static SettingResDto.WeightDto toWeightDto(Setting setting) {
        return SettingResDto.WeightDto.builder()
                .weight(setting.getWeight())
                .build();
    }

    public static SettingResDto.UserInfoDto toUserInfoDto(User user) {
        return SettingResDto.UserInfoDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}