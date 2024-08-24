package com.waither.domain.notification.service;

import com.waither.domain.noti.dto.response.NotificationResponse;
import com.waither.domain.noti.entity.Notification;
import com.waither.domain.noti.repository.jpa.NotificationRepository;
import com.waither.domain.noti.repository.redis.NotificationRecordRepository;
import com.waither.domain.noti.service.AlarmService;
import com.waither.domain.noti.service.NotificationService;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.domain.user.repository.UserDataRepository;
import com.waither.domain.user.repository.UserMedianRepository;
import com.waither.domain.user.repository.UserRepository;
import com.waither.domain.weather.service.WeatherService;
import com.waither.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserMedianRepository userMedianRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SettingRepository settingRepository;
    @Mock
    private UserDataRepository userDataRepository;
    @Mock
    private NotificationRecordRepository notificationRecordRepository;
    @Mock
    private AlarmService alarmService;
    @Mock
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNotificationsTest() {
        //given
        User user = User.builder().id(1L).build();
        Pageable pageable = Pageable.unpaged();
        List<Notification> notifications = Arrays.asList(
                Notification.builder().id("1").title("title1").content("content1").user(user).build(),
                Notification.builder().id("2").title("title2").content("content2").user(user).build()
        );
        Slice<NotificationResponse> page
                = new SliceImpl<>(notifications.stream().map(NotificationResponse::of).toList());

        when(notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageable)).thenReturn(page);

        //when
        List<NotificationResponse> result = notificationService.getNotifications(user, pageable);

        //then
        assertEquals(2, result.size());
        assertEquals("title1", result.get(0).title());
        assertEquals("content2", result.get(1).content());
    }

    @Test
    void deleteNotificationTest() {
        //given
        User user = User.builder().email("test@example.com").build();
        Notification notification = Notification.builder()
                .id("1")
                .title("Title")
                .content("Content")
                .user(user)
                .build();
        when(notificationRepository.findById("1")).thenReturn(Optional.of(notification));

        //when
        assertDoesNotThrow(() -> notificationService.deleteNotification(user, "1"));

        //then
        verify(notificationRepository, times(1)).delete(notification);
    }

    @Test
    void deleteNotificationUnauthorizedTest() {
        //given
        User user = User.builder().email("test@example.com").build();
        User otherUser = User.builder().email("other@example.com").build();
        Notification notification = Notification.builder()
                .id("1")
                .title("Title")
                .content("Content")
                .user(otherUser)
                .build();

        when(notificationRepository.findById("1")).thenReturn(Optional.of(notification));

        //when , then
        assertThrows(CustomException.class, () -> notificationService.deleteNotification(user, "1"));
    }

//    @Test
//    void sendGoOutAlarmTest() {
//        User user = User.builder()
//                .email("test@example.com")
//                .nickname("TestUser")
//                .build();
//        LocationDto location = new LocationDto(37.5665, 126.9780);
//        Setting setting = Setting.builder().userAlert(true).build();
//        UserData userData = UserData.builder().build();
//        UserMedian userMedian = UserMedian.builder().build();
//        MainWeatherResponse mainWeather = new MainWeatherResponse("20", Arrays.asList("0", "0", "0", "0", "0", "0"));
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//        when(settingRepository.findByUser(user)).thenReturn(Optional.of(setting));
//        when(userDataRepository.findByUser(user)).thenReturn(Optional.of(userData));
//        when(userMedianRepository.findByUserAndSeason(eq(user), any())).thenReturn(Optional.of(userMedian));
//        when(weatherService.getMainWeather(location.latitude(), location.longitude())).thenReturn(mainWeather);
//
//        String result = notificationService.sendGoOutAlarm(user, location);
//
//        assertTrue(result.contains("TestUser님"));
//        verify(alarmService, times(1)).sendSingleAlarmByUser(eq(user), anyString(), anyString());
//    }
//
//    @Test
//    void updateLocationTest() {
//        User user = User.builder().email("test@example.com").build();
//        LocationDto location = new LocationDto(37.5665, 126.9780);
//        String region = "Seoul";
//
//        when(weatherService.convertGpsToRegionName(location.latitude(), location.longitude())).thenReturn(region);
//        when(notificationRecordRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//
//        notificationService.updateLocation(user, location);
//
//        verify(notificationRecordRepository, times(1)).save(any(NotificationRecord.class));
//    }

}
