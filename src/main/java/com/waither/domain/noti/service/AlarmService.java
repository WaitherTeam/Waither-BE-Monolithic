package com.waither.domain.noti.service;

import com.waither.domain.noti.api.request.TokenDto;
import com.waither.domain.noti.entity.Notification;
import com.waither.domain.noti.repository.jpa.NotificationRepository;
import com.waither.global.utils.FireBaseUtil;
import com.waither.global.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final RedisUtil redisUtil;
    private final FireBaseUtil fireBaseUtil;
    private final NotificationRepository notificationRepository;

    public void updateToken(String email, TokenDto tokenDto) {
        redisUtil.save(email, tokenDto.token());
    }

    public void sendSingleAlarm(String email, String title, String message) {
        String token = String.valueOf(redisUtil.get(email));
//        fireBaseUtils.sendSingleMessage(token, title, message);
        notificationRepository.save(Notification.builder()
                .email(email)
                .title(title)
                .content(message)
                .build());
    }

    public void sendAlarms(List<String> userEmails, String title, String message) {
        List<String> tokens = userEmails.stream()
                .map(email -> String.valueOf(redisUtil.get(email)))
                .toList();

        log.info("[ 푸시알림 ] Email ---> {}", userEmails);
        log.info("[ 푸시알림 ] message ---> {}", message);

//        fireBaseUtils.sendAllMessages(tokens,title, message);

        List<Notification> notifications = userEmails.stream()
                .map(email -> Notification.builder()
                        .email(email)
                        .title(title)
                        .content(message)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}
