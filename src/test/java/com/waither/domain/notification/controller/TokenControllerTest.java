package com.waither.domain.notification.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Notification - token 컨트롤러 테스트")
public class TokenControllerTest {

//    @InjectMocks
//    private TokenController tokenController;
//
//    @Mock
//    private AlarmService alarmService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void updateTokenTest() {
//        User user = User.builder().id("1").email("test@example.com").build();
//        TokenDto tokenDto = TokenDto.builder().token("testToken").build();
//
//        ApiResponse<?> response = tokenController.updateToken(user, tokenDto);
//
//        assertEquals("토큰 업로드가 완료되었습니다.", response.getResult());
//        verify(alarmService, times(1)).updateToken(user, tokenDto);
//    }
}
