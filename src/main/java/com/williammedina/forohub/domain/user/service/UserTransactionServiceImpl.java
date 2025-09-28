package com.williammedina.forohub.domain.user.service;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTransactionServiceImpl implements UserTransactionService {

    private final CommonHelperService commonHelperService;
    private final UserRepository userRepository;
    private final EmailService emailService;


    /**
     * Resends the confirmation email for a user whose account is not yet confirmed.
     * Uses REQUIRES_NEW to ensure this operation runs in a new, independent transaction
     * so it does not get rolled back if the calling transaction fails.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAccountDisabled(String username) throws MessagingException {
        UserEntity user = commonHelperService.findUserByEmailOrUsername(username);
        user.generateConfirmationToken();
        userRepository.save(user);
        emailService.sendConfirmationEmail(user.getEmail(), user);
        log.info("Resent account confirmation email for unconfirmed account: user ID {}", user.getId());
    }
}
