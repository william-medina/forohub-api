package com.williammedina.forohub.domain.notification.service.permission;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.context.AuthenticatedUserProvider;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPermissionServiceImpl implements NotificationPermissionService{

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public void checkCanModify(NotificationEntity notification) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!notification.getUser().equals(currentUser)) {
            log.warn("User ID: {} attempted to modify a notification they do not own (ID: {})", currentUser.getId(), notification.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta notificación", HttpStatus.FORBIDDEN);
        }
    }

}
