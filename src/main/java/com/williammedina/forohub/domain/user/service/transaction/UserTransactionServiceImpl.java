package com.williammedina.forohub.domain.user.service.transaction;

import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.domain.user.service.notifier.UserNotifier;
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTransactionServiceImpl implements UserTransactionService {

    private final UserRepository userRepository;
    private final UserNotifier notifier;

    /**
     * Resends the confirmation email for a user whose account is not yet confirmed.
     * Uses REQUIRES_NEW to ensure this operation runs in a new, independent transaction
     * so it does not get rolled back if the calling transaction fails.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAccountDisabled(String username) throws MessagingException {
        UserEntity user = findUserByEmailOrUsername(username);
        user.generateConfirmationToken();
        userRepository.save(user);
        notifier.notifyConfirmationEmail(user);
        log.info("Resent account confirmation email for unconfirmed account: user ID {}", user.getId());
    }

    private UserEntity findUserByEmailOrUsername(String identifier) {
        return userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> {
                    log.error("User not registered: {}", identifier);
                    return new AppException("Usuario no está registrado.", HttpStatus.NOT_FOUND);
                });
    }
}
