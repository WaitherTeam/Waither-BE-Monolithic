package com.waither.global.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waither.global.event.entity.EventType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class WeatherEventAbstract {
    private final String eventId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final EventType eventType;

    public WeatherEventAbstract(EventType eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.createdAt = LocalDateTime.now();
    }

}
