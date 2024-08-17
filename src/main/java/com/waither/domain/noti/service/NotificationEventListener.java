package com.waither.domain.noti.service;

import com.waither.domain.noti.entity.redis.NotificationRecord;
import com.waither.domain.noti.repository.redis.NotificationRecordRepository;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.global.event.WeatherEvent;
import com.waither.global.utils.WeatherMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventListener {

    private final AlarmService alarmService;
    private final NotificationRecordRepository notificationRecordRepository;
    private final SettingRepository settingRepository;


    /**
     * 바람 세기 알림 Listener
     * @Query  : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300  */
    @Async("windStrengthTaskExecutor")
    @TransactionalEventListener(classes = WeatherEvent.WindStrength.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleWindStrength(WeatherEvent.WindStrength windStrengthEvent) {

        String title = "Waither 바람 세기 알림";
        StringBuilder sb = new StringBuilder();

        String region = windStrengthEvent.getRegion();
        Double windStrength = windStrengthEvent.getWindStrength(); //바람세기

        log.info("[ Event Listener ] 바람 세기");
        log.info("[ Event Listener ] Wind Strength : --> {}", windStrengthEvent);

        // Wind Alert를 True로 설정한 User Query
        List<String> emailsToSend = getUsersForWindAlert(region, windStrength.intValue());

        if (emailsToSend.isEmpty()) {
            log.info("[ Event Listener ] 보낼 사용자 없음.");
            return;
        }

        sb.append("현재 바람 세기가 ").append(windStrengthEvent).append("m/s 이상입니다.");

        log.info("[ 푸시 알림 ] 바람 세기 알림 전송");

        alarmService.sendAlarmsByEmails(emailsToSend,title, sb.toString());

        //Record 알림 시간 초기화
        emailsToSend
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
    @Async("expectRainTaskExecutor")
    @TransactionalEventListener(classes = WeatherEvent.ExpectRain.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleExpectRain(WeatherEvent.ExpectRain rainEvent) {

        //지역
        String region = rainEvent.getRegion();
        log.info("[ Event Listener ] 강수량  지역 --> {}", region);
        List<String> expectRain = rainEvent.getExpectRain();

        List<String> emailsToSend = getUsersForRainAlert(region);



        if (emailsToSend.isEmpty()) {
            log.info("[ Event Listener ] 보낼 사용자 없음.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        String title = "Waither 강수 정보 알림";

        //1시간 뒤, 2시간 뒤, 3시간 뒤, 4시간 뒤, 5시간 뒤, 6시간 뒤
        List<Double> predictions =  expectRain.stream()
                .map(String::trim) //공백 제거
                .map(s -> s.equals("강수없음") ? "0" : s)
                .map(Double::parseDouble)
                .toList();
        String predictionMessage = WeatherMessageUtil.getRainPredictionsMessage(predictions);

        if (predictionMessage == null) {
            log.info("[ Event Listener ] 6시간 동안 강수 정보 없음.");
            //6시간 동안 강수 정보 없음
            return;
        }

        sb.append("현재 ").append(region).append(" 지역에 ").append(predictionMessage);
        //알림 보낼 사용자 이메일


        log.info("[ 푸시알림 ] 강수량 알림");
        alarmService.sendAlarmsByEmails(emailsToSend, title, sb.toString());

        //Record 알림 시간 초기화
        emailsToSend
                .forEach(email -> {
                    Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
                    notificationRecord.ifPresent(NotificationRecord::initializeRainTime);
                });
    }


    /**
     * 기상 특보 알림 Listener
     * */
    @Async("weatherWarningTaskExecutor")
    @TransactionalEventListener(classes = WeatherEvent.WeatherWarning.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleWeatherWarning(WeatherEvent.WeatherWarning warningEvent) {

        String title = "Waither 기상 특보 알림";
        StringBuilder sb = new StringBuilder();

        log.info("[ Event Listener ] 기상 특보");

        String region = warningEvent.getRegion();
        String message = warningEvent.getContent();

        List<String> emailsToSend = getUsersForWeatherWarning(region);

        // Wind Climate를 True로 설정한 User Query
        if (emailsToSend.isEmpty()) {
            log.info("[ Event Listener ] 보낼 사용자 없음.");
            return;
        }

        // 알림 보낼 사용자 이메일

        sb.append("[기상청 기상 특보] ").append(message);

        log.info("[ 푸시알림 ] 기상 특보 알림");
        alarmService.sendAlarmsByEmails(emailsToSend, title, sb.toString());
    }


    //지역 필터링 & 알림 규칙 검사
    private List<String> getUsersForWindAlert(String region, int windStrength) {

        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return Collections.emptyList();
        }

        // Wind Alert를 True로 설정한 User Query
        List<Setting> userQueryResult
                = settingRepository.findAllByWindAlertIsTrueAndWindDegreeGreaterThan(windStrength);

        // Notification Record에서 지역, 푸시 알림 시간으로 필터링
        return userQueryResult.stream()
                .map(setting -> setting.getUser().getEmail())
                .filter(email -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(email);
                    return isUserEligibleForWindAlert(region, currentHour, notiRecord);
                })
                .toList();

    }

    private boolean isUserEligibleForWindAlert
            (String region, int currentHour, Optional<NotificationRecord> notiRecord) {
        return notiRecord.map(notificationRecord ->
                (Math.abs(notiRecord.get().getLastWindAlarmReceived().getHour() - currentHour) >= 3
                        && notificationRecord.getRegion().equals(region))
        ).orElse(false);
    }

    //지역 필터링 & 알림 규칙 검사
    private List<String> getUsersForRainAlert(String region) {

        int currentHour = LocalDateTime.now().getHour();
        // 22:00 ~ 07:00 는 알림을 전송하지 않음
        if (currentHour >= 22 || currentHour <= 7) {
            return Collections.emptyList();
        }

        // Wind Alert를 True로 설정한 User Query
        List<Setting> userQueryResult
                = settingRepository.findAllBySnowAlertIsTrue();

        // Notification Record에서 지역, 푸시 알림 시간으로 필터링
        return userQueryResult.stream()
                .map(setting -> {
                    log.info("setting id : {}", setting.getId());
                    log.info("user : {}", setting.getUser());
                    log.info("user email : {}", setting.getUser().getEmail());
                    return setting.getUser().getEmail();
                })
                .filter(email -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(email);
                    return isUserEligibleForRainAlert(region, currentHour, notiRecord);
                })
                .toList();

    }

    private boolean isUserEligibleForRainAlert
            (String region, int currentHour, Optional<NotificationRecord> notiRecord) {
        return notiRecord.map(notificationRecord ->
                (Math.abs(notiRecord.get().getLastRainAlarmReceived().getHour() - currentHour) >= 3
                        && notificationRecord.getRegion().equals(region))
        ).orElse(false);
    }

    //지역 필터링 & 알림 규칙 검사
    private List<String> getUsersForWeatherWarning(String region) {

        int currentHour = LocalDateTime.now().getHour();
        // 02:00 ~ 05:00 는 알림을 전송하지 않음
        if (2 <= currentHour && currentHour <= 5) {
            return Collections.emptyList();
        }

        // Wind Alert를 True로 설정한 User Query
        List<Setting> userQueryResult
                = settingRepository.findAllByClimateAlertIsTrue();

        // Notification Record에서 지역, 푸시 알림 시간으로 필터링
        return userQueryResult.stream()
                .map(setting -> setting.getUser().getEmail())
                .filter(email -> {
                    Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(email);
                    return notiRecord.map(notificationRecord -> notificationRecord.getRegion().equals(region))
                            .orElse(false);
                })
                .toList();

    }

}
