package com.waither.domain.noti.repository.jpa;

import com.waither.domain.noti.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByUser_Email(String email);
}
