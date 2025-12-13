package com.williammedina.forohub.domain.topic.service.permission;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;
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
public class TopicPermissionServiceImpl implements TopicPermissionService {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public UserEntity getCurrentUser() {
        return authenticatedUserProvider.getAuthenticatedUser();
    }

    @Override
    public UserEntity checkCanModify(TopicEntity topic) {
        UserEntity currentUser = getCurrentUser();
        if (!topic.getUser().equals(currentUser) && !currentUser.hasElevatedPermissions()) {
            log.warn("User ID: {} attempted to modify topic ID: {} without permission", currentUser.getId(), topic.getId());
            throw new AppException("No tienes permiso para realizar cambios en este tópico", HttpStatus.FORBIDDEN);
        }
        return currentUser;
    }
}
