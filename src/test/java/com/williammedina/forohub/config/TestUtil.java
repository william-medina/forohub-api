package com.williammedina.forohub.config;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.infrastructure.security.TokenService;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Component
@AllArgsConstructor
public class TestUtil {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserEntity createUser(String email, String username) {
        return userRepository.save(new UserEntity(username, email, passwordEncoder.encode("password")));
    }

    public UserEntity getAuthenticatedUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con username '" + username + "' no encontrado"));
    }

    public MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder, UserEntity user) {
        String token = tokenService.generateAccessToken(user);
        return requestBuilder.header("Authorization", "Bearer " + token);
    }

    // Method to generate an authentication cookie with JWT
    public Cookie createCookie(UserEntity user, String name, String path, int expired) {
        String token = tokenService.generateToken(user, expired);

        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge(expired);
        cookie.setAttribute("SameSite", "Strict");

        return cookie;
    }
}
