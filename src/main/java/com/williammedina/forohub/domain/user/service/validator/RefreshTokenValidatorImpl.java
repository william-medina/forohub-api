package com.williammedina.forohub.domain.user.service.validator;

import com.williammedina.forohub.domain.user.entity.RefreshTokenEntity;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenValidatorImpl implements RefreshTokenValidator {

    @Override
    public void ensureRefreshTokenIsValid(RefreshTokenEntity refreshToken) {
        if (refreshToken.getRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            log.warn("Refresh token expired");
            throw new AppException("El refresh token expiró, inicia sesión nuevamente", HttpStatus.UNAUTHORIZED);
        }
    }
}
