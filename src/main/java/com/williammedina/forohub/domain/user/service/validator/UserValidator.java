package com.williammedina.forohub.domain.user.service.validator;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.enums.RequestType;

public interface UserValidator {

    void ensureTokenIsNotExpired(UserEntity user);
    void ensureAccountIsNotConfirmed(UserEntity user);
    void ensureAccountIsConfirmed(UserEntity user);
    void ensurePasswordsMatch(String password, String confirmation);
    void ensureUsernameIsUnique(String username);
    void ensureEmailIsUnique(String email);
    void ensureUsernameContentIsValid(String username);
    void ensureUsernameIsDifferent(UserEntity user, String newUsername);
    void ensureCurrentPasswordIsValid(UserEntity user, String currentPassword);
    void ensureRequestIntervalIsAllowed(UserEntity user, RequestType type);

}
