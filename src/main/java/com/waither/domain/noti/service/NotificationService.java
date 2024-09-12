package com.waither.domain.noti.service;

import com.waither.domain.noti.dto.request.LocationDto;
import com.waither.domain.noti.dto.request.SqsMessageDto;
import com.waither.domain.noti.dto.response.NotificationResponse;
import com.waither.domain.noti.entity.Notification;
import com.waither.domain.noti.entity.redis.NotificationRecord;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.entity.UserData;
import com.waither.domain.user.entity.UserMedian;
import com.waither.domain.user.entity.enums.Season;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.domain.user.repository.UserDataRepository;
import com.waither.domain.user.repository.UserMedianRepository;
import com.waither.domain.noti.repository.jpa.NotificationRepository;
import com.waither.domain.noti.repository.redis.NotificationRecordRepository;
import com.waither.domain.user.repository.UserRepository;
import com.waither.domain.weather.dto.response.MainWeatherResponse;
import com.waither.domain.weather.service.WeatherService;
import com.waither.global.exception.CustomException;
import com.waither.global.response.ErrorCode;
import com.waither.global.response.NotiErrorCode;
import com.waither.domain.user.exception.UserErrorCode;
import com.waither.global.utils.AwsSqsUtils;
import com.waither.global.utils.RedisUtil;
import com.waither.global.utils.WeatherMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserMedianRepository userMedianRepository;
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;
    private final UserDataRepository userDataRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final TokenService tokenService;
    private final WeatherService weatherService;
    private final RedisUtil redisUtil;
    private final AwsSqsUtils awsSqsUtils;


    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(User currentUser, Pageable pageable) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable).toList();
    }

    @Transactional
    public void deleteNotification(User currentUser, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_404));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_401);
        }

        notificationRepository.delete(notification);

    }

    @Transactional
    public String sendGoOutAlarm(User currentUser, LocationDto location) {

        User user = userRepository.findById(currentUser.getId()).orElseThrow(
                () -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Setting setting = settingRepository.findByUser(user).orElseThrow(
                () -> new CustomException(NotiErrorCode.USER_SETTINGS_NOT_FOUND));

        UserData userData = userDataRepository.findByUser(user).orElseThrow(
                () -> new CustomException(NotiErrorCode.USER_DATA_NOT_FOUND));;

        Season currentSeason = WeatherMessageUtil.getCurrentSeason();

        String title = generateWeatherForecastTitleByToday();
        StringBuilder sb = new StringBuilder();

        //메인 날씨 정보
        MainWeatherResponse mainWeather = weatherService.getMainWeather(location.latitude(), location.longitude());

        /*
          1. 기본 메세지 시작 형식
         */
        String nickName = user.getNickname();
        sb.append(nickName).append("님, 오늘은 ");

        /*
          2. 당일 온도 정리 - Weather Service
          {@link com.waither.notiservice.enums.Expressions} 참고
         */

        //현재 온도라고 함 -> 평균 온도 구하기 알아보는 중
        double avgTemp = Double.parseDouble(mainWeather.temp());

        if (setting.isUserAlert()) {
            //사용자 맞춤 알림이 on이라면 -> 계산 후 전용 정보 제공
            UserMedian userMedian = userMedianRepository.findByUserAndSeason(user, currentSeason).orElseThrow(
                    () ->  new CustomException(NotiErrorCode.USER_MEDIAN_NOT_FOUND));

            sb.append(WeatherMessageUtil.createUserDataMessage(userMedian, avgTemp, setting.getWeight()));
        } else {
            //사용자 맞춤 알림이 off라면 -> 하루 평균 온도 정보 제공
            sb.append("평균 온도가 ").append(avgTemp).append("도입니다.");
        }


        /*
          3. 강수 정보 가져오기 - Weather Service
         */
        List<Double> predictions = mainWeather.expectedRain().stream()
                        .map(String::trim) //공백 제거
                        .map(s -> s.equals("강수없음") ? "0" : s)
                        .map(Double::parseDouble)
                        .toList();
        String rainPredictionMessage = WeatherMessageUtil.getRainPredictionsMessage(predictions);
        sb.append(rainPredictionMessage);

        //알림 보내기
        log.info("[ Notification Service ] Final Message ---> {}", sb.toString());
        String token = redisUtil.get("fcm_" + user.getId()).toString();
        awsSqsUtils.sendMessage(new SqsMessageDto(List.of(token), title, sb.toString()));
        save(user, title, sb.toString());
        return sb.toString();
    }

    private static String generateWeatherForecastTitleByToday() {
        LocalDateTime now = LocalDateTime.now();
        return now.getMonth() + "월 " + now.getDayOfMonth() + "일 날씨 정보입니다.";
    }

    //현재 위치 업데이트
    @Transactional
    public void updateLocation(User currentUser, LocationDto locationDto) {

        log.info("[ Notification Service ]  currentUser.getId() ---> {}", currentUser.getId());
        log.info("[ Notification Service ]  현재 위치 위도 (latitude) ---> {}", locationDto.latitude());
        log.info("[ Notification Service ]  현재 위치 경도 (longitude) ---> {}", locationDto.longitude());

        String region = weatherService.convertGpsToRegionName(locationDto.latitude(), locationDto.longitude());

        saveOrUpdateLocation(currentUser, region);

    }

    private void saveOrUpdateLocation(User currentUser, String region) {
        LocalDateTime fourHoursAgo = LocalDateTime.now().minusHours(4);
        notificationRecordRepository.findByEmail(currentUser.getEmail())
                .ifPresentOrElse(
                        record -> {
                            //만약 지역이 변경됐다면 새로운 알림을 받기 위해 마지막 알림을 4시간 전으로 초기화
                            if (!record.getRegion().equals(region)) {
                                record.setLastWindAlarmReceived(fourHoursAgo);
                                record.setLastRainAlarmReceived(fourHoursAgo);
                            }
                            record.setRegion(region);
                        },
                        //저장된 알림 레코드가 없을 경우
                        () -> notificationRecordRepository.save(
                                NotificationRecord.builder()
                                        .email(currentUser.getEmail())
                                        .region(region)
                                        .lastRainAlarmReceived(fourHoursAgo)
                                        .lastWindAlarmReceived(fourHoursAgo)
                                        .build()
                        )
                );
    }

    @Transactional
    public void save(User user, String title, String message) {
        notificationRepository.save(Notification.builder()
                .user(user)
                .title(title)
                .content(message)
                .build());
    }

    @Transactional
    public void saveAll(List<String> emails, String title, String message) {
        //Bulk 조회
        List<User> users = userRepository.findAllByEmailIn(emails);
        Map<String, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getEmail, Function.identity()));

        List<Notification> notifications = emails.stream()
                .map(email -> Notification.builder()
                        .user(userMap.get(email))
                        .title(title)
                        .content(message)
                        .build())
                .toList();


        notificationRepository.saveAll(notifications);
    }
}
