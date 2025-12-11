package com.williammedina.forohub.domain.topic.service.permission;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface TopicPermissionService {

    UserEntity getCurrentUser();
    UserEntity checkCanModify(TopicEntity topic);

}
