package com.williammedina.forohub.domain.notification.service;

import com.williammedina.forohub.domain.notification.dto.NotificationDTO;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotificationsByUser();
    void deleteNotification(Long notifyId);
    NotificationDTO markNotificationAsRead(Long notifyId);
    void notifyTopicReply(TopicEntity topic, UserEntity user);
    void notifyTopicSolved(TopicEntity topic);
    void notifyTopicEdited(TopicEntity topic);
    void notifyTopicDeleted(TopicEntity topic);
    void notifyReplySolved(ReplyEntity reply, TopicEntity topic);
    void notifyReplyEdited(ReplyEntity reply);
    void notifyReplyDeleted(ReplyEntity reply);
    void notifyFollowersTopicReply(TopicEntity topic, UserEntity user);
    void notifyFollowersTopicSolved(TopicEntity topic);

}
