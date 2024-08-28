package com.waither.domain.noti.dto.request;

import lombok.AllArgsConstructor;

public record SqsMessageDto(
        String token,
        String title,
        String content
) {
}
