package com.williammedina.forohub.domain.user.service.validator;

import com.williammedina.forohub.domain.user.entity.RefreshTokenEntity;

public interface RefreshTokenValidator {

    void checkRefreshTokenValidity(RefreshTokenEntity refreshToken);

}
