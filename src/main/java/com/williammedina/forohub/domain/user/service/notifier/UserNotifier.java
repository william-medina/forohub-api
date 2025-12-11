package com.williammedina.forohub.domain.user.service.notifier;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;

public interface UserNotifier {

    void notifyConfirmationEmail(UserEntity user) throws MessagingException;
    void notifyPasswordResetEmail(UserEntity user) throws MessagingException;

}
