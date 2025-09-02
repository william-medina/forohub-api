package com.williammedina.forohub.domain.notification.repository;

import com.williammedina.forohub.domain.notification.entity.Notification;
import com.williammedina.forohub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

}
