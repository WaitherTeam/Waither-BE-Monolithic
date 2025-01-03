package com.waither.global.event.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waither.global.entity.BaseEntity;
import com.waither.global.event.WeatherEvent;
import com.waither.global.event.WeatherEventAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_events")
public class OutBoxEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;

    private EventType eventType;

    //이벤트 페이로드, json 형태로 저장
//    @Column(name = "payload", columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    //재시도 횟수
    private int retryCount;

    //마지막 처리 시도 시간
    private LocalDateTime lastProcessedAt;

    //예외 메세지
    private String exceptionMessage;

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public void increaseRetryCount() {
        retryCount++;
    }

    public void setLastProcessedAt(LocalDateTime lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    public static OutBoxEvent of(WeatherEventAbstract event, String payload) throws JsonProcessingException {
        return OutBoxEvent.builder()
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .status(OutboxStatus.CREATED) //초기 상태는 생성됨
                .retryCount(0) //초기 재시도 횟수는 0
                .build();
    }
}
