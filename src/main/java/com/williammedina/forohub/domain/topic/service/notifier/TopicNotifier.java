package com.williammedina.forohub.domain.topic.service.notifier;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;

public interface TopicNotifier {

    void notifyTopicEdited(TopicEntity topic, UserEntity editor) throws MessagingException;
    void notifyTopicDeleted(TopicEntity topic, UserEntity editor) throws MessagingException;

}
