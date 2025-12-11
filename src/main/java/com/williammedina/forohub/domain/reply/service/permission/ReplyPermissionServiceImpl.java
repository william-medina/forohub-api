package com.williammedina.forohub.domain.reply.service.permission;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.AuthenticatedUserProvider;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyPermissionServiceImpl implements ReplyPermissionService{

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public UserEntity checkCanModify(ReplyEntity reply) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        // If the user is the owner OR has elevated permissions, they are allowed to modify the reply
        if (!reply.getUser().equals(currentUser) && !currentUser.hasElevatedPermissions()) {
            log.warn("User ID: {} without permissions attempted to modify reply ID: {}", currentUser.getId(), reply.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta respuesta", HttpStatus.FORBIDDEN);
        }

        return currentUser;
    }

    @Override
    public void checkElevatedPermissionsForSolution(UserEntity user, Long replyId) {
        if (!user.hasElevatedPermissions()) {
            log.warn("User ID: {} attempted to modify topic state without permission for reply ID: {}", user.getId(), replyId);
            throw new AppException("No tienes permiso para modificar el estado de la respuesta", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void checkCannotDeleteSolution(ReplyEntity reply) {
        if (reply.getSolution()) {
            log.warn("Attempt to delete reply ID: {} marked as solution", reply.getId());
            throw new AppException("No puedes eliminar una respuesta marcada como solución", HttpStatus.CONFLICT);
        }
    }

}
