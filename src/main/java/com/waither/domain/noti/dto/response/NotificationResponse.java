package com.waither.domain.noti.dto.response;

import com.waither.domain.noti.entity.Notification;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        String id,
        LocalDateTime createdAt,
        String title,
        String content
) {
    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .title(notification.getTitle())
                .content(notification.getContent())
                .build();
    }
}
