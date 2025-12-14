package com.williammedina.forohub.domain.user.service.notifier;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotifierImpl implements UserNotifier {

    private final EmailService emailService;

    @Override
    public void notifyConfirmationEmail(UserEntity user) throws MessagingException {
        emailService.sendConfirmationEmail(user);
    }

    @Override
    public void notifyPasswordResetEmail(UserEntity user) throws MessagingException {
        emailService.sendPasswordResetEmail(user);
    }
}
