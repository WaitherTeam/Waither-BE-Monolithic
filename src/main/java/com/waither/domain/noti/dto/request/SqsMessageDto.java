package com.waither.domain.noti.dto.request;

import java.util.List;

public record SqsMessageDto(
        List<String> tokens,
        String title,
        String content
) {
}
