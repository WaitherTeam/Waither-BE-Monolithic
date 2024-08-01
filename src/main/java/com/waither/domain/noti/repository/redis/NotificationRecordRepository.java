package com.waither.domain.noti.repository.redis;

import com.waither.domain.noti.entity.redis.NotificationRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NotificationRecordRepository extends CrudRepository<NotificationRecord, String > {

    Optional<NotificationRecord> findByEmail(String email);



}
