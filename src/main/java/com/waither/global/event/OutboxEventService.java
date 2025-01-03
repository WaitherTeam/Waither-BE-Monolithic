package com.waither.global.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waither.global.event.entity.OutBoxEvent;
import com.waither.global.exception.CustomException;
import com.waither.global.response.NotiErrorCode;
import com.waither.global.response.WeatherErrorCode;
import com.waither.global.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final EventConverter eventConverter;

    @Transactional
    public void saveEvent(WeatherEventAbstract event) {
        try {
            String payload = eventConverter.toJson(event);
            outboxEventRepository.save(OutBoxEvent.of(event, payload));
        } catch (JsonProcessingException e) {
            throw new CustomException(NotiErrorCode.JSON_PARSING_ERROR);
        }
    }
}
