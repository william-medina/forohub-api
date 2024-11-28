package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topicfollow.TopicFollow;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.errors.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private  final Environment environment;

    @Value("${frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    public void sendConfirmationEmail(String to, String token) throws MessagingException {
        String subject = "Confirmación de cuenta";
        String url = frontendUrl + "/confirm-account/" + token;
        String title = "¡Bienvenido a Foro Hub!";
        String message = "Para completar tu registro, haz clic en el siguiente enlace para confirmar tu cuenta:";
        String buttonLabel = "Confirmar Cuenta";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        sendEmail(to, subject, title, message, buttonLabel, url, footer);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        String subject = "Restablecimiento de password";
        String url = frontendUrl + "/reset-password/" + token;
        String title = "Restablecimiento de Password";
        String message = "Has solicitado restablecer tu password. Haz clic en el siguiente enlace para crear un nuevo password:";
        String buttonLabel = "Restablecer Password";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        sendEmail(to, subject, title, message, buttonLabel, url, footer);
    }

    @Async
    public void notifyTopicReply(Topic topic, User user) throws MessagingException {
        String subject = "Nueva respuesta a tu tópico";
        String actionMessage = "Tu tópico ha recibido una nueva respuesta.";
        String highlightedMessage = "<span style='color: #03dac5;'>" + user.getUsername() + "</span> respondió al tópico ";
        String topicDetails = "<b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b>";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        String htmlContent = "<p>" + actionMessage + "</p>" + "<p>" + highlightedMessage + " " + topicDetails + "</p>";

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
        String subject = "Tu respuesta ha sido eliminado";
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
            System.out.println("Modo de test activado: El email ha sido procesado para " + to + ", pero no se enviará realmente en este entorno.");
            return;
        }

        if (!isEmailEnabled()) {
            System.out.println("El email ha sido preparado para ser enviado a " + to + ", pero el envío de emails está actualmente deshabilitado.");
            return;
        }

        String htmlContent = buildEmailContent(title, message, buttonLabel, url, footer);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String emailFrom = environment.getProperty("EMAIL_FROM");

        helper.setFrom("william@hotmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            System.out.println(e.getMessage());
            throw new AppException("Error al enviar el email. Intenta más tarde.", "SERVICE_UNAVAILABLE");
        }
    }

    private String buildEmailContent(String title, String message, String buttonLabel, String url, String footer) {
        StringBuilder emailContent = new StringBuilder("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { ")
                .append("  font-family: Arial, sans-serif; ")
                .append("  margin: 0; ")
                .append("  padding: 0; ")
                .append("  color: #e0e0e0; ")
                .append("  background-color: #121212; ")
                .append("  display: flex; ")
                .append("  justify-content: center; ")
                .append("  align-items: center; ")
                .append("  height: 100vh; ")
                .append("}")
                .append(".container { ")
                .append("  max-width: 600px; ")
                .append("  margin: 0 auto; ")
                .append("  padding: 20px; ")
                .append("  background-color: #1f1f1f; ")
                .append("  border-radius: 10px; ")
                .append("  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2); ")
                .append("}")
                .append(".header { text-align: center; color: #03dac5; }")
                .append(".footer { text-align: center; font-size: 12px; color: #b0b0b0; margin-top: 20px; }")
                .append(".button-container { text-align: center; margin: 20px 0; }")
                .append(".button { ")
                .append("  background-color: #03dac5; ")
                .append("  color: #121212; ")
                .append("  padding: 10px 20px; ")
                .append("  text-decoration: none; ")
                .append("  border-radius: 5px; ")
                .append("  font-weight: bold; ")
                .append("  display: inline-block; ")
                .append("}")
                .append("a.button:hover { background-color: #018786; }")
                .append("p { color: #e0e0e0; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<div class='header'>")
                .append("<h2>").append(title).append("</h2>")
                .append("</div>")
                .append("<p>").append(message).append("</p>");

        // Condicional para mostrar el botón solo si buttonLabel y url no son null
        if (buttonLabel != null && url != null) {
            emailContent.append("<div class='button-container'>")
                    .append("<a href='").append(url).append("' class='button'>").append(buttonLabel).append("</a>")
                    .append("</div>");
        }

        emailContent.append("<div class='footer'>")
                .append("<p>").append(footer).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return emailContent.toString();
    }

    private boolean isEmailEnabled() {
        String emailEnabled = environment.getProperty("email.enabled", "false");
        return Boolean.parseBoolean(emailEnabled);
    }

}