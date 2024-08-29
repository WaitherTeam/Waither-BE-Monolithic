package com.waither.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    //Credential
    private StaticCredentialsProvider createAwsCredentialsProvider() {
        AwsBasicCredentials basicAWSCredentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        return StaticCredentialsProvider.create(basicAWSCredentials);
    }

    //SQS Async Client
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.AP_NORTHEAST_2) //서울 Region
                .credentialsProvider(createAwsCredentialsProvider())
                .build();
    }
}
