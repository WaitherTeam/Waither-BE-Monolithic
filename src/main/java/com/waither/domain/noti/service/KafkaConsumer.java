package com.waither.domain.noti.service;

import com.waither.notiservice.domain.UserData;
import com.waither.notiservice.domain.UserMedian;
import com.waither.notiservice.domain.redis.NotificationRecord;
import com.waither.notiservice.dto.kafka.KafkaDto;
import com.waither.notiservice.enums.Season;
import com.waither.notiservice.repository.jpa.UserDataRepository;
import com.waither.notiservice.repository.jpa.UserMedianRepository;
import com.waither.notiservice.repository.redis.NotificationRecordRepository;
import com.waither.notiservice.utils.RedisUtils;
import com.waither.notiservice.utils.WeatherMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final AlarmService alarmService;
    private final UserDataRepository userDataRepository;
    private final UserMedianRepository userMedianRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final RedisUtils redisUtils;

    /**
     * 중앙값 동기화 Listener
     * */
    @Transactional
    @KafkaListener(topics = "${spring.kafka.template.user-median-topic}", containerFactory = "userMedianKafkaListenerContainerFactory")
    public void consumeUserMedian(KafkaDto.UserMedianDto userMedianDto) {

        Season currentSeason = WeatherMessageUtils.getCurrentSeason();
        log.info("[ Kafka Listener ] 사용자 중앙값 데이터 동기화");
        log.info("[ Kafka Listener ] Season : -- {} ", currentSeason.name());
        log.info("[ Kafka Listener ] Email : --> {}", userMedianDto.email());


        Optional<UserMedian> optionalUserMedian = userMedianRepository.findByEmailAndSeason(userMedianDto.email(), currentSeason);
        if (optionalUserMedian.isPresent()) {
            //User Median 이미 있을 경우
            UserMedian userMedian = optionalUserMedian.get();
            userMedian.setLevel(userMedianDto);
        } else {
            //User Median 없을 경우 생성
            //TODO : 계절당 초기값 받아야 함
            log.warn("[ Kafka Listener ] User Median 초기값이 없었습니다.");
            UserMedian newUserMedian = UserMedian.builder()
                    .email(userMedianDto.email())
                    .season(currentSeason)
                    .build();
            newUserMedian.setLevel(userMedianDto);
            userMedianRepository.save(newUserMedian);
        }
    }


    /**
     * User Settings Listener
     * */
    @Transactional
    @KafkaListener(topics = "${spring.kafka.template.user-settings-topic}", containerFactory = "userSettingsKafkaListenerContainerFactory")
    public void consumeUserSettings(KafkaDto.UserSettingsDto userSettingsDto) {

        log.info("[ Kafka Listener ] 사용자 설정값 데이터 동기화");
        log.info("[ Kafka Listener ] Email : --> {}", userSettingsDto.email());
        log.info("[ Kafka Listener ] Key : --> {}", userSettingsDto.key());
        log.info("[ Kafka Listener ] Value : --> {}", userSettingsDto.value());

        Optional<UserData> userData = userDataRepository.findByEmail(userSettingsDto.email());
        if (userData.isPresent()) {
            userData.get().updateValue(userSettingsDto.key(), userSettingsDto.value());
        } else {
            log.warn("[ Kafka Listener ] User Data 초기값이 없었습니다.");
            UserData newUserData = UserData.builder()
                    .email(userSettingsDto.email())
                    .build();
            newUserData.updateValue(userSettingsDto.key(), userSettingsDto.value());
            userDataRepository.save(newUserData);
        }

    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.template.initial-data-topic}", containerFactory = "initialDataKafkaListenerContainerFactory")
    public void consumeUserInit(KafkaDto.InitialDataDto initialDataDto) {

        log.info("[ Kafka Listener ] 초기 설정값 세팅");
        log.info("[ Kafka Listener ] email --> {}", initialDataDto.email());
        userDataRepository.save(initialDataDto.toUserDataEntity());
        userMedianRepository.saveAll(initialDataDto.toUserMedianList());
    }




    /**
     * 바람 세기 알림 Listener
     * @Query  : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300  */
    @Transactional
    @KafkaListener(topics = "alarm-wind", containerFactory = "weatherKafkaListenerContainerFactory")
    public void consumeWindAlarm(@Payload KafkaDto.WeatherDto weatherDto) {

        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return;
        }

        String title = "Waither 바람 세기 알림";
        StringBuilder sb = new StringBuilder();

        String region = weatherDto.region();
        Double windStrength = Double.valueOf(weatherDto.message()); //바람세기

        log.info("[ Kafka Listener ] 바람 세기");
        log.info("[ Kafka Listener ] Wind Strength : --> {}", windStrength);

        // Wind Alert를 True로 설정한 User Query
        List<UserData> userData = userDataRepository.findAllByWindAlertIsTrue();
        if (userData.isEmpty()) {
            log.info("[ Kafka Listener ] 보낼 사용자 없음.");
            return;
        }

        //알림 보낼 사용자 이메일
        List<String> userEmails = filterRegionAndWindAlarm(region, userData, currentHour);
        if (userEmails.isEmpty()) {
            log.info("[ Kafka Listener ] 보낼 사용자 없음.");
            return;
        }

        sb.append("현재 바람 세기가 ").append(windStrength).append("m/s 이상입니다.");

        System.out.println("[ 푸시알림 ] 바람 세기 알림");

        alarmService.sendAlarms(userEmails,title, sb.toString());

        //Record 알림 시간 초기화
        userEmails
                .forEach(email -> {
                    Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
                    notificationRecord.ifPresent(NotificationRecord::initializeWindTime);
                });
    }


    /**
     * 강수 정보 알림 Listener <br>
     * 기상청 기준 <br>
     * 약한 비     1~3mm <br>
     * 보통 비     3~15mm <br>
     * 강한 비     15~30mm <br>
     * 매우 강한 비 30mm 이상 <br>
     * <a href="https://www.kma.go.kr/kma/biz/forecast05.jsp">참고</a>
     */
    @Transactional
    @KafkaListener(topics = "alarm-rain", containerFactory = "weatherKafkaListenerContainerFactory")
    public void consumeRain(@Payload KafkaDto.WeatherDto weatherDto) {

        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return;
        }

        //지역
        String region = weatherDto.region();
        log.info("[ Kafka Listener ] 강수량  지역 --> {}", region);
        String message = weatherDto.message();

        String title = "Waither 강수 정보 알림";
        List<UserData> userData = userDataRepository.findAllBySnowAlertIsTrue();
        if (userData.isEmpty()) {
            log.info("[ Kafka Listener ] 보낼 사용자 없음.");
            return;
        }

        List<String> userEmails = filterRegionAndRainAlarm(region, userData, currentHour);
        if (userEmails.isEmpty()) {
            log.info("[ Kafka Listener ] 보낼 사용자 없음.");
            return;
        }

        StringBuilder sb = new StringBuilder();


        //1시간 뒤, 2시간 뒤, 3시간 뒤, 4시간 뒤, 5시간 뒤, 6시간 뒤
        List<Double> predictions =  Arrays.stream(message.split(","))
                .map(String::trim) //공백 제거
                .map(s -> s.equals("강수없음") ? "0" : s)
                .map(Double::parseDouble)
                .toList();
        String rainMessage = WeatherMessageUtils.getRainPredictions(predictions);

        if (rainMessage == null) {
            log.info("[ Kafka Listener ] 6시간 동안 강수 정보 없음.");
            //6시간 동안 강수 정보 없음
            return;
        }

        sb.append("현재 ").append(region).append(" 지역에 ").append(rainMessage);
        //알림 보낼 사용자 이메일


        System.out.println("[ 푸시알림 ] 강수량 알림");
        alarmService.sendAlarms(userEmails, title, sb.toString());

        //Record 알림 시간 초기화
        userEmails
                .forEach(email -> {
                    Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
                    notificationRecord.ifPresent(NotificationRecord::initializeRainTime);
                });

    }


    /**
     * 기상 특보 알림 Listener
     * */
    @Transactional
    @KafkaListener(topics = "alarm-climate", containerFactory = "weatherKafkaListenerContainerFactory")
    public void consumeClimateAlarm(@Payload KafkaDto.WeatherDto weatherDto) {
        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return;
        }

        String title = "Waither 기상 특보 알림";
        StringBuilder sb = new StringBuilder();

        log.info("[ Kafka Listener ] 기상 특보");

        String region = weatherDto.region();
        String message = weatherDto.message();

        // Wind Climate를 True로 설정한 User Query
        List<UserData> userData = userDataRepository.findAllByClimateAlertIsTrue();
        if (userData.isEmpty()) {
            log.info("[ Kafka Listener ] 보낼 사용자 없음.");
            return;
        }

        // 알림 보낼 사용자 이메일
        List<String> userEmails = filterRegion(region, userData);
        if (userEmails.isEmpty()) {
            log.info("[ Kafka Listener ] 보낼 사용자 없음.");
            return;
        }

        sb.append("[기상청 기상 특보] ").append(message);

        System.out.println("[ 푸시알림 ] 기상 특보 알림");
        alarmService.sendAlarms(userEmails, title, sb.toString());
    }


    //지역 필터링 & 알림 규칙 검사

    private List<String> filterRegionAndWindAlarm(String region, List<UserData> userData, int currentHour) {
        return userData.stream()
                .filter(data -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(data.getEmail());
                    return notiRecord.map(notificationRecord ->
                            (Math.abs(notiRecord.get().getLastWindAlarmReceived().getHour() - currentHour) >=3
                                    && notificationRecord.getRegion().equals(region) )
                    ).orElse(false);
                })
                .map(UserData::getEmail)
                .toList();
    }
    //지역 필터링 & 알림 규칙 검사

    private List<String> filterRegionAndRainAlarm(String region, List<UserData> userData, int currentHour) {
        return userData.stream()
                .filter(data -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(data.getEmail());
                    return notiRecord.map(notificationRecord ->
                            (Math.abs(notiRecord.get().getLastRainAlarmReceived().getHour() - currentHour) >=3
                                    && notificationRecord.getRegion().equals(region) )
                    ).orElse(false);
                })
                .map(UserData::getEmail)
                .toList();
    }
    private List<String> filterRegion(String region, List<UserData> userData) {
        return userData.stream()
                .filter(data -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(data.getEmail());
                    return notiRecord.map(notificationRecord -> notificationRecord.getRegion().equals(region)).orElse(false);
                })
                .map(UserData::getEmail)
                .toList();
    }



}
