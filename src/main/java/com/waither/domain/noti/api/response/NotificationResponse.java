package com.waither.domain.noti.api.response;

import com.waither.domain.noti.entity.Notification;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        String id,
        LocalDateTime time,
        String message
) {
    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .time(notification.getCreatedAt())
                .message(notification.getContent())
                .build();
    }
}
