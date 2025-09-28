package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "email.enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final Environment environment;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendConfirmationEmail(String to, UserEntity user) throws MessagingException {
        String subject = "Confirmación de cuenta";
        String url = frontendUrl + "/confirm-account/" + user.getToken();
        String title = "¡Bienvenido a Foro Hub!";
        String message = "Hola <b style='color: #03dac5;'>" + user.getUsername() + "</b>, para completar tu registro, haz clic en el siguiente enlace para confirmar tu cuenta:";
        String buttonLabel = "Confirmar Cuenta";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparing confirmation email for user ID: {}", user.getId());
        sendEmail(to, subject, title, message, buttonLabel, url, footer);
        log.info("Confirmation email sent to: {}", to);
    }

    @Override
    public void sendPasswordResetEmail(String to, UserEntity user) throws MessagingException {
        String subject = "Restablecimiento de password";
        String url = frontendUrl + "/reset-password/" + user.getToken();
        String title = "Restablecimiento de Password";
        String message = "Hola <b style='color: #03dac5;'>" + user.getUsername() + "</b>, has solicitado restablecer tu password. Haz clic en el siguiente enlace para crear un nuevo password:";
        String buttonLabel = "Restablecer Password";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparing password reset email for user ID: {}", user.getId());
        sendEmail(to, subject, title, message, buttonLabel, url, footer);
        log.info("Password reset email sent to: {}", to);
    }

    @Override
    @Async
    public void notifyTopicReply(TopicEntity topic, UserEntity user) throws MessagingException {
        String subject = "Nueva respuesta a tu tópico";
        String actionMessage = "<b style='color: #03dac5;'>" + user.getUsername() + "</b> respondió a tu tópico ";
        String topicDetails = "<b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b>.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        String htmlContent = "<p>" + actionMessage + " " + topicDetails + "</p>";

        sendEmail(topic.getUser().getEmail(), subject, subject, htmlContent, "Ver Tópico", url, footer);
    }

    @Override
    @Async
    public void notifyTopicSolved(TopicEntity topic) throws MessagingException {
        String subject = "Tu tópico ha sido marcado como solucionado";
        String actionMessage = "Tu tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Override
    @Async
    public void notifyTopicEdited(TopicEntity topic) throws MessagingException {
        String subject = "Tu tópico ha sido editado";
        String actionMessage = "Se ha realizado cambios en tu tópico titulado <b style='color: #03dac5;'>\"" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Override
    @Async
    public void notifyTopicDeleted(TopicEntity topic) throws MessagingException {
        String subject = "Tu tópico ha sido eliminado";
        String actionMessage = "Lamentamos informarte que tu tópico titulado <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido eliminado. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Override
    @Async
    public void notifyReplySolved(ReplyEntity reply, TopicEntity topic) throws MessagingException {
        String subject = "Tu respuesta ha sido marcada como solución";
        String actionMessage = "Tu respuesta en el tópico <b style='color: #03dac5;'>" + reply.getTopic().getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido marcada como solución.";

        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(reply.getUser().getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Override
    @Async
    public void notifyReplyEdited(ReplyEntity reply) throws MessagingException {
        String subject = "Tu respuesta ha sido editada";
        String actionMessage = "Se ha realizado cambios en tu respuesta del tópico <b style='color: #03dac5;'>\"" + reply.getTopic().getTitle() + "</b> del curso: <b>" + reply.getTopic().getCourse().getName() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + reply.getTopic().getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(reply.getUser().getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Override
    @Async
    public void notifyReplyDeleted(ReplyEntity reply) throws MessagingException {
        String subject = "Tu respuesta ha sido eliminada";
        String actionMessage = "Lamentamos informarte que tu respuesta del tópico <b style='color: #03dac5;'>" + reply.getTopic().getTitle() + "</b> del curso: <b>" + reply.getTopic().getCourse().getName() + "</b> ha sido eliminada. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(reply.getUser().getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Override
    @Async
    public void notifyFollowersTopicReply(TopicEntity topic, UserEntity user) throws MessagingException {
        String subject = "Nueva respuesta en un tópico que sigues";
        String actionMessage = "Se ha añadido una nueva respuesta al tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> que sigues.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        for (TopicFollowEntity follower : topic.getFollowedTopics()) {
            if (!follower.getUser().getUsername().equals(user.getUsername())) {
                sendEmail(follower.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
            }
        }
    }

    @Override
    @Async
    public void notifyFollowersTopicSolved(TopicEntity topic) throws MessagingException {
        String subject = "Un tópico que sigues ha sido marcado como solucionado";
        String actionMessage = "El tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> que sigues ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        for (TopicFollowEntity follower : topic.getFollowedTopics()) {
            sendEmail(follower.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
        }
    }

    public void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer) throws MessagingException {

        String htmlContent = buildEmailContent(title, message, buttonLabel, url, footer);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String emailFrom = environment.getProperty("EMAIL_FROM");

        helper.setFrom(emailFrom);
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

    private String buildEmailContent(String title, String message, String buttonLabel, String url, String footer) {
        String template = loadTemplate("email_template.html")
                .replace("{TITLE}", title)
                .replace("{MESSAGE}", message)
                .replace("{FOOTER}", footer);

        // Check if buttonLabel and url are null
        String buttonSection = (buttonLabel != null && url != null)
                ? "<a href=\"" + url + "\" style=\"display: inline-block; background-color: #03dac5; color: #121212; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; max-width: 100%; text-align: center;\">"
                + buttonLabel + "</a>"
                : "";

        return template.replace("{BUTTON_SECTION}", buttonSection);
    }

    // Loads a template file from the file system
    private String loadTemplate(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + fileName)) {
            if (inputStream == null) {
                log.error("Email template not found: {}", fileName);
                throw new RuntimeException("No se pudo encontrar la plantilla de email: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            log.error("Error loading email template: {}. Details: {}", fileName, e.getMessage());
            throw new RuntimeException("Error al cargar la plantilla de email: " + fileName, e);
        }
    }

}