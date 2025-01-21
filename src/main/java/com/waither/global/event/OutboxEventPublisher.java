package com.waither.global.event;

import com.waither.global.event.entity.OutBoxEvent;
import com.waither.global.event.entity.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class OutboxEventPublisher {

    private static final int BATCH_SIZE = 10;
    private static final int MAX_RETRY = 3;
    private static final Duration PROCESSING_TIMEOUT = Duration.ofMinutes(5);

    private final OutboxEventRepository outboxEventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EventConverter eventConverter;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void publishEvents() {
        // 처리 타임아웃 시간
        LocalDateTime timeoutThreshold = LocalDateTime.now().minus(PROCESSING_TIMEOUT);

        log.info("[Outbox Event Publisher] Finding Unpublished Events ...");
        List<OutBoxEvent> events = outboxEventRepository.findUnpublishedEventsWithLock(
                MAX_RETRY,
                timeoutThreshold,
                Pageable.ofSize(BATCH_SIZE)
        );

        for (OutBoxEvent event : events) {
            log.info("[Outbox Event Publisher] Publishing event --> {}", event.getId());
            try {
                //이벤트 클래스 변환
                WeatherEventAbstract weatherEvent = eventConverter.fromJson(event.getPayload(), event.getEventType().getEventClass());

                //처리 시간 기록
                event.setLastProcessedAt(LocalDateTime.now());
                //이미 실패한 이벤트라면 재시도 횟수 +1
                if (event.getStatus() == OutboxStatus.FAILED) {
                    event.increaseRetryCount();
                }

                //이벤트 발행
                eventPublisher.publishEvent(weatherEvent);
                log.info("[Outbox Event Publisher] Publishing success --> {}", event.getId());
                event.setStatus(OutboxStatus.PUBLISHED);
            } catch (Exception e) {
                e.printStackTrace();
                handlePublishingFailure(event, e);
            }
        }
    }


    private void handlePublishingFailure(OutBoxEvent event, Exception e) {
        log.info("[Outbox Event Publisher] Publishing Failed --> {}", event.getId());

        event.setStatus(OutboxStatus.FAILED);
        event.setExceptionMessage(e.getCause() == null ? "unknown" : e.getCause().getMessage());
        event.increaseRetryCount();
    }
}
