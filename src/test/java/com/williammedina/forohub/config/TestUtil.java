package com.williammedina.forohub.config;

import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.domain.user.UserRepository;
import com.williammedina.forohub.infrastructure.security.TokenService;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TestUtil {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public User createUser(String email, String username) {
        return userRepository.save(new User(username, email, passwordEncoder.encode("password")));
    }

    public User getAuthenticatedUser(String username) {
        return (User) userRepository.findByUsername(username);
    }

    // Método para generar una cookie de autenticación con JWT
    public Cookie createCookie(User user, String name, String path, int expired) {
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
