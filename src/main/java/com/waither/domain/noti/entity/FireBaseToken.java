package com.waither.domain.noti.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash(value = "firebase", timeToLive = 30000L) //TODO : TTL 어떻게 설정?
public class FireBaseToken {

    @Id
    private String email;

    private String token;
}
