package com.waither.domain.noti.repository.jpa;

import com.waither.domain.noti.dto.response.NotificationResponse;
import com.waither.domain.noti.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    Slice<NotificationResponse> findAllByUserIdOrderByCreatedAtDesc(Long id, Pageable pageable);

    Long countByUserId(Long userId);

}
