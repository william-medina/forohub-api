package com.williammedina.forohub.domain.user.service.validator;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.enums.RequestType;

import java.time.LocalDateTime;

public interface UserValidator {

    void validateTokenExpiration(UserEntity user);
    void checkIfAccountConfirmed(UserEntity user);
    void checkIfAccountNotConfirmed(UserEntity user);
    void validatePasswordsMatch(String password, String passwordConfirmation);
    void existsByUsername(String username);
    void existsByEmail(String email);
    void validateUsernameContent(String username);
    void ensureNewUsername(UserEntity user, String newUsername);
    void validateCurrentPassword(UserEntity user, String currentPassword);
    void ensureAllowedRequestInterval(UserEntity user, RequestType type);
}
