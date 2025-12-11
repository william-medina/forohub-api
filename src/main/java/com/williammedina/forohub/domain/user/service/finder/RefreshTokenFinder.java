package com.williammedina.forohub.domain.user.service.finder;

import com.williammedina.forohub.domain.user.entity.RefreshTokenEntity;

public interface RefreshTokenFinder {

    RefreshTokenEntity findToken(String token);

}
