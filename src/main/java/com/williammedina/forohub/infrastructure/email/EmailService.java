package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topicfollow.TopicFollow;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Environment environment;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    public void sendConfirmationEmail(String to, User user) throws MessagingException {
        String subject = "Confirmación de cuenta";
        String url = frontendUrl + "/confirm-account/" + user.getToken();
        String title = "¡Bienvenido a Foro Hub!";
        String message = "Hola <b style='color: #03dac5;'>" + user.getUsername() + "</b>, para completar tu registro, haz clic en el siguiente enlace para confirmar tu cuenta:";
        String buttonLabel = "Confirmar Cuenta";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparando email de confirmación para usuario ID: {}", user.getId());
        sendEmail(to, subject, title, message, buttonLabel, url, footer);
        log.info("Email de confirmación enviado a: {}", to);
    }

    public void sendPasswordResetEmail(String to, User user) throws MessagingException {
        String subject = "Restablecimiento de password";
        String url = frontendUrl + "/reset-password/" + user.getToken();
        String title = "Restablecimiento de Password";
        String message = "Hola <b style='color: #03dac5;'>" + user.getUsername() + "</b>, has solicitado restablecer tu password. Haz clic en el siguiente enlace para crear un nuevo password:";
        String buttonLabel = "Restablecer Password";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparando email de restablecimiento de password para usuario ID: {}", user.getId());
        sendEmail(to, subject, title, message, buttonLabel, url, footer);
        log.info("Email de restablecimiento de password enviado a: {}", to);
    }

    @Async
    public void notifyTopicReply(Topic topic, User user) throws MessagingException {
        String subject = "Nueva respuesta a tu tópico";
        String actionMessage = "<b style='color: #03dac5;'>" + user.getUsername() + "</b> respondió a tu tópico ";
        String topicDetails = "<b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b>.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        String htmlContent = "<p>" + actionMessage + " " + topicDetails + "</p>";

        sendEmail(topic.getUser().getEmail(), subject, subject, htmlContent, "Ver Tópico", url, footer);
    }

    @Async
    public void notifyTopicSolved(Topic topic) throws MessagingException {
        String subject = "Tu tópico ha sido marcado como solucionado";
        String actionMessage = "Tu tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Async
    public void notifyTopicEdited(Topic topic) throws MessagingException {
        String subject = "Tu tópico ha sido editado";
        String actionMessage = "Se ha realizado cambios en tu tópico titulado <b style='color: #03dac5;'>\"" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Async
    public void notifyTopicDeleted(Topic topic) throws MessagingException {
        String subject = "Tu tópico ha sido eliminado";
        String actionMessage = "Lamentamos informarte que tu tópico titulado <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido eliminado. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Async
    public void notifyResponseSolved(Response response, Topic topic) throws MessagingException {
        String subject = "Tu respuesta ha sido marcada como solución";
        String actionMessage = "Tu respuesta en el tópico <b style='color: #03dac5;'>" + response.getTopic().getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido marcada como solución.";

        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(response.getUser().getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Async
    public void notifyResponseEdited(Response response) throws MessagingException {
        String subject = "Tu respuesta ha sido editada";
        String actionMessage = "Se ha realizado cambios en tu respuesta del tópico <b style='color: #03dac5;'>\"" + response.getTopic().getTitle() + "</b> del curso: <b>" + response.getTopic().getCourse().getName() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + response.getTopic().getId();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(response.getUser().getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Async
    public void notifyResponseDeleted(Response response) throws MessagingException {
        String subject = "Tu respuesta ha sido eliminada";
        String actionMessage = "Lamentamos informarte que tu respuesta del tópico <b style='color: #03dac5;'>" + response.getTopic().getTitle() + "</b> del curso: <b>" + response.getTopic().getCourse().getName() + "</b> ha sido eliminada. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(response.getUser().getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Async
    public void notifyFollowersTopicReply(Topic topic, User user) throws MessagingException {
        String subject = "Nueva respuesta en un tópico que sigues";
        String actionMessage = "Se ha añadido una nueva respuesta al tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> que sigues.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        for (TopicFollow follower : topic.getFollowedTopics()) {
            if (!follower.getUser().getUsername().equals(user.getUsername())) {
                sendEmail(follower.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
            }
        }
    }

    @Async
    public void notifyFollowersTopicSolved(Topic topic) throws MessagingException {
        String subject = "Un tópico que sigues ha sido marcado como solucionado";
        String actionMessage = "El tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> que sigues ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        for (TopicFollow follower : topic.getFollowedTopics()) {
            sendEmail(follower.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
        }
    }

    public void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer) throws MessagingException {

        if (Arrays.toString(environment.getActiveProfiles()).contains("test")) {
            log.warn("Modo test activo - Email preparado para: {}, asunto: {}. No se enviará.", to, subject);
            return;
        }

        if (!isEmailEnabled()) {
            log.warn("Envío de emails deshabilitado. Email a: {}, asunto: {} NO será enviado.", to, subject);
            return;
        }

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
            log.error("Error al enviar email a: {}. Motivo: {}", to, e.getMessage());
            throw new AppException("Error al enviar el email. Intenta más tarde.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private String buildEmailContent(String title, String message, String buttonLabel, String url, String footer) {
        String template = loadTemplate("email_template.html")
                .replace("{TITLE}", title)
                .replace("{MESSAGE}", message)
                .replace("{FOOTER}", footer);

        // Verificar si buttonLabel y url son nulos
        String buttonSection = (buttonLabel != null && url != null)
                ? "<a href=\"" + url + "\" style=\"display: inline-block; background-color: #03dac5; color: #121212; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; max-width: 100%; text-align: center;\">"
                + buttonLabel + "</a>"
                : "";

        return template.replace("{BUTTON_SECTION}", buttonSection);
    }

    // Carga un archivo de plantilla desde el sistema de archivos.
    private String loadTemplate(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + fileName)) {
            if (inputStream == null) {
                log.error("Plantilla de email no encontrada: {}", fileName);
                throw new RuntimeException("No se pudo encontrar la plantilla de email: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            log.error("Error al cargar plantilla de email: {}. Detalle: {}", fileName, e.getMessage());
            throw new RuntimeException("Error al cargar la plantilla de email: " + fileName, e);
        }
    }


    private boolean isEmailEnabled() {
        String emailEnabled = environment.getProperty("email.enabled", "false");
        return Boolean.parseBoolean(emailEnabled);
    }

}