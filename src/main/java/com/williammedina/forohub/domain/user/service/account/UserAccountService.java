package com.williammedina.forohub.domain.user.service.account;

import com.williammedina.forohub.domain.user.dto.CreateUserDTO;
import com.williammedina.forohub.domain.user.dto.EmailUserDTO;
import com.williammedina.forohub.domain.user.dto.UpdatePasswordWithTokenDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import jakarta.mail.MessagingException;

public interface UserAccountService {

    UserDTO createAccount(CreateUserDTO request) throws MessagingException;
    UserDTO confirmAccount(String token);
    UserDTO requestConfirmationCode(EmailUserDTO request) throws MessagingException;
    UserDTO forgotPassword(EmailUserDTO request) throws MessagingException;
    UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO request);

}
