package com.williammedina.forohub.domain.notification.dto;

import com.williammedina.forohub.domain.notification.Notification;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        String username,
        Long topicId,
        Notification.Type type,
        Notification.Subtype subtype,
        String title,
        String message,
        Boolean isRead,
        LocalDateTime createdAt
) {
}
