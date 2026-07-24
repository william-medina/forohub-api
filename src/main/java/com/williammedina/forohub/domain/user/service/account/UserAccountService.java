package com.williammedina.forohub.domain.user.service.account;

import com.williammedina.forohub.domain.user.dto.CreateUserDTO;
import com.williammedina.forohub.domain.user.dto.EmailUserDTO;
import com.williammedina.forohub.domain.user.dto.UpdatePasswordWithTokenDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;

public interface UserAccountService {

    UserDTO createAccount(CreateUserDTO request);
    UserDTO confirmAccount(String token);
    UserDTO requestConfirmationCode(EmailUserDTO request);
    UserDTO forgotPassword(EmailUserDTO request);
    UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO request);

}
