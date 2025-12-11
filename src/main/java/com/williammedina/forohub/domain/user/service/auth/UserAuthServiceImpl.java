package com.williammedina.forohub.domain.user.service.auth;

import com.williammedina.forohub.domain.user.entity.RefreshTokenEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.dto.*;
import com.williammedina.forohub.domain.user.repository.RefreshTokenRepository;
import com.williammedina.forohub.domain.user.service.AuthenticatedUserProvider;
import com.williammedina.forohub.domain.user.service.finder.RefreshTokenFinder;
import com.williammedina.forohub.domain.user.service.handler.UnconfirmedAccountHandler;
import com.williammedina.forohub.domain.user.service.transaction.UserTransactionService;
import com.williammedina.forohub.domain.user.service.validator.RefreshTokenValidator;
import com.williammedina.forohub.infrastructure.exception.*;
import com.williammedina.forohub.infrastructure.response.MessageResponse;
import com.williammedina.forohub.infrastructure.security.CookieService;
import com.williammedina.forohub.infrastructure.security.JwtTokenResponse;
import com.williammedina.forohub.infrastructure.security.TokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserTransactionService userTransactionService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
    private final RefreshTokenFinder refreshTokenFinder;
    private final RefreshTokenValidator validator;
    private final UnconfirmedAccountHandler unconfirmedAccountHandler;


    @Override
    @Transactional
    public JwtTokenResponse authenticateAndGenerateToken(LoginUserDTO request, HttpServletResponse response) throws MessagingException {
        log.info("Attempting to authenticate user: {}", request.username());

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authenticationToken);

        UserEntity user = (UserEntity) authenticatedUser.getPrincipal();
        unconfirmedAccountHandler.handleIfUnconfirmed(user);

        // Generate authentication tokens (access and refresh)
        String accessToken = tokenService.generateAccessToken(user);
        RefreshTokenEntity refreshToken = tokenService.createRefreshToken(user);

        refreshTokenRepository.save(refreshToken);
        response.addCookie(cookieService.createRefreshCookie(refreshToken.getToken()));
        log.info("User successfully authenticated - ID: {}", user.getId());

        return new JwtTokenResponse(accessToken);
    }

    @Override
    @Transactional
    public JwtTokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Requesting refresh token");

        String token = cookieService.extractRefreshToken(request);
        RefreshTokenEntity refreshToken = refreshTokenFinder.findToken(token);
        validator.checkRefreshTokenValidity(refreshToken);

        String newAccessToken = tokenService.generateAccessToken(refreshToken.getUser());
        log.info("New access token generated for user ID: {}", refreshToken.getUser().getId());
        return new JwtTokenResponse(newAccessToken);
    }

    @Override
    @Transactional
    public MessageResponse logout(HttpServletRequest request, HttpServletResponse response) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Logging out user ID: {}", currentUser.getId());

        Optional<String> token = cookieService.getTokenFromCookies(request, "refresh_token");

        token.flatMap(refreshTokenRepository::findByToken).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            log.info("Refresh token revoked for user ID: {}", currentUser.getId());
        });

        response.addCookie(cookieService.deleteRefreshCookie());
        return new MessageResponse("Sesión cerrada exitosamente.");
    }


}