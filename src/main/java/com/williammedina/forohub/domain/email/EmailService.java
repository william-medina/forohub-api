package com.williammedina.forohub.domain.email;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface EmailService {

    void sendConfirmationEmail(UserEntity user);
    void sendPasswordResetEmail(UserEntity user);
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
