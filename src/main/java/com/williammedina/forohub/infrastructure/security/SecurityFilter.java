package com.williammedina.forohub.infrastructure.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.williammedina.forohub.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;

    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = SecurityConfigurations.PUBLIC_ENDPOINTS;

    private boolean isPublicUrl(String requestUri, String requestMethod) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> {
            String uriPattern = endpoint.url()
                    .replace("{token}", "[^/]+")
                    .replace("{topicId}", "[^/]+")
                    .replace("{responseId}", "[^/]+")
                    .replace("**", ".*");

            return requestUri.matches(uriPattern) && requestMethod.equals(endpoint.method().name());
        });
    }

    public Optional<String> getTokenFromCookies(HttpServletRequest request, String name) {

        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (isPublicUrl(requestUri, requestMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtener el token de las cookies
        Optional<String> tokenOptional = getTokenFromCookies(request, "access_token");

        if (tokenOptional.isEmpty()) {
            sendUnauthorizedResponse(response, "Token inválido o expirado.");
            return;
        }

        try {
            authenticateUser(tokenOptional.get());
        } catch (JWTVerificationException e) {
            sendUnauthorizedResponse(response, "Token inválido o expirado.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) {
        String userId = tokenService.getSubjectFromToken(token);
        if (userId == null) {
            throw new JWTVerificationException("Token inválido.");
        }

        var user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}
