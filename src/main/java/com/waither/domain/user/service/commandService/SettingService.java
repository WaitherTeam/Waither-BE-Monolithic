package com.waither.domain.user.service.commandService;

import com.waither.domain.user.dto.request.SettingReqDto;
import com.waither.domain.user.entity.UserRegion;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.UserRegionRepository;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.exception.CustomException;
import com.waither.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SettingService {

    private final UserRepository userRepository;
    private final SettingRepository settingRepository;
    private final UserRegionRepository userRegionRepository;


    /* --------- Update  --------- */

    // 사용자 맞춤 서비스 제공 설정 변경
    public void updateCustom(User currentUser, SettingReqDto.CustomDto customDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.setCustom(customDto.custom());
        userRepository.save(user);
    }

    // 메인 화면 날씨 상세 정보 변경
    public void updateDisplay(User currentUser, SettingReqDto.DisplayDto displayDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        if (displayDto.precipitation() != null) {
            setting.setPrecipitation(displayDto.precipitation());
        }
        if (displayDto.wind() != null) {
            setting.setWind(displayDto.wind());
        }
        if (displayDto.dust() != null) {
            setting.setDust(displayDto.dust());
        }
        settingRepository.save(setting);
    }

    // 알림 설정 변경 (요일 & 시간)
    public void updateOutAlertSet(User currentUser, SettingReqDto.OutAlertSetDto outAlertSetDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        if (!setting.isOutAlert()) {
            throw new CustomException(UserErrorCode.INACTIVE_OUT_ALERT_SETTING);
        }
        updateDays(setting, outAlertSetDto.days());

        if (outAlertSetDto.outTime() != null) {
            setting.setOutTime(outAlertSetDto.outTime());
        }
        setting.setOutTime(outAlertSetDto.outTime());
        settingRepository.save(setting);
    }

    // 요일 설정 업데이트
    private void updateDays(Setting setting, List<String> daysToUpdate) {
        EnumSet<DayOfWeek> selectedDays = daysToUpdate.stream()
                .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));
        setting.setDays(selectedDays);
    }

    // 외출 알림 설정 변경
    public void updateOutAlert(User currentUser, SettingReqDto.OutAlertDto outAlertDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setOutAlert(outAlertDto.outAlert());
        settingRepository.save(setting);
    }

    // 기상 특보 알림 설정 변경
    public void updateClimateAlert(User currentUser, SettingReqDto.ClimateAlertDto climateAlertDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setClimateAlert(climateAlertDto.climateAlert());
        settingRepository.save(setting);
    }

    // 사용자 맞춤 예보 설정 변경
    public void updateUserAlert(User currentUser, SettingReqDto.UserAlertDto userAlertDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!user.isCustom()) {
            throw new CustomException(UserErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        setting.setUserAlert(userAlertDto.userAlert());
        settingRepository.save(setting);
    }

    // 강설 정보 알림 설정 변경
    public void updateSnowAlert(User currentUser, SettingReqDto.SnowAlertDto snowAlertDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!user.isCustom()) {
            throw new CustomException(UserErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        setting.setSnowAlert(snowAlertDto.snowAlert());
        settingRepository.save(setting);
    }

    // 바람 세기 알림 설정 변경
    public void updateWind(User currentUser, SettingReqDto.WindDto windDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!user.isCustom()) {
            throw new CustomException(UserErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        if (windDto.windAlert() != null) {
            setting.setWindAlert(windDto.windAlert());
        }
        if (windDto.windDegree() != null) {
            setting.setWindDegree(windDto.windDegree());
        }
        settingRepository.save(setting);
    }

    // 직장 지역 레포트 알림 받기
    public void updateRegionReport(User currentUser, SettingReqDto.RegionReportDto regionReportDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setRegionReport(regionReportDto.regionReport());
        settingRepository.save(setting);
    }

    // 직장 지역 설정
    public void updateRegion(User currentUser, SettingReqDto.RegionDto regionDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        UserRegion userRegion = user.getSetting().getUserRegion();
        userRegion.update(regionDto.regionName(), regionDto.longitude(), regionDto.latitude());
        userRegionRepository.save(userRegion);
    }

    // 사용자 가중치 설정
    public void updateWeight(User currentUser, SettingReqDto.WeightDto weightDto) {
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setWeight(weightDto.weight());
        settingRepository.save(setting);
    }
}
