package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final EmailContentBuilder emailContentBuilder;

    @Value("${email.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer) throws MessagingException {

        String htmlContent = emailContentBuilder.buildEmailContent(title, message, buttonLabel, url, footer);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            log.error("Error sending email to: {}. Reason: {}", to, e.getMessage());
            throw new AppException("Error al enviar el email. Intenta más tarde.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
