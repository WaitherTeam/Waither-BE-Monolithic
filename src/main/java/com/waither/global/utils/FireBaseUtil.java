package com.waither.global.utils;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FireBaseUtil {

    //특정 기기에 메시지 전송
//    @Retryable(maxAttempts = 3)
    public void sendSingleMessage(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);

            log.info("[ FireBaseUtils ] Successfully sent message --> {}", response);

        } catch (FirebaseMessagingException ex) {
            ex.printStackTrace();
            log.error("[ FireBaseUtils ] Failed to send message : {}",ex.getMessage());
        }
    }

    // 일괄 메세지 전송
    // 호출당 최대 500기기 등록 가능
    public void sendAllMessages(List<String> tokens, String title, String body) {
        try {

            log.info("[ FireBaseUtils ] Try Sending Messages, Count ---> {}", tokens.size());

            List<Message> messages = tokens.stream().map(
                    token -> Message.builder()
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .setToken(token)
                            .build()
            ).toList();

            //send All 은 deprecated. 대신 sendEach 사용
            BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);

            log.info("[ FireBaseUtils ] Successfully Sent Messages, Count ---> {}", response.getSuccessCount());

            if (response.getFailureCount() > 0) {
                response.getResponses()
                        .forEach( singleResponse -> {
                            if (!singleResponse.isSuccessful()) {
                                log.warn("[ FireBaseUtils ] Failed to send message, id ---> {}", singleResponse.getMessageId());
                            }
                        });
            }

        } catch (FirebaseMessagingException ex) {
            ex.printStackTrace();
            log.error("[ FireBaseUtils ] Failed to send message : {}",ex.getMessage());
        }
    }
}
