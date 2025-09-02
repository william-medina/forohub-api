package com.williammedina.forohub.domain.user.service;

import com.williammedina.forohub.domain.user.dto.*;
import com.williammedina.forohub.infrastructure.response.MessageResponse;
import com.williammedina.forohub.infrastructure.security.JwtTokenResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    JwtTokenResponse authenticateAndGenerateToken(LoginUserDTO data, HttpServletResponse response) throws MessagingException;
    UserDTO createAccount(CreateUserDTO data) throws MessagingException;
    UserDTO confirmAccount(String token);
    UserDTO requestConfirmationCode(EmailUserDTO data) throws MessagingException;
    UserDTO forgotPassword(EmailUserDTO data) throws MessagingException;
    UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO data);
    UserDTO updateCurrentUserPassword(UpdateCurrentUserPasswordDTO data);
    UserDTO updateUsername(UpdateUsernameDTO data);
    UserStatsDTO getUserStats();
    UserDTO getCurrentUser();
    JwtTokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
    MessageResponse logout(HttpServletResponse response);

}
