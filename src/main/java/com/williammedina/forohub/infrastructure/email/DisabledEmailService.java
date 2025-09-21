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
        log.info("[DISABLED EMAIL] Account confirmation for user: {} ({})", user.getUsername(), to);
    }

    @Override
    public void sendPasswordResetEmail(String to, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Password reset requested by user: {} ({})", user.getUsername(), to);
    }

    @Override
    public void notifyTopicReply(Topic topic, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of new reply in topic '{}' by user: {}", topic.getTitle(), user.getUsername());
    }

    @Override
    public void notifyTopicSolved(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of topic marked as solved: {}", topic.getTitle());
    }

    @Override
    public void notifyTopicEdited(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of topic edited: {}", topic.getTitle());
    }

    @Override
    public void notifyTopicDeleted(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of topic deleted: {}", topic.getTitle());
    }

    @Override
    public void notifyResponseSolved(Response response, Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of response marked as solution in topic '{}'", topic.getTitle());
    }

    @Override
    public void notifyResponseEdited(Response response) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of response edited in topic '{}'", response.getTopic().getTitle());
    }

    @Override
    public void notifyResponseDeleted(Response response) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of response deleted in topic '{}'", response.getTopic().getTitle());
    }

    @Override
    public void notifyFollowersTopicReply(Topic topic, User user) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification to followers of new reply in topic '{}' by user {}", topic.getTitle(), user.getUsername());
    }

    @Override
    public void notifyFollowersTopicSolved(Topic topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification to followers of topic marked as solved '{}'", topic.getTitle());
    }
}
