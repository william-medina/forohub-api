package com.williammedina.forohub.domain.user.service.handler;

import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface UnconfirmedAccountHandler {

    void handleIfUnconfirmed(UserEntity user);

}
