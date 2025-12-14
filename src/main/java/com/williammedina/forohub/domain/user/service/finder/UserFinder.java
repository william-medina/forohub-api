package com.williammedina.forohub.domain.user.service.finder;

import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface UserFinder {

    UserEntity findUserById(Long userId);
    UserEntity findUserByEmail(String email);
    UserEntity findUserByValidToken(String token);

}
