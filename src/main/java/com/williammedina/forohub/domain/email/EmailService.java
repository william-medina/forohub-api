package com.williammedina.forohub.domain.email;

import com.williammedina.forohub.domain.response.entity.Response;
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
    void notifyResponseSolved(Response response, Topic topic) throws MessagingException;
    void notifyResponseEdited(Response response) throws MessagingException;
    void notifyResponseDeleted(Response response) throws MessagingException;
    void notifyFollowersTopicReply(Topic topic, User user) throws MessagingException;
    void notifyFollowersTopicSolved(Topic topic) throws MessagingException;

}
