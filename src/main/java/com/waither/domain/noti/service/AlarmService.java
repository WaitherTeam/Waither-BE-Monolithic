package com.waither.domain.noti.service;

import com.waither.domain.noti.dto.request.TokenDto;
import com.waither.domain.noti.entity.Notification;
import com.waither.domain.noti.repository.jpa.NotificationRepository;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.utils.FireBaseUtil;
import com.waither.global.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final RedisUtil redisUtil;
    private final FireBaseUtil fireBaseUtil;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void updateToken(String email, TokenDto tokenDto) {
        redisUtil.save(email, tokenDto.token());
    }

    public void sendSingleAlarmByUser(User user, String title, String message) {
        String token = String.valueOf(redisUtil.get(user.getEmail()));
//        fireBaseUtils.sendSingleMessage(token, title, message);
        notificationRepository.save(Notification.builder()
                .user(user)
                .title(title)
                .content(message)
                .build());
    }

    public void sendAlarmsByEmails(List<String> emails, String title, String message) {
        List<String> tokens = emails.stream()
                .map(email -> String.valueOf(redisUtil.get(email)))
                .toList();

        log.info("[ 푸시알림 ] 푸시알림 전송 ---> {}", emails);
        log.info("[ 푸시알림 ] message ---> {}", message);

//        fireBaseUtils.sendAllMessages(tokens,title, message);

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
