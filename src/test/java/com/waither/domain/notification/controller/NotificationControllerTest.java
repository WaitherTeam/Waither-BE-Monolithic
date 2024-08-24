package com.waither.domain.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.waither.annotation.WithMockUser;
import com.waither.domain.noti.controller.NotificationController;
import com.waither.domain.noti.controller.TokenController;
import com.waither.domain.noti.dto.request.LocationDto;
import com.waither.domain.noti.dto.response.NotificationResponse;
import com.waither.domain.noti.service.AlarmService;
import com.waither.domain.noti.service.NotificationService;
import com.waither.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableSpringDataWebSupport
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(email = "test@example.com")
@ExtendWith(SpringExtension.class)
//@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationController notificationController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) //Pageable Default 설정
                .build();
    }

    @Test
    @DisplayName("알림 조회 컨트롤러 테스트")
    @WithMockUser(email = "test@example.com")
    void getNotificationsTest() throws Exception {
        // Given
        User user = User.builder().id(1L).email("test@example.com").build();
        Pageable pageable = PageRequest.of(0, 10);
        List<NotificationResponse> notifications = Arrays.asList(
                NotificationResponse.builder()
                        .id("1")
                        .createdAt(LocalDateTime.now())
                        .title("Title1")
                        .content("Content1")
                        .build(),
                NotificationResponse.builder()
                        .id("2")
                        .createdAt(LocalDateTime.now())
                        .title("Title2")
                        .content("Content2")
                        .build()
        );

        when(notificationService.getNotifications(user, pageable)).thenReturn(notifications);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/noti")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON));

        // Then
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        System.out.println("mvcResult : " + mvcResult.getResponse().getContentAsString());


    }

    @Test
    @DisplayName("알림 삭제 컨트롤러 테스트")
    @WithMockUser(email = "test@example.com")
    void deleteNotificationTest() throws Exception {
        // Given
        User user = User.builder().id(1L).email("test@example.com").build();
        String notificationId = "1";

//        ApiResponse<?> response = notificationController.deleteNotification(user, notificationId);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/noti?id="+notificationId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        System.out.println("mvcResult : " + mvcResult.getResponse().getContentAsString());

    }

    @Test
    @DisplayName("외출 알림 전송 컨트롤러 테스트")
    @WithMockUser(email = "test@example.com")
    void sendGoOutAlarmTest() throws Exception {
        // When
        User user = User.builder().id(1L).email("test@example.com").build();

        String location = getSeoulRegionRequestToString();



//        ApiResponse<?> response = notificationController.sendGoOutAlarm(user, location);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/noti/goOut")
                        .content(location)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        System.out.println("mvcResult : " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("위치 전송 컨트롤러 테스트")
    @WithMockUser(email = "test@example.com")
    void updateLocationTest() throws Exception {
        // When
        User user = User.builder().id(1L).email("test@example.com").build();
        String location = getSeoulRegionRequestToString();

//        ApiResponse<?> response = notificationController.sendGoOutAlarm(user, location);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/noti/location")
                        .content(location)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        System.out.println("mvcResult : " + mvcResult.getResponse().getContentAsString());
    }

    private static String getSeoulRegionRequestToString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        LocationDto location = LocationDto.builder()
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        return ow.writeValueAsString(location);
    }
}
