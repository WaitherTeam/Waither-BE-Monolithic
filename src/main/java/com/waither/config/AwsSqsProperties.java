package com.waither.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AwsSqsProperties {
    @Value("${cloud.aws.sqs.queue.url}")
    private String queueUrl;
    @Value("${cloud.aws.sqs.queue.message-delay-seconds}")
    private Integer messageDelaySecs;
}
