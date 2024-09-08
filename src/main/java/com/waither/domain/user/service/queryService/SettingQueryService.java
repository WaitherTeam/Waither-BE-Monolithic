package com.waither.domain.user.service.queryService;

import com.waither.domain.user.converter.SettingConverter;
import com.waither.domain.user.dto.response.SettingResDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.exception.CustomException;
import com.waither.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettingQueryService {

    private final UserRepository userRepository;

    // 사용자 맞춤 서비스 제공 조회
    public SettingResDto.CustomDto getUserCustom(User currentUser) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toCustomDto(user);
    }

    // 알림 설정 조회
    public SettingResDto.NotificationDto getNotification(User currentUser) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toNotificationDto(user.getSetting());
    }

    // 메인 화면 날씨 상세 정보 조회
    public SettingResDto.DisplayDto getDisplay(User currentUser) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toDisplayDto(user.getSetting());
    }

    // 바람 세기 설정 조회
    public SettingResDto.WindDto getWind(User currentUser) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toWindDto(user.getSetting());
    }

    // 사용자 가중치 설정 조회
    public SettingResDto.WeightDto getWeight(User currentUser) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toWeightDto(user.getSetting());
    }

    // 직장 지역 설정 조회
    public SettingResDto.RegionDto getRegion(User currentUser){
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toRegionNameDto(user.getSetting());
    }

    // 개인 정보 설정 조회
    public SettingResDto.UserInfoDto getUserInfo(User currentUser) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return SettingConverter.toUserInfoDto(user);
    }

}
