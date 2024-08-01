package com.waither.domain.user.service.queryService;

import com.waither.userservice.converter.SettingConverter;
import com.waither.userservice.dto.response.SettingResDto;
import com.waither.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettingQueryService {

    // 사용자 맞춤 서비스 제공 조회
    public SettingResDto.CustomDto getUserCustom(User user) {
        return SettingConverter.toCustomDto(user);
    }

    // 알림 설정 조회
    public SettingResDto.NotificationDto getNotification(User user) {
        return SettingConverter.toNotificationDto(user.getSetting());
    }

    // 메인 화면 날씨 상세 정보 조회
    public SettingResDto.DisplayDto getDisplay(User user) {
        return SettingConverter.toDisplayDto(user.getSetting());
    }

    // 바람 세기 설정 조회
    public SettingResDto.WindDto getWind(User user) {
        return SettingConverter.toWindDto(user.getSetting());
    }

    // 사용자 가중치 설정 조회
    public SettingResDto.WeightDto getWeight(User user) {
        return SettingConverter.toWeightDto(user.getSetting());
    }

    // 직장 지역 설정 조회
    public SettingResDto.RegionNameDto getRegion(User user){
        return SettingConverter.toRegionNameDto(user.getSetting());
    }

    // 개인 정보 설정 조회
    public SettingResDto.UserInfoDto getUserInfo(User user) {
        return SettingConverter.toUserInfoDto(user);
    }

}
