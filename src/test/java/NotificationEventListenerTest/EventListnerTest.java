package NotificationEventListenerTest;

import com.waither.domain.noti.entity.redis.NotificationRecord;
import com.waither.domain.noti.repository.redis.NotificationRecordRepository;
import com.waither.domain.noti.service.AlarmService;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.global.event.WeatherEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class NotificationEventPublisherTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private AlarmService alarmService;
    @Mock
    private NotificationRecordRepository notificationRecordRepository;
    @Mock
    private SettingRepository settingRepository;


    @Test
    void 바람세기_알림_이벤트_발행() {
        // Given
        String region = "TestRegion";
        double windStrength = 10.0;
        WeatherEvent.WindStrength event = new WeatherEvent.WindStrength(region, windStrength);

        Setting setting = mock(Setting.class);
        when(setting.getUser().getEmail()).thenReturn("user@example.com");
        when(settingRepository.findAllByWindAlertIsTrueAndWindDegreeGreaterThan(anyInt()))
                .thenReturn(Collections.singletonList(setting));

        NotificationRecord record = mock(NotificationRecord.class);
        when(record.getRegion()).thenReturn(region);
        when(record.getLastWindAlarmReceived()).thenReturn(LocalDateTime.now().minusHours(4));
        when(notificationRecordRepository.findByEmail("user@example.com")).thenReturn(Optional.of(record));

        // When
        eventPublisher.publishEvent(event);

        // Then
        verify(alarmService).sendAlarmsByEmails(anyList(), anyString(), anyString());
        verify(record).initializeWindTime();
    }

    @Test
    void 예상_강수량_이벤트_발행_테스트() {
        // Given
        String region = "TestRegion";
        List<String> expectRain = Arrays.asList("0", "5", "10", "0", "0", "0");
        WeatherEvent.ExpectRain event = new WeatherEvent.ExpectRain(region, expectRain);

        Setting setting = mock(Setting.class);
        when(setting.getUser().getEmail()).thenReturn("user@example.com");
        when(settingRepository.findAllBySnowAlertIsTrue()).thenReturn(Collections.singletonList(setting));

        NotificationRecord record = mock(NotificationRecord.class);
        when(record.getRegion()).thenReturn(region);
        when(record.getLastRainAlarmReceived()).thenReturn(LocalDateTime.now().minusHours(4));
        when(notificationRecordRepository.findByEmail("user@example.com")).thenReturn(Optional.of(record));

        // When
        eventPublisher.publishEvent(event);

        // Then
        verify(alarmService).sendAlarmsByEmails(anyList(), anyString(), contains("TestRegion 지역에"));
        verify(record).initializeRainTime();
    }

    @Test
    void 기상특보_이벤트_발행_테스트() {
        // Given
        String region = "TestRegion";
        String content = "Severe weather warning";
        WeatherEvent.WeatherWarning event = new WeatherEvent.WeatherWarning(region, content);

        Setting setting = mock(Setting.class);
        when(setting.getUser().getEmail()).thenReturn("user@example.com");
        when(settingRepository.findAllByClimateAlertIsTrue()).thenReturn(Collections.singletonList(setting));

        NotificationRecord record = mock(NotificationRecord.class);
        when(record.getRegion()).thenReturn(region);
        when(notificationRecordRepository.findByEmail("user@example.com")).thenReturn(Optional.of(record));

        // When
        eventPublisher.publishEvent(event);

        // Then
        verify(alarmService).sendAlarmsByEmails(anyList(), anyString(), contains("Severe weather warning"));
    }

    @Test
    void 바람세기_발행_테스트_새벽에는_작동하지_않음() {
        // Given
        String region = "TestRegion";
        double windStrength = 10.0;
        WeatherEvent.WindStrength event = new WeatherEvent.WindStrength(region, windStrength);

        // Mocking current time to be 23:00
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            LocalDateTime fixedDateTime = LocalDateTime.of(2024, 1, 1, 23, 0);
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedDateTime);

            // When
            eventPublisher.publishEvent(event);

            // Then
            verify(alarmService, never()).sendAlarmsByEmails(anyList(), anyString(), anyString());
        }
    }

    @Test
    void publishExpectRainEvent_ShouldNotTriggerAlarmWhenNoRainPredicted() {
        // Given
        String region = "TestRegion";
        List<String> expectRain = Arrays.asList("0", "0", "0", "0", "0", "0");
        WeatherEvent.ExpectRain event = new WeatherEvent.ExpectRain(region, expectRain);

        // When
        eventPublisher.publishEvent(event);

        // Then
        verify(alarmService, never()).sendAlarmsByEmails(anyList(), anyString(), anyString());
    }

    @Test
    void publishWeatherWarningEvent_ShouldNotTriggerAlarmDuringEarlyMorningHours() {
        // Given
        String region = "TestRegion";
        String content = "Severe weather warning";
        WeatherEvent.WeatherWarning event = new WeatherEvent.WeatherWarning(region, content);

        // Mocking current time to be 03:00
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            LocalDateTime fixedDateTime = LocalDateTime.of(2024, 1, 1, 3, 0);
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedDateTime);

            // When
            eventPublisher.publishEvent(event);

            // Then
            verify(alarmService, never()).sendAlarmsByEmails(anyList(), anyString(), anyString());
        }
    }
}