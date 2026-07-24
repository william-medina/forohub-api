package com.williammedina.forohub.infrastructure.email.disabled;

import com.williammedina.forohub.infrastructure.email.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "disabled", matchIfMissing = true)
public class DisabledEmailSender implements EmailSender {

    @Override
    public void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer) {
        log.info("[DISABLED EMAIL] Subject: '{}', To: '{}'", subject, to);
    }
}
