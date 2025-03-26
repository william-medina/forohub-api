package com.williammedina.forohub.domain.user;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.common.ContentValidationService;
import com.williammedina.forohub.domain.response.ResponseRepository;
import com.williammedina.forohub.domain.topic.TopicRepository;
import com.williammedina.forohub.domain.topicfollow.TopicFollowRepository;
import com.williammedina.forohub.domain.user.dto.*;
import com.williammedina.forohub.infrastructure.email.EmailService;
import com.williammedina.forohub.infrastructure.errors.*;
import com.williammedina.forohub.infrastructure.security.JwtTokenResponse;
import com.williammedina.forohub.infrastructure.security.SecurityFilter;
import com.williammedina.forohub.infrastructure.security.TokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CommonHelperService commonHelperService;
    private final TopicRepository topicRepository;
    private final ResponseRepository responseRepository;
    private final TopicFollowRepository topicFollowRepository;
    private final ContentValidationService contentValidationService;
    private final SecurityFilter securityFilter;

    @Transactional
    public JwtTokenResponse authenticateAndGenerateToken(LoginUserDTO data, HttpServletResponse response) throws MessagingException {

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authenticationToken);

        User user = (User) authenticatedUser.getPrincipal();

        if (!user.isAccountConfirmed()) {
            handleAccountDisabled(data.username());
            throw new AppException("La cuenta no está confirmada. Por favor, verifique su email.", "ACCOUNT_NOT_CONFIRMED");
        }

        // Generar tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        response.addCookie(createCookie("access_token", accessToken, "/", TokenService.ACCESS_TOKEN_EXPIRATION));
        response.addCookie(createCookie("refresh_token", refreshToken, "/api/auth/refresh-token", TokenService.REFRESH_TOKEN_EXPIRATION));

        return new JwtTokenResponse("Autenticación exitosa.");
    }

    @Transactional
    public UserDTO createAccount(CreateUserDTO data) throws MessagingException {
        validatePasswordsMatch(data.password(), data.password_confirmation());
        existsByUsername(data.username());
        existsByEmail(data.email());

        validateUsernameContent(data.username()); // Validar el username con IA

        User user = new User(data.username(), data.email().trim().toLowerCase(), passwordEncoder.encode(data.password()));
        User userCreated = userRepository.save(user);

        emailService.sendConfirmationEmail(userCreated.getEmail(), userCreated.getUsername(), userCreated.getToken());
        return toUserDTO(userCreated);
    }

    @Transactional
    public UserDTO confirmAccount(String token) {
        User user = findUserByToken(token);
        validateTokenExpiration(user);
        checkIfAccountConfirmed(user);

        user.setAccountConfirmed(true);
        user.clearTokenData();
        userRepository.save(user);

        return toUserDTO(user);
    }

    @Transactional
    public UserDTO requestConfirmationCode(EmailUserDTO data) throws MessagingException {
        User user = findUserByEmail(data.email());
        checkIfAccountConfirmed(user);

        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            throw new AppException("Debe esperar 2 minutos para solicitar otro código de confirmación.", "BAD_REQUEST");
        }

        user.generateConfirmationToken();
        emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), user.getToken());
        return toUserDTO(user);
    }

    @Transactional
    public UserDTO forgotPassword(EmailUserDTO data) throws MessagingException {
        User user = findUserByEmail(data.email());
        checkIfAccountNotConfirmed(user);

        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            throw new AppException("Debe esperar 2 minutos para solicitar otro código de restablecimiento de password.", "BAD_REQUEST");
        }

        user.generateConfirmationToken();
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), user.getToken());
        return toUserDTO(user);
    }

    @Transactional
    public UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO data) {
        validatePasswordsMatch(data.password(), data.password_confirmation());

        User user = findUserByToken(token);
        validateTokenExpiration(user);
        checkIfAccountNotConfirmed(user);

        user.setPassword(passwordEncoder.encode(data.password()));
        user.clearTokenData();
        userRepository.save(user);

        return toUserDTO(user);
    }

    @Transactional
    public UserDTO updateCurrentUserPassword(UpdateCurrentUserPasswordDTO data) {
        validatePasswordsMatch(data.password(), data.password_confirmation());

        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(data.current_password(), user.getPassword())) {
            throw new AppException("El password actual es incorrecto.", "UNAUTHORIZED");
        }

        user.setPassword(passwordEncoder.encode(data.password()));
        userRepository.save(user);

        return toUserDTO(user);
    }

    @Transactional
    public UserDTO updateUsername(UpdateUsernameDTO data) {
        User user = getAuthenticatedUser();

        if (user.getUsername().equals(data.username())) {
            throw new AppException("Debes ingresa un nuevo nombre.", "BAD_REQUEST");
        }

        validateUsernameContent(data.username()); // Validar el nuevo username con IA

        existsByUsername(data.username());
        user.setUsername(data.username());
        userRepository.save(user);

        return new UserDTO(user.getId(), user.getUsername(), user.getProfile().getName());
    }

    @Transactional(readOnly = true)
    public UserStatsDTO getUserStats() {
        User user = getAuthenticatedUser();
        long topicsCount = topicRepository.countByUserId(user.getId());
        long responsesCount = responseRepository.countByUserId(user.getId());
        long followedTopicsCount = topicFollowRepository.countByUserId(user.getId());
        return new UserStatsDTO(topicsCount, responsesCount, followedTopicsCount);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        User user = getAuthenticatedUser();
        return toUserDTO(user);
    }

    @Transactional
    public JwtTokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = securityFilter.getTokenFromCookies(request, "refresh_token")
                .orElseThrow(() -> new AppException("Unauthorized", "UNAUTHORIZED"));

        try {
            String userId = tokenService.getSubjectFromToken(refreshToken);
            String newAccessToken = tokenService.generateAccessToken(findUserById(Long.valueOf(userId)));

            response.addCookie(createCookie("access_token", newAccessToken, "/", TokenService.ACCESS_TOKEN_EXPIRATION));

            return new JwtTokenResponse("Token de acceso actualizado correctamente.");

        } catch (TokenExpiredException e) {
            throw new AppException("Unauthorized", "UNAUTHORIZED");
        }
    }

    @Transactional
    public JwtTokenResponse logout(HttpServletResponse response) {
        response.addCookie(deleteCookie("access_token", "/"));
        response.addCookie(deleteCookie("refresh_token", "/api/auth/refresh-token"));
        return new JwtTokenResponse("Sesión cerrada exitosamente.");
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private void handleAccountDisabled(String username) throws MessagingException {
        User user = (User) userRepository.findByUsername(username);
        user.generateConfirmationToken();
        userRepository.save(user);
        emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), user.getToken());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Usuario no encontrado.", "NOT_FOUND"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("El email no está registrado.", "NOT_FOUND"));
    }

    private User findUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Token de confirmación inválido o expirado.", "BAD_REQUEST"));
    }

    private void validateTokenExpiration(User user) {
        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new AppException("El token de confirmación ha expirado.", "TOKEN_EXPIRED");
        }
    }

    private void checkIfAccountConfirmed(User user) {
        if (user.isAccountConfirmed()) {
            throw new AppException("La cuenta ya está confirmada.", "CONFLICT");
        }
    }

    private void checkIfAccountNotConfirmed(User user) {
        if (!user.isAccountConfirmed()) {
            throw new AppException("La cuenta no está confirmada.", "CONFLICT");
        }
    }

    private boolean isRecentRequest(LocalDateTime lastUpdated) {
        return ChronoUnit.MINUTES.between(lastUpdated, LocalDateTime.now()) < 2;
    }

    private void validatePasswordsMatch(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) {
            throw new AppException("Los passwords no coinciden.", "BAD_REQUEST");
        }
    }

    private void existsByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new AppException("El nombre de usuario ya está registrado.", "CONFLICT");
        }
    }

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new AppException("El email ya está registrado.", "CONFLICT");
        }
    }

    private void validateUsernameContent(String username) {
        String validationResponse = contentValidationService.validateUsername(username);

        if (validationResponse.equals("approved")) {
            return;
        } else {
            throw new AppException("El nombre de usuario " + validationResponse, "FORBIDDEN");
        }
    }

    private UserDTO toUserDTO(User user) {
        return commonHelperService.toUserDTO(user);
    }

    private Cookie createCookie(String name, String value, String path, long maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge((int) maxAge);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }

    private Cookie deleteCookie(String name, String path) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }

}