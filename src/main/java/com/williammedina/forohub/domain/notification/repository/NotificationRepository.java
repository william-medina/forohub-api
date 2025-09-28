package com.williammedina.forohub.domain.notification.repository;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByUserOrderByCreatedAtDesc(UserEntity user);

}
