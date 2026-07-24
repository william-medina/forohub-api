package com.williammedina.forohub.domain.user.service.notifier;

import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface UserNotifier {

    void notifyConfirmationEmail(UserEntity user);
    void notifyPasswordResetEmail(UserEntity user);

}
