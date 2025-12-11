package com.williammedina.forohub.domain.user.service.handler;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.transaction.UserTransactionService;
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnconfirmedAccountHandlerImpl implements UnconfirmedAccountHandler{

    private final UserTransactionService userTransactionService;

    @Override
    public void handleIfUnconfirmed(UserEntity user) throws MessagingException {
        if (!user.isAccountConfirmed()) {
            log.warn("Unconfirmed user attempted login: {} (ID: {})", user.getUsername(), user.getId());
            userTransactionService.handleAccountDisabled(user.getUsername());
            throw new AppException("La cuenta no está confirmada. Por favor, verifique su email.", HttpStatus.FORBIDDEN);
        }
    }
}
