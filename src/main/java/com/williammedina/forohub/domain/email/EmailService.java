package com.williammedina.forohub.domain.email;

import com.williammedina.forohub.domain.reply.entity.Reply;
import com.williammedina.forohub.domain.topic.entity.Topic;
import com.williammedina.forohub.domain.user.entity.User;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendConfirmationEmail(String to, User user) throws MessagingException;
    void sendPasswordResetEmail(String to, User user) throws MessagingException;
    void notifyTopicReply(Topic topic, User user) throws MessagingException;
    void notifyTopicSolved(Topic topic) throws MessagingException;
    void notifyTopicEdited(Topic topic) throws MessagingException;
    void notifyTopicDeleted(Topic topic) throws MessagingException;
    void notifyReplySolved(Reply reply, Topic topic) throws MessagingException;
    void notifyReplyEdited(Reply reply) throws MessagingException;
    void notifyReplyDeleted(Reply reply) throws MessagingException;
    void notifyFollowersTopicReply(Topic topic, User user) throws MessagingException;
    void notifyFollowersTopicSolved(Topic topic) throws MessagingException;

}
