package com.waither.domain.user.service.commandService;

import com.waither.userservice.dto.request.SettingReqDto;
import com.waither.userservice.entity.Region;
import com.waither.userservice.entity.Setting;
import com.waither.userservice.entity.User;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.kafka.KafkaConverter;
import com.waither.userservice.kafka.KafkaDto;
import com.waither.userservice.kafka.KafkaService;
import com.waither.userservice.repository.RegionRepository;
import com.waither.userservice.repository.SettingRepository;
import com.waither.userservice.repository.UserRepository;
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
    private final RegionRepository regionRepository;

    private final KafkaService kafkaService;

    /* --------- Update  --------- */

    // 사용자 맞춤 서비스 제공 설정 변경
    public void updateCustom(User user, SettingReqDto.CustomDto customDto) {
        user.setCustom(customDto.custom());
        userRepository.save(user);
    }

    // 메인 화면 날씨 상세 정보 변경
    public void updateDisplay(User user, SettingReqDto.DisplayDto displayDto) {
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
    public void updateOutAlertSet(User user, SettingReqDto.OutAlertSetDto outAlertSetDto) {
        Setting setting = user.getSetting();
        if (!setting.isOutAlert()) {
            throw new CustomException(ErrorCode.INACTIVE_OUT_ALERT_SETTING);
        }
        updateDays(setting, outAlertSetDto.days());

        if (outAlertSetDto.outTime() != null) {
            setting.setOutTime(outAlertSetDto.outTime());
        }
        setting.setOutTime(outAlertSetDto.outTime());
        settingRepository.save(setting);
    }

    // 요일 설정 업데이트
    public void updateDays(Setting setting, List<String> daysToUpdate) {
        EnumSet<DayOfWeek> selectedDays = daysToUpdate.stream()
                .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));

        setting.setDays(selectedDays);
    }

    // 외출 알림 설정 변경
    public void updateOutAlert(User user, SettingReqDto.OutAlertDto outAlertDto) {
        Setting setting = user.getSetting();
        setting.setOutAlert(outAlertDto.outAlert());
        settingRepository.save(setting);
    }

    // 기상 특보 알림 설정 변경
    public void updateClimateAlert(User user, SettingReqDto.ClimateAlertDto climateAlertDto) {
        Setting setting = user.getSetting();
        setting.setClimateAlert(climateAlertDto.climateAlert());

        // Kafka 전송
        KafkaDto.UserSettingsDto settingDto =
                KafkaConverter.toSettingDto(user, "climateAlert", String.valueOf(climateAlertDto.climateAlert()));
        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 사용자 맞춤 예보 설정 변경
    public void updateUserAlert(User user, SettingReqDto.UserAlertDto userAlertDto) {
        if (!user.isCustom()) {
            throw new CustomException(ErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        setting.setUserAlert(userAlertDto.userAlert());

        // Kafka 전송
        KafkaDto.UserSettingsDto settingDto =
                KafkaConverter.toSettingDto(user, "userAlert", String.valueOf(userAlertDto.userAlert()));
        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 강설 정보 알림 설정 변경
    public void updateSnowAlert(User user, SettingReqDto.SnowAlertDto snowAlertDto) {
        if (!user.isCustom()) {
            throw new CustomException(ErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        setting.setSnowAlert(snowAlertDto.snowAlert());

        // Kafka 전송
        KafkaDto.UserSettingsDto settingDto =
                KafkaConverter.toSettingDto(user, "snowAlert", String.valueOf(snowAlertDto.snowAlert()));
        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 바람 세기 알림 설정 변경
    public void updateWind(User user, SettingReqDto.WindDto windDto) {
        if (!user.isCustom()) {
            throw new CustomException(ErrorCode.INACTIVE_CUSTOM_SETTING);
        }
        Setting setting = user.getSetting();
        if (windDto.windAlert() != null) {
            // Kafka 전송
            KafkaDto.UserSettingsDto settingDto =
                    KafkaConverter.toSettingDto(user, "windAlert", String.valueOf(windDto.windAlert()));
            kafkaService.sendUserSettings(settingDto);
            setting.setWindAlert(windDto.windAlert());
        }
        if (windDto.windDegree() != null) {
            // Kafka 전송
            KafkaDto.UserSettingsDto settingDto =
                    KafkaConverter.toSettingDto(user, "windDegree", String.valueOf(windDto.windDegree()));
            kafkaService.sendUserSettings(settingDto);
            setting.setWindDegree(windDto.windDegree());
        }

        settingRepository.save(setting);
    }

    // 직장 지역 레포트 알림 받기
    public void updateRegionReport(User user, SettingReqDto.RegionReportDto regionReportDto) {
        Setting setting = user.getSetting();
        setting.setRegionReport(regionReportDto.regionReport());

        // Kafka 전송
        KafkaDto.UserSettingsDto settingDto =
                KafkaConverter.toSettingDto(user, "regionReport", String.valueOf(regionReportDto.regionReport()));
        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

    // 직장 지역 설정
    public void updateRegion(User user, SettingReqDto.RegionDto regionDto) {
        Region region = user.getSetting().getRegion();
        region.update(regionDto.regionName(), regionDto.longitude(), regionDto.latitude());
        regionRepository.save(region);
    }

    // 사용자 가중치 설정
    public void updateWeight(User user, SettingReqDto.WeightDto weightDto) {
        Setting setting = user.getSetting();
        setting.setWeight(weightDto.weight());

        // Kafka 전송
        KafkaDto.UserSettingsDto settingDto =
                KafkaConverter.toSettingDto(user, "weight", String.valueOf(weightDto.weight()));
        kafkaService.sendUserSettings(settingDto);

        settingRepository.save(setting);
    }

}
