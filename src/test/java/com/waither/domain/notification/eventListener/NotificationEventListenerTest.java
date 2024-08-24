package com.waither.domain.notification.eventListener;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationEventListenerTest {

//    @InjectMocks
//    private NotificationEventListener notificationEventListener;
//
//    @Mock
//    private AlarmService alarmService;
//    @Mock
//    private NotificationRecordRepository notificationRecordRepository;
//    @Mock
//    private SettingRepository settingRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void handleWindStrengthTest() {
//        WeatherEvent.WindStrength event = new WeatherEvent.WindStrength("Seoul", 10.0);
//        List<String> emails = Arrays.asList("test1@example.com", "test2@example.com");
//        List<Setting> settings = Arrays.asList(
//                Setting.builder().windAlert(true).windDegree(5).build(),
//                Setting.builder().windAlert(true).windDegree(8).build()
//        );
//
//        when(settingRepository.findAllByWindAlertIsTrueAndWindDegreeGreaterThan(anyInt())).thenReturn(settings);
//        when(notificationRecordRepository.findByEmail(anyString())).thenReturn(Optional.of(NotificationRecord.builder().build()));
//
//        notificationEventListener.handleWindStrength(event);
//
//        verify(alarmService, times(1)).sendAlarmsByEmails(anyList(), anyString(), anyString());
//    }
//
//    @Test
//    void handleExpectRainTest() {
//        WeatherEvent.ExpectRain event = new WeatherEvent.ExpectRain("Seoul", Arrays.asList("1", "2", "3", "4", "5", "6"));
//        List<String> emails = Arrays.asList("test1@example.com", "test2@example.com");
//        List<Setting> settings = Arrays.asList(
//                Setting.builder().snowAlert(true).build(),
//                Setting.builder().snowAlert(true).build()
//        );
//
//        when(settingRepository.findAllBySnowAlertIsTrue()).thenReturn(settings);
//        when(notificationRecordRepository.findByEmail(anyString())).thenReturn(Optional.of(NotificationRecord.builder().build()));
//
//        notificationEventListener.handleExpectRain(event);
//
//        verify(alarmService, times(1)).sendAlarmsByEmails(anyList(), anyString(), anyString());
//    }
//
//    @Test
//    void handleWeatherWarningTest() {
//        WeatherEvent.WeatherWarning event = new WeatherEvent.WeatherWarning("Seoul", "Heavy rain warning");
//        List<String> emails = Arrays.asList("test1@example.com", "test2@example.com");
//        List<Setting> settings = Arrays.asList(
//                Setting.builder().climateAlert(true).build(),
//                Setting.builder().climateAlert(true).build()
//        );
//
//        when(settingRepository.findAllByClimateAlertIsTrue()).thenReturn(settings);
//        when(notificationRecordRepository.findByEmail(anyString())).thenReturn(Optional.of(NotificationRecord.builder().build()));
//
//        notificationEventListener.handleWeatherWarning(event);
//
//        verify(alarmService, times(1)).sendAlarmsByEmails(anyList(), anyString(), anyString());
//    }
}
