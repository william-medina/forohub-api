package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.response.entity.Response;
import com.williammedina.forohub.domain.topic.entity.Topic;
import com.williammedina.forohub.domain.user.entity.User;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "email.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledEmailService implements EmailService {

    @Override
    public void sendConfirmationEmail(String to, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Confirmación de cuenta para usuario: {} ({})", user.getUsername(), to);
    }

    @Override
    public void sendPasswordResetEmail(String to, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Restablecimiento de password solicitado por usuario: {} ({})", user.getUsername(), to);
    }

    @Override
    public void notifyTopicReply(Topic topic, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de nueva respuesta en tópico '{}' por usuario: {}", topic.getTitle(), user.getUsername());
    }

    @Override
    public void notifyTopicSolved(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de tópico solucionado: {}", topic.getTitle());
    }

    @Override
    public void notifyTopicEdited(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de tópico editado: {}", topic.getTitle());
    }

    @Override
    public void notifyTopicDeleted(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de tópico eliminado: {}", topic.getTitle());
    }

    @Override
    public void notifyResponseSolved(Response response, Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de respuesta marcada como solución en tópico '{}'", topic.getTitle());
    }

    @Override
    public void notifyResponseEdited(Response response) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de respuesta editada en tópico '{}'", response.getTopic().getTitle());
    }

    @Override
    public void notifyResponseDeleted(Response response) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación de respuesta eliminada en tópico '{}'", response.getTopic().getTitle());
    }

    @Override
    public void notifyFollowersTopicReply(Topic topic, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación a seguidores de nueva respuesta en tópico '{}' por usuario {}", topic.getTitle(), user.getUsername());
    }

    @Override
    public void notifyFollowersTopicSolved(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notificación a seguidores de tópico solucionado '{}'", topic.getTitle());
    }
}
