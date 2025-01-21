package com.waither.global.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConverter {
    private final ObjectMapper objectMapper;

    public String toJson(WeatherEventAbstract event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }

    public <T extends WeatherEventAbstract> T fromJson(String json, Class<T> eventClass) throws JsonProcessingException {
        return objectMapper.readValue(json, eventClass);
    }
}
