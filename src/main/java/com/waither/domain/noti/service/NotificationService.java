package com.waither.domain.noti.service;

import com.waither.domain.noti.dto.request.LocationDto;
import com.waither.domain.noti.dto.response.MainWeatherResponse;
import com.waither.domain.noti.dto.response.NotificationResponse;
import com.waither.domain.noti.entity.Notification;
import com.waither.domain.noti.entity.redis.NotificationRecord;
import com.waither.domain.user.entity.UserData;
import com.waither.domain.user.repository.UserDataRepository;
import com.waither.domain.user.repository.UserMedianRepository;
import com.waither.domain.noti.repository.jpa.NotificationRepository;
import com.waither.domain.noti.repository.redis.NotificationRecordRepository;
import com.waither.global.exception.CustomException;
import com.waither.global.response.ErrorCode;
import com.waither.global.response.NotiErrorCode;
import com.waither.global.utils.WeatherMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserMedianRepository userMedianRepository;
    private final UserDataRepository userDataRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final AlarmService alarmService;


    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String email) {

        return notificationRepository.findAllByEmail(email)
                .stream().map(NotificationResponse::of).toList();
    }

    @Transactional
    public void deleteNotification(String email, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_404));

        if (!notification.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_401);
        }

        notificationRepository.delete(notification);

    }

    @Transactional
    public String sendGoOutAlarm(String email, LocationDto location) {

        UserData userData = userDataRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(NotiErrorCode.NO_USER_DATA_REGISTERED));

        Season currentSeason = WeatherMessageUtil.getCurrentSeason();

        LocalDateTime now = LocalDateTime.now();
        String title = now.getMonth() + "월 " + now.getDayOfMonth() + "일 날씨 정보입니다.";
        StringBuilder sb = new StringBuilder();

        //메인 날씨 정보
        MainWeatherResponse mainWeather = RestClient.getMainWeather(location);

        /**
         * 1. 기본 메세지 시작 형식
         */
        String nickName = userData.getNickName();
        sb.append(nickName).append("님, 오늘은 ");

        /**
         * 2. 당일 온도 정리 - Weather Service
         * {@link com.waither.notiservice.enums.Expressions} 참고
         */
        //현재 온도라고 함 -> 평균 온도 구하기 알아보는 중
        double avgTemp = Double.parseDouble(mainWeather.temp());

        if (userData.isUserAlert()) {
            //사용자 맞춤 알림이 on이라면 -> 계산 후 전용 정보 제공
            UserMedian userMedian = userMedianRepository.findByEmailAndSeason(email, currentSeason).orElseThrow(
                    () ->  new CustomException(NotiErrorCode.NO_USER_MEDIAN_REGISTERED));

            sb.append(WeatherMessageUtil.createUserDataMessage(userMedian, avgTemp, userData.getWeight()));
        } else {
            //사용자 맞춤 알림이 off라면 -> 하루 평균 온도 정보 제공
            sb.append("평균 온도가 ").append(avgTemp).append("도입니다.");
        }


        /**
         * 3. 강수 정보 가져오기 - Weather Service
         */
        List<Double> predictions = mainWeather.expectedRain().stream()
                        .map(String::trim) //공백 제거
                        .map(s -> s.equals("강수없음") ? "0" : s)
                        .map(Double::parseDouble)
                        .toList();
        String rainPredictionMessage = WeatherMessageUtil.getRainPredictions(predictions);
        sb.append(rainPredictionMessage);

        //알림 보내기
        log.info("[ Notification Service ] Final Message ---> {}", sb.toString());
        alarmService.sendSingleAlarm(email, title, sb.toString());
        return sb.toString();
    }

    //현재 위치 업데이트
    @Transactional
    public void updateLocation(String email, LocationDto locationDto) {

        log.info("[ Notification Service ]  email ---> {}", email);
        log.info("[ Notification Service ]  현재 위치 위도 (latitude) ---> {}", locationDto.latitude());
        log.info("[ Notification Service ]  현재 위치 경도 (longitude) ---> {}", locationDto.longitude());

        Optional<NotificationRecord> notiRecord = notificationRecordRepository.findByEmail(email);

        String region = RestClient.transferToRegion(locationDto);

        if (notiRecord.isPresent()) {
            NotificationRecord notificationRecord = notiRecord.get();

            if (!notiRecord.get().getRegion().equals(region)) {
                //만약 위치가 이동됐다면 알림 시간 초기화
                notificationRecord.setLastWindAlarmReceived(LocalDateTime.now().minusHours(4));
                notificationRecord.setLastRainAlarmReceived(LocalDateTime.now().minusHours(4));
            }
            notificationRecord.setRegion(region);

        } else notificationRecordRepository.save(
                NotificationRecord.builder()
                .email(email)
                .region(region)
                .lastRainAlarmReceived(LocalDateTime.now().minusHours(4))
                .lastWindAlarmReceived(LocalDateTime.now().minusHours(4))
                .build()
        );




    }
}
