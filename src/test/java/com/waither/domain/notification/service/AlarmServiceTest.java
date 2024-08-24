package com.waither.domain.notification.service;

//@SpringBootTest
public class AlarmServiceTest {

//    @InjectMocks
//    private AlarmService alarmService;
//
//    @Mock
//    private RedisUtil redisUtil;
//    @Mock
//    private FireBaseUtil fireBaseUtil;
//    @Mock
//    private NotificationRepository notificationRepository;
//    @Mock
//    private UserRepository userRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void updateTokenTest() {
//        User user = User.builder().email("test@example.com").build();
//        TokenDto tokenDto = new TokenDto("testToken");
//
//        alarmService.updateToken(user, tokenDto);
//
//        verify(redisUtil, times(1)).save("firebase_test@example.com", "testToken");
//    }
//
//    @Test
//    void sendSingleAlarmByUserTest() {
//        User user = User.builder().email("test@example.com").build();
//        String title = "Test Title";
//        String message = "Test Message";
//
//        when(redisUtil.get(user.getEmail())).thenReturn("testToken");
//
//        alarmService.sendSingleAlarmByUser(user, title, message);
//
//        verify(notificationRepository, times(1)).save(any(Notification.class));
//    }
//
//    @Test
//    void sendAlarmsByEmailsTest() {
//        List<String> emails = Arrays.asList("test1@example.com", "test2@example.com");
//        String title = "Test Title";
//        String message = "Test Message";
//        List<User> users = Arrays.asList(
//                User.builder().email("test1@example.com").build(),
//                User.builder().email("test2@example.com").build()
//        );
//
//        when(redisUtil.get(anyString())).thenReturn("testToken");
//        when(userRepository.findAllByEmailIn(emails)).thenReturn(users);
//
//        alarmService.sendAlarmsByEmails(emails, title, message);
//
//        verify(notificationRepository, times(1)).saveAll(anyList());
//    }
}
