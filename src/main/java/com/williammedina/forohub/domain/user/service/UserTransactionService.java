package com.williammedina.forohub.domain.user.service;

import jakarta.mail.MessagingException;

public interface UserTransactionService {

    void handleAccountDisabled(String username) throws MessagingException;

}
