package com.williammedina.forohub.domain.reply.service.notifier;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.notification.service.NotificationService;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyNotifierImpl implements ReplyNotifier {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public void notifyNewReply(TopicEntity topic, UserEntity editor) throws MessagingException {
        if(!editor.equals(topic.getUser())) {
            log.debug("Notifying topic owner ID: {} about new reply", topic.getId());
            notificationService.notifyTopicReply(topic, editor);
            emailService.notifyTopicReply(topic, editor);
        }

        log.debug("Notifying followers of topic ID: {}", topic.getId());
        notificationService.notifyFollowersTopicReply(topic, editor);
        emailService.notifyFollowersTopicReply(topic, editor);
    }

    @Override
    public void notifyReplyUpdated(ReplyEntity reply, UserEntity editor) throws MessagingException {
        if(!editor.equals(reply.getUser())) {
            log.debug("Notifying reply owner ID: {}", reply.getId());
            notificationService.notifyReplyEdited(reply);
            emailService.notifyReplyEdited(reply);
        }
    }

    @Override
    public void notifyReplyDeleted(ReplyEntity reply, UserEntity editor) throws MessagingException {
        if(!editor.equals(reply.getUser())) {
            log.debug("Notifying reply owner ID: {} about deletion", reply.getId());
            notificationService.notifyReplyDeleted(reply);
            emailService.notifyReplyDeleted(reply);
        }
    }

    @Override
    public void notifyReplySolution(ReplyEntity reply) throws MessagingException {
        if(reply.getSolution()) {
            log.debug("Sending notifications for reply solution");
            notificationService.notifyTopicSolved(reply.getTopic());
            notificationService.notifyReplySolved(reply, reply.getTopic());
            notificationService.notifyFollowersTopicSolved(reply.getTopic());
            emailService.notifyTopicSolved(reply.getTopic());
            emailService.notifyReplySolved(reply, reply.getTopic());
            emailService.notifyFollowersTopicSolved(reply.getTopic());
        }
    }

}
