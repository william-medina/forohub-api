package com.williammedina.forohub.domain.user.service.context;

import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface AuthenticatedUserProvider {

    UserEntity getAuthenticatedUser();

}
