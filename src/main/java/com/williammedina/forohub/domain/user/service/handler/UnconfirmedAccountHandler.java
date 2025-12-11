package com.williammedina.forohub.domain.user.service.handler;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;

public interface UnconfirmedAccountHandler {

    void handleIfUnconfirmed(UserEntity user) throws MessagingException;

}
