package com.williammedina.forohub.infrastructure.security;

import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class CookieService {

    public Optional<String> getTokenFromCookies(HttpServletRequest request, String name) {

        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public String extractRefreshToken(HttpServletRequest request) {
        return getTokenFromCookies(request, "refresh_token")
                .orElseThrow(() -> {
                    log.warn("Refresh token not present in cookies");
                    return new AppException("Refresh token no presente", HttpStatus.UNAUTHORIZED);
                });
    }

    public Cookie createRefreshCookie(String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/token");
        cookie.setMaxAge((int) TokenService.REFRESH_TOKEN_EXPIRATION_SECONDS);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "true");
        return cookie;
    }

    public Cookie deleteRefreshCookie() {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/api/auth/token");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "true");
        return cookie;
    }
}
