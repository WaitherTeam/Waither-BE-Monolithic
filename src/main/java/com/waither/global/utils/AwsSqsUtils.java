package com.waither.global.utils;

import com.waither.config.AwsSqsProperties;
import com.waither.domain.noti.dto.request.SqsMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsSqsUtils {

    private final SqsAsyncClient sqsAsyncClient;
    private final AwsSqsProperties awsSqsProperties;

    public void sendMessage(SqsMessageDto messageDto) {
        Map<String, MessageAttributeValue> attributes = createAttributes(messageDto.tokens(), messageDto.title());

        SendMessageRequest request = createRequest(messageDto, attributes);

        try {
            CompletableFuture<SendMessageResponse> future = sqsAsyncClient.sendMessage(request);
            future.whenComplete((sendMessageResponse, throwable) -> {
                if (throwable == null) {
                    log.info("[SQS Async Client] 메세지 전송 성공 Status ---> {}", sendMessageResponse.sdkHttpResponse().statusCode());
                    log.info("[SQS Async Client] 메세지 전송 성공 ID ---> {}", sendMessageResponse.messageId());
                } else {
                    log.error("[SQS Async Client] 메세지 전송 실패 ---> {}", throwable.getMessage());
                }
            });

        } catch (SqsException sqsException) {
            log.error("[SQS] SQS 메세지 전송 실패 --> {}", sqsException.getMessage());
        }
    }

    private SendMessageRequest createRequest(SqsMessageDto messageDto, Map<String, MessageAttributeValue> attributes) {
        return SendMessageRequest.builder()
                .queueUrl(awsSqsProperties.getQueueUrl())
                .delaySeconds(awsSqsProperties.getMessageDelaySecs())
                .messageAttributes(attributes)
                .messageBody(messageDto.content())
                .build();
    }

    public void sendMessages(List<String> tokens, String title, String content) {
        sendMessage(new SqsMessageDto(tokens, title, content));
    }

    private static Map<String, MessageAttributeValue> createAttributes(List<String> tokens, String title) {
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("token", MessageAttributeValue.builder().stringValue(tokens.toString()).dataType("String").build());
        attributes.put("title", MessageAttributeValue.builder().stringValue(title).dataType("String").build());
        return attributes;
    }

}
