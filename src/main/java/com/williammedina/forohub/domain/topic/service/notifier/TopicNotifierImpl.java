package com.williammedina.forohub.domain.topic.service.notifier;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.notification.service.NotificationService;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicNotifierImpl implements TopicNotifier {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public void notifyTopicEdited(TopicEntity topic, UserEntity editor) throws MessagingException {
        if(!editor.equals(topic.getUser())) {
            log.debug("Notifying topic owner ID: {} about update", topic.getId());
            notificationService.notifyTopicEdited(topic);
            emailService.notifyTopicEdited(topic);
        }
    }

    @Override
    public void notifyTopicDeleted(TopicEntity topic, UserEntity editor) throws MessagingException {
        if(!editor.equals(topic.getUser())) {
            log.debug("Notifying topic owner ID: {} about deletion", topic.getId());
            notificationService.notifyTopicDeleted(topic);
            emailService.notifyTopicDeleted(topic);
        }
    }
}
