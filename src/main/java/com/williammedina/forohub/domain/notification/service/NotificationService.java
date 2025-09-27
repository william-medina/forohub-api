package com.williammedina.forohub.domain.notification.service;

import com.williammedina.forohub.domain.notification.dto.NotificationDTO;
import com.williammedina.forohub.domain.reply.entity.Reply;
import com.williammedina.forohub.domain.topic.entity.Topic;
import com.williammedina.forohub.domain.user.entity.User;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotificationsByUser();
    void deleteNotification(Long notifyId);
    NotificationDTO markNotificationAsRead(Long notifyId);
    void notifyTopicReply(Topic topic, User user);
    void notifyTopicSolved(Topic topic);
    void notifyTopicEdited(Topic topic);
    void notifyTopicDeleted(Topic topic);
    void notifyReplySolved(Reply reply, Topic topic);
    void notifyReplyEdited(Reply reply);
    void notifyReplyDeleted(Reply reply);
    void notifyFollowersTopicReply(Topic topic, User user);
    void notifyFollowersTopicSolved(Topic topic);

}
