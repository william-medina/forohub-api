package com.williammedina.forohub.domain.email;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendConfirmationEmail(UserEntity user) throws MessagingException;
    void sendPasswordResetEmail(UserEntity user) throws MessagingException;
    void notifyTopicReply(TopicEntity topic, UserEntity user) throws MessagingException;
    void notifyTopicSolved(TopicEntity topic) throws MessagingException;
    void notifyTopicEdited(TopicEntity topic) throws MessagingException;
    void notifyTopicDeleted(TopicEntity topic) throws MessagingException;
    void notifyReplySolved(ReplyEntity reply, TopicEntity topic) throws MessagingException;
    void notifyReplyEdited(ReplyEntity reply) throws MessagingException;
    void notifyReplyDeleted(ReplyEntity reply) throws MessagingException;
    void notifyFollowersTopicReply(TopicEntity topic, UserEntity user) throws MessagingException;
    void notifyFollowersTopicSolved(TopicEntity topic) throws MessagingException;

}
