package com.williammedina.forohub.domain.user.service.auth;

import com.williammedina.forohub.domain.user.dto.*;
import com.williammedina.forohub.infrastructure.response.MessageResponse;
import com.williammedina.forohub.infrastructure.security.JwtTokenResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserAuthService {

    JwtTokenResponse authenticateAndGenerateToken(LoginUserDTO data, HttpServletResponse response) throws MessagingException;
    JwtTokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
    MessageResponse logout(HttpServletRequest request, HttpServletResponse response);

}
