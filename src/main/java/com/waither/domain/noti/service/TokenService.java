package com.waither.domain.noti.service;

import com.waither.domain.noti.dto.request.TokenDto;
import com.waither.domain.user.entity.User;
import com.waither.global.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final RedisUtil redisUtil;

    public void updateToken(User currentUser, TokenDto tokenDto) {
        redisUtil.save("fcm_"+currentUser.getEmail(), tokenDto.token());
    }


}
