package com.williammedina.forohub.domain.user.service.account;

import com.williammedina.forohub.domain.user.dto.CreateUserDTO;
import com.williammedina.forohub.domain.user.dto.EmailUserDTO;
import com.williammedina.forohub.domain.user.dto.UpdatePasswordWithTokenDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.enums.RequestType;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.domain.user.service.finder.UserFinder;
import com.williammedina.forohub.domain.user.service.notifier.UserNotifier;
import com.williammedina.forohub.domain.user.service.validator.UserValidator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidator validator;
    private final UserFinder userFinder;
    private final UserNotifier notifier;

    @Override
    @Transactional
    public UserDTO createAccount(CreateUserDTO request) throws MessagingException {
        log.info("Creating account for: {}", request.username());

        validator.ensurePasswordsMatch(request.password(), request.password_confirmation());
        validator.ensureUsernameIsUnique(request.username());
        validator.ensureEmailIsUnique(request.email());
        validator.ensureUsernameContentIsValid(request.username()); // Validate the username with AI

        UserEntity newUserEntity = new UserEntity(request.username(), request.email().trim().toLowerCase(), passwordEncoder.encode(request.password()));
        UserEntity userCreated = userRepository.save(newUserEntity);

        notifier.notifyConfirmationEmail(userCreated);
        log.info("User created successfully - ID: {}", userCreated.getId());

        return UserDTO.fromEntity(userCreated);
    }

    @Override
    @Transactional
    public UserDTO confirmAccount(String token) {
        log.info("Confirming account with token");

        UserEntity user = userFinder.findUserByValidToken(token);
        validator.ensureTokenIsNotExpired(user);
        validator.ensureAccountIsNotConfirmed(user);

        user.setAccountConfirmed(true);
        user.clearTokenData();
        userRepository.save(user);
        log.info("Account confirmed for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO requestConfirmationCode(EmailUserDTO request) throws MessagingException {
        log.info("Requesting confirmation code for email: {}", request.email());

        UserEntity user = userFinder.findUserByEmail(request.email());
        validator.ensureAccountIsNotConfirmed(user);
        validator.ensureRequestIntervalIsAllowed(user, RequestType.CONFIRMATION);

        user.generateConfirmationToken();
        notifier.notifyConfirmationEmail(user);
        log.info("Confirmation code generated and sent for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO forgotPassword(EmailUserDTO request) throws MessagingException {
        log.info("Password reset requested for: {}", request.email());

        UserEntity user = userFinder.findUserByEmail(request.email());
        validator.ensureAccountIsConfirmed(user);
        validator.ensureRequestIntervalIsAllowed(user, RequestType.PASSWORD_RESET);

        user.generateConfirmationToken();
        notifier.notifyPasswordResetEmail(user);
        log.info("Password reset code generated and sent for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO request) {
        log.info("Updating password using token");

        validator.ensurePasswordsMatch(request.password(), request.password_confirmation());

        UserEntity user = userFinder.findUserByValidToken(token);
        validator.ensureTokenIsNotExpired(user);
        validator.ensureAccountIsConfirmed(user);

        user.setPassword(passwordEncoder.encode(request.password()));
        user.clearTokenData();
        userRepository.save(user);
        log.info("Password updated for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }
}
