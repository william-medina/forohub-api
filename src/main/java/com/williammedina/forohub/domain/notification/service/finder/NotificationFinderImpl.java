package com.williammedina.forohub.domain.notification.service.finder;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import com.williammedina.forohub.domain.notification.repository.NotificationRepository;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationFinderImpl implements NotificationFinder{

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationEntity findNotificationById(Long notifyId) {
        return notificationRepository.findById(notifyId)
                .orElseThrow(() -> {
                    log.warn("Notification not found with ID: {}", notifyId);
                    return new AppException("Notificación no encontrada", HttpStatus.NOT_FOUND);
                });
    }

}
