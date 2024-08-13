package com.waither.domain.user.service.commandService;

import com.waither.domain.user.dto.request.SettingReqDto;
import com.waither.domain.user.entity.UserRegion;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.UserRegionRepository;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.exception.CustomException;
import com.waither.global.response.UserErrorCode;
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
    public void updateCustom(String email, SettingReqDto.CustomDto customDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.setCustom(customDto.custom());
        userRepository.save(user);
    }

    // 메인 화면 날씨 상세 정보 변경
    public void updateDisplay(String email, SettingReqDto.DisplayDto displayDto) {
        User user = userRepository.findByEmail(email)
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
    public void updateOutAlertSet(String email, SettingReqDto.OutAlertSetDto outAlertSetDto) {
        User user = userRepository.findByEmail(email)
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
    public void updateOutAlert(String email, SettingReqDto.OutAlertDto outAlertDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setOutAlert(outAlertDto.outAlert());
        settingRepository.save(setting);
    }

    // 기상 특보 알림 설정 변경
    public void updateClimateAlert(String email, SettingReqDto.ClimateAlertDto climateAlertDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setClimateAlert(climateAlertDto.climateAlert());

        // TODO: Kafka 전송
//        KafkaDto.UserSettingsDto settingDto =
//                KafkaConverter.toSettingDto(user, "climateAlert", String.valueOf(climateAlertDto.climateAlert()));
//        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 사용자 맞춤 예보 설정 변경
    public void updateUserAlert(String email, SettingReqDto.UserAlertDto userAlertDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!user.isCustom()) {
            throw new CustomException(UserErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        setting.setUserAlert(userAlertDto.userAlert());

        // TODO: Kafka 전송
//        KafkaDto.UserSettingsDto settingDto =
//                KafkaConverter.toSettingDto(user, "userAlert", String.valueOf(userAlertDto.userAlert()));
//        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 강설 정보 알림 설정 변경
    public void updateSnowAlert(String email, SettingReqDto.SnowAlertDto snowAlertDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!user.isCustom()) {
            throw new CustomException(UserErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        setting.setSnowAlert(snowAlertDto.snowAlert());

        //TODO:  Kafka 전송
//        KafkaDto.UserSettingsDto settingDto =
//                KafkaConverter.toSettingDto(user, "snowAlert", String.valueOf(snowAlertDto.snowAlert()));
//        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 바람 세기 알림 설정 변경
    public void updateWind(String email, SettingReqDto.WindDto windDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!user.isCustom()) {
            throw new CustomException(UserErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        if (windDto.windAlert() != null) {
            // TODO: Kafka 전송
//            KafkaDto.UserSettingsDto settingDto =
//                    KafkaConverter.toSettingDto(user, "windAlert", String.valueOf(windDto.windAlert()));
//            kafkaService.sendUserSettings(settingDto);
            setting.setWindAlert(windDto.windAlert());
        }
        if (windDto.windDegree() != null) {
            // TODO: Kafka 전송
//            KafkaDto.UserSettingsDto settingDto =
//                    KafkaConverter.toSettingDto(user, "windDegree", String.valueOf(windDto.windDegree()));
//            kafkaService.sendUserSettings(settingDto);
            setting.setWindDegree(windDto.windDegree());
        }

        settingRepository.save(setting);
    }

    // 직장 지역 레포트 알림 받기
    public void updateRegionReport(String email, SettingReqDto.RegionReportDto regionReportDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setRegionReport(regionReportDto.regionReport());

        // TOD: Kafka 전송
//        KafkaDto.UserSettingsDto settingDto =
//                KafkaConverter.toSettingDto(user, "regionReport", String.valueOf(regionReportDto.regionReport()));
//        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 직장 지역 설정
    public void updateRegion(String email, SettingReqDto.RegionDto regionDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        UserRegion userRegion = user.getSetting().getUserRegion();
        userRegion.update(regionDto.regionName(), regionDto.longitude(), regionDto.latitude());
        userRegionRepository.save(userRegion);
    }

    // 사용자 가중치 설정
    public void updateWeight(String email, SettingReqDto.WeightDto weightDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Setting setting = user.getSetting();
        setting.setWeight(weightDto.weight());

        // TODO:  Kafka 전송
//        KafkaDto.UserSettingsDto settingDto =
//                KafkaConverter.toSettingDto(user, "weight", String.valueOf(weightDto.weight()));
//        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

}
