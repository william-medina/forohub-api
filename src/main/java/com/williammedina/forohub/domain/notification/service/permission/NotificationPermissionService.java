package com.williammedina.forohub.domain.notification.service.permission;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;

public interface NotificationPermissionService {

    void checkCanModify(NotificationEntity notification);

}
