package com.williammedina.forohub.infrastructure.email;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "email.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledEmailService implements EmailService {

    @Override
    public void sendConfirmationEmail(String to, UserEntity user) throws MessagingException {
        log.info("[DISABLED EMAIL] Account confirmation for user: {} ({})", user.getUsername(), to);
    }

    @Override
    public void sendPasswordResetEmail(String to, UserEntity user) throws MessagingException {
        log.info("[DISABLED EMAIL] Password reset requested by user: {} ({})", user.getUsername(), to);
    }

    @Override
    public void notifyTopicReply(TopicEntity topic, UserEntity user) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of new reply in topic '{}' by user: {}", topic.getTitle(), user.getUsername());
    }

    @Override
    public void notifyTopicSolved(TopicEntity topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of topic marked as solved: {}", topic.getTitle());
    }

    @Override
    public void notifyTopicEdited(TopicEntity topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of topic edited: {}", topic.getTitle());
    }

    @Override
    public void notifyTopicDeleted(TopicEntity topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of topic deleted: {}", topic.getTitle());
    }

    @Override
    public void notifyReplySolved(ReplyEntity reply, TopicEntity topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of reply marked as solution in topic '{}'", topic.getTitle());
    }

    @Override
    public void notifyReplyEdited(ReplyEntity reply) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of reply edited in topic '{}'", reply.getTopic().getTitle());
    }

    @Override
    public void notifyReplyDeleted(ReplyEntity reply) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification of reply deleted in topic '{}'", reply.getTopic().getTitle());
    }

    @Override
    public void notifyFollowersTopicReply(TopicEntity topic, UserEntity user) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification to followers of new reply in topic '{}' by user {}", topic.getTitle(), user.getUsername());
    }

    @Override
    public void notifyFollowersTopicSolved(TopicEntity topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Notification to followers of topic marked as solved '{}'", topic.getTitle());
    }
}
