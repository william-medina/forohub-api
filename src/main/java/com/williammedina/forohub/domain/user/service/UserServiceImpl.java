package com.williammedina.forohub.domain.user.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import com.williammedina.forohub.domain.response.repository.ResponseRepository;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.topicfollow.repository.TopicFollowRepository;
import com.williammedina.forohub.domain.user.entity.User;
import com.williammedina.forohub.domain.user.dto.*;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.infrastructure.exception.*;
import com.williammedina.forohub.infrastructure.response.MessageResponse;
import com.williammedina.forohub.infrastructure.security.JwtTokenResponse;
import com.williammedina.forohub.infrastructure.security.SecurityFilter;
import com.williammedina.forohub.infrastructure.security.TokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

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

    @Override
    @Transactional
    public JwtTokenResponse authenticateAndGenerateToken(LoginUserDTO data, HttpServletResponse response) throws MessagingException {
        log.info("Intentando autenticar usuario: {}", data.username());

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authenticationToken);

        User user = (User) authenticatedUser.getPrincipal();

        if (!user.isAccountConfirmed()) {
            log.warn("Usuario no confirmado intentó autenticarse: {} (ID: {})", data.username(), user.getId());
            handleAccountDisabled(data.username());
            throw new AppException("La cuenta no está confirmada. Por favor, verifique su email.", HttpStatus.FORBIDDEN);
        }

        // Generar tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        response.addCookie(createCookie("refresh_token", refreshToken, "/api/auth/refresh-token", TokenService.REFRESH_TOKEN_EXPIRATION));
        log.info("Usuario autenticado correctamente ID: {}", user.getId());

        return new JwtTokenResponse(accessToken);
    }

    @Override
    @Transactional
    public UserDTO createAccount(CreateUserDTO data) throws MessagingException {
        log.info("Creando cuenta para: {}", data.username());

        validatePasswordsMatch(data.password(), data.password_confirmation());
        existsByUsername(data.username());
        existsByEmail(data.email());

        validateUsernameContent(data.username()); // Validar el username con IA

        User user = new User(data.username(), data.email().trim().toLowerCase(), passwordEncoder.encode(data.password()));
        User userCreated = userRepository.save(user);

        emailService.sendConfirmationEmail(userCreated.getEmail(), userCreated);
        log.info("Usuario creado con ID: {}", userCreated.getId());

        return UserDTO.fromEntity(userCreated);
    }

    @Override
    @Transactional
    public UserDTO confirmAccount(String token) {
        log.info("Confirmando cuenta con token");

        User user = findUserByToken(token);
        validateTokenExpiration(user);
        checkIfAccountConfirmed(user);

        user.setAccountConfirmed(true);
        user.clearTokenData();
        userRepository.save(user);
        log.info("Cuenta confirmada para usuario ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO requestConfirmationCode(EmailUserDTO data) throws MessagingException {
        log.info("Solicitud de código de confirmación para email: {}", data.email());

        User user = findUserByEmail(data.email());
        checkIfAccountConfirmed(user);

        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            log.warn("Solicitud de confirmación demasiado pronto para usuario ID: {}", user.getId());
            throw new AppException("Debe esperar 2 minutos para solicitar otro código de confirmación.", HttpStatus.BAD_REQUEST);
        }

        user.generateConfirmationToken();
        emailService.sendConfirmationEmail(user.getEmail(), user);
        log.info("Código de confirmación generado y enviado para usuario ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO forgotPassword(EmailUserDTO data) throws MessagingException {
        log.info("Solicitud de recuperación de password para: {}", data.email());

        User user = findUserByEmail(data.email());
        checkIfAccountNotConfirmed(user);

        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            log.warn("Solicitud de reset demasiado pronto para usuario ID: {}", user.getId());
            throw new AppException("Debe esperar 2 minutos para solicitar otro código de restablecimiento de password.", HttpStatus.BAD_REQUEST);
        }

        user.generateConfirmationToken();
        emailService.sendPasswordResetEmail(user.getEmail(), user);
        log.info("Código de restablecimiento de contraseña generado y enviado para usuario ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO data) {
        log.info("Actualizando password usando token");

        validatePasswordsMatch(data.password(), data.password_confirmation());

        User user = findUserByToken(token);
        validateTokenExpiration(user);
        checkIfAccountNotConfirmed(user);

        user.setPassword(passwordEncoder.encode(data.password()));
        user.clearTokenData();
        userRepository.save(user);
        log.info("Password actualizado para usuario ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO updateCurrentUserPassword(UpdateCurrentUserPasswordDTO data) {
        validatePasswordsMatch(data.password(), data.password_confirmation());

        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(data.current_password(), user.getPassword())) {
            log.warn("Password actual incorrecto para usuario ID: {}", user.getId());
            throw new AppException("El password actual es incorrecto.", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(data.password()));
        userRepository.save(user);
        log.info("Password cambiado exitosamente para usuario ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO updateUsername(UpdateUsernameDTO data) {
        User user = getAuthenticatedUser();

        if (user.getUsername().equals(data.username())) {
            log.warn("Intento de cambiar a mismo username - usuario ID: {}", user.getId());
            throw new AppException("Debes ingresa un nuevo nombre.", HttpStatus.BAD_REQUEST);
        }

        validateUsernameContent(data.username()); // Validar el nuevo username con IA

        existsByUsername(data.username());
        user.setUsername(data.username());
        userRepository.save(user);
        log.info("Username actualizado a '{}' para usuario ID: {}", data.username(), user.getId());

        return new UserDTO(user.getId(), user.getUsername(), user.getProfile().getName());
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsDTO getUserStats() {
        User user = getAuthenticatedUser();
        log.debug("Obteniendo estadísticas para usuario ID: {}", user.getId());

        long topicsCount = topicRepository.countByUserId(user.getId());
        long responsesCount = responseRepository.countByUserId(user.getId());
        long followedTopicsCount = topicFollowRepository.countByUserId(user.getId());
        return new UserStatsDTO(topicsCount, responsesCount, followedTopicsCount);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        User user = getAuthenticatedUser();
        log.debug("Consultando datos del usuario ID: {}", user.getId());
        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public JwtTokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Solicitando refresh token");

        String refreshToken = securityFilter.getTokenFromCookies(request, "refresh_token")
                .orElseThrow(() -> {
                    log.warn("Token de refresh no presente en cookies");
                    return new AppException("Unauthorized", HttpStatus.UNAUTHORIZED);
                });

        try {
            String userId = tokenService.getSubjectFromToken(refreshToken);
            String newAccessToken = tokenService.generateAccessToken(findUserById(Long.valueOf(userId)));

            log.info("Nuevo access token generado para usuario ID: {}", userId);

            return new JwtTokenResponse(newAccessToken);

        } catch (TokenExpiredException e) {
            log.warn("Token de refresh expirado");
            throw new AppException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public MessageResponse logout(HttpServletResponse response) {
        User user = getAuthenticatedUser();
        log.info("Cierre de sesión para usuario ID: {}", user.getId());

        response.addCookie(deleteCookie("refresh_token", "/api/auth/refresh-token"));
        return new MessageResponse("Sesión cerrada exitosamente.");
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private void handleAccountDisabled(String username) throws MessagingException {
        User user = findUserByUsername(username);
        user.generateConfirmationToken();
        userRepository.save(user);
        emailService.sendConfirmationEmail(user.getEmail(), user);
        log.info("Se reenvió confirmación por cuenta no confirmada: usuario ID {}", user.getId());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con ID: {}", userId);
                    return new AppException("Usuario no encontrado.", HttpStatus.NOT_FOUND);
                });
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Email no registrado: {}", email);
                    return new AppException("El email no está registrado.", HttpStatus.NOT_FOUND);
                });
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Nombre de usuario no registrado: {}", username);
                    return new AppException("El nombre de usuario no está registrado.", HttpStatus.NOT_FOUND);
                });
    }

    private User findUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Token inválido o expirado");
                    return new AppException("Token inválido o expirado.", HttpStatus.BAD_REQUEST);
                });
    }

    private void validateTokenExpiration(User user) {
        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            log.warn("Token expirado para usuario ID: {}", user.getId());
            throw new AppException("El token de confirmación ha expirado.", HttpStatus.GONE);
        }
    }

    private void checkIfAccountConfirmed(User user) {
        if (user.isAccountConfirmed()) {
            log.warn("Cuenta ya confirmada para usuario ID: {}", user.getId());
            throw new AppException("La cuenta ya está confirmada.", HttpStatus.CONFLICT);
        }
    }

    private void checkIfAccountNotConfirmed(User user) {
        if (!user.isAccountConfirmed()) {
            log.warn("Cuenta aún no confirmada para usuario ID: {}", user.getId());
            throw new AppException("La cuenta no está confirmada.", HttpStatus.CONFLICT);
        }
    }

    private boolean isRecentRequest(LocalDateTime lastUpdated) {
        return ChronoUnit.MINUTES.between(lastUpdated, LocalDateTime.now()) < 2;
    }

    private void validatePasswordsMatch(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) {
            log.warn("Passwords no coinciden");
            throw new AppException("Los passwords no coinciden.", HttpStatus.BAD_REQUEST);
        }
    }

    private void existsByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            log.warn("Username ya registrado: {}", username);
            throw new AppException("El nombre de usuario ya está registrado.", HttpStatus.CONFLICT);
        }
    }

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            log.warn("Email ya registrado: {}", email);
            throw new AppException("El email ya está registrado.", HttpStatus.CONFLICT);
        }
    }

    private void validateUsernameContent(String username) {
        String validationResponse = contentValidationService.validateUsername(username);
        if (!"approved".equals(validationResponse)) {
            log.warn("Username no aprobado: {} - Resultado: {}", username, validationResponse);
            throw new AppException("El nombre de usuario " + validationResponse, HttpStatus.FORBIDDEN);
        }
    }

    private Cookie createCookie(String name, String value, String path, long maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge((int) maxAge);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "true");
        return cookie;
    }

    private Cookie deleteCookie(String name, String path) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "true");
        return cookie;
    }

}