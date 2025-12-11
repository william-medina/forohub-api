package com.williammedina.forohub.domain.reply.service.permission;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface ReplyPermissionService {

    UserEntity checkCanModify(ReplyEntity reply);
    void checkElevatedPermissionsForSolution(UserEntity user, Long replyId);
    void checkCannotDeleteSolution(ReplyEntity reply);

}
