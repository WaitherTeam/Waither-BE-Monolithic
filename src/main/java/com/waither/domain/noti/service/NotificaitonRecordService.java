package com.waither.domain.noti.service;

import com.waither.domain.noti.entity.redis.NotificationRecord;
import com.waither.domain.noti.repository.redis.NotificationRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NotificaitonRecordService {

    private final NotificationRecordRepository notificationRecordRepository;

    public void updateWindAlarm(String email) {
        Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);

        notificationRecord.ifPresentOrElse(record -> record.setLastWindAlarmReceived(LocalDateTime.now()), null);
    }

    public void updateRainAlarm(String email) {
        Optional<NotificationRecord> notificationRecord = notificationRecordRepository.findByEmail(email);
        notificationRecord.ifPresent(record -> record.setLastRainAlarmReceived(LocalDateTime.now()));
    }


}
