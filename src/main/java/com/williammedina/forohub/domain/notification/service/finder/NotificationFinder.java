package com.williammedina.forohub.domain.notification.service.finder;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;

public interface NotificationFinder {

    NotificationEntity findNotificationById(Long notifyId);

}
