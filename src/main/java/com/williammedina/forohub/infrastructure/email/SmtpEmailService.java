package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "email.enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private final EmailSenderService emailSenderService;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendConfirmationEmail(UserEntity user) throws MessagingException {
        String subject = "Confirmación de cuenta";
        String url = frontendUrl + "/confirm-account/" + user.getToken();
        String title = "¡Bienvenido a Foro Hub!";
        String message = "Hola <b style='color: #03dac5;'>" + user.getUsername() + "</b>, para completar tu registro, haz clic en el siguiente enlace para confirmar tu cuenta:";
        String buttonLabel = "Confirmar Cuenta";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparing confirmation email for user ID: {}", user.getId());
        emailSenderService.sendEmail(user.getEmail(), subject, title, message, buttonLabel, url, footer);
        log.info("Confirmation email sent to: {}", user.getEmail());
    }

    @Override
    public void sendPasswordResetEmail(UserEntity user) throws MessagingException {
        String subject = "Restablecimiento de password";
        String url = frontendUrl + "/reset-password/" + user.getToken();
        String title = "Restablecimiento de Password";
        String message = "Hola <b style='color: #03dac5;'>" + user.getUsername() + "</b>, has solicitado restablecer tu password. Haz clic en el siguiente enlace para crear un nuevo password:";
        String buttonLabel = "Restablecer Password";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparing password reset email for user ID: {}", user.getId());
        emailSenderService.sendEmail(user.getEmail(), subject, title, message, buttonLabel, url, footer);
        log.info("Password reset email sent to: {}", user.getEmail());
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

        emailSenderService.sendEmail(topic.getUser().getEmail(), subject, subject, htmlContent, "Ver Tópico", url, footer);
    }

    @Override
    @Async
    public void notifyTopicSolved(TopicEntity topic) throws MessagingException {
        String subject = "Tu tópico ha sido marcado como solucionado";
        String actionMessage = "Tu tópico <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        emailSenderService.sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Override
    @Async
    public void notifyTopicEdited(TopicEntity topic) throws MessagingException {
        String subject = "Tu tópico ha sido editado";
        String actionMessage = "Se ha realizado cambios en tu tópico titulado <b style='color: #03dac5;'>\"" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        emailSenderService.sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Override
    @Async
    public void notifyTopicDeleted(TopicEntity topic) throws MessagingException {
        String subject = "Tu tópico ha sido eliminado";
        String actionMessage = "Lamentamos informarte que tu tópico titulado <b style='color: #03dac5;'>" + topic.getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido eliminado. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        emailSenderService.sendEmail(topic.getUser().getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Override
    @Async
    public void notifyReplySolved(ReplyEntity reply, TopicEntity topic) throws MessagingException {
        String subject = "Tu respuesta ha sido marcada como solución";
        String actionMessage = "Tu respuesta en el tópico <b style='color: #03dac5;'>" + reply.getTopic().getTitle() + "</b> del curso: <b>" + topic.getCourse().getName() + "</b> ha sido marcada como solución.";

        String url = frontendUrl + "/topic/" + topic.getId();
        String footer = "Gracias por ser parte de ForoHub.";

        emailSenderService.sendEmail(reply.getUser().getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Override
    @Async
    public void notifyReplyEdited(ReplyEntity reply) throws MessagingException {
        String subject = "Tu respuesta ha sido editada";
        String actionMessage = "Se ha realizado cambios en tu respuesta del tópico <b style='color: #03dac5;'>\"" + reply.getTopic().getTitle() + "</b> del curso: <b>" + reply.getTopic().getCourse().getName() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + reply.getTopic().getId();
        String footer = "Gracias por ser parte de ForoHub.";

        emailSenderService.sendEmail(reply.getUser().getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Override
    @Async
    public void notifyReplyDeleted(ReplyEntity reply) throws MessagingException {
        String subject = "Tu respuesta ha sido eliminada";
        String actionMessage = "Lamentamos informarte que tu respuesta del tópico <b style='color: #03dac5;'>" + reply.getTopic().getTitle() + "</b> del curso: <b>" + reply.getTopic().getCourse().getName() + "</b> ha sido eliminada. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        emailSenderService.sendEmail(reply.getUser().getEmail(), subject, subject, actionMessage, null, null, footer);
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
                emailSenderService.sendEmail(follower.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
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
            emailSenderService.sendEmail(follower.getUser().getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
        }
    }

}