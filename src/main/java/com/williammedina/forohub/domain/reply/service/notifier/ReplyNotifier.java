package com.williammedina.forohub.domain.reply.service.notifier;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;

public interface ReplyNotifier {

    void notifyNewReply(TopicEntity topic, UserEntity editor) throws MessagingException;
    void notifyReplyUpdated(ReplyEntity reply, UserEntity editor) throws MessagingException;
    void notifyReplyDeleted(ReplyEntity reply, UserEntity editor) throws MessagingException;
    void notifyReplySolution(ReplyEntity reply) throws MessagingException;

}
