package com.williammedina.forohub.domain.user.service.finder;

import com.williammedina.forohub.domain.user.entity.RefreshTokenEntity;
import com.williammedina.forohub.domain.user.repository.RefreshTokenRepository;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenFinderImpl implements RefreshTokenFinder {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshTokenEntity findToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found");
                    return new AppException("Refresh token no encontrado", HttpStatus.UNAUTHORIZED);
                });
    }
}
