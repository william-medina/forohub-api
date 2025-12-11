package com.williammedina.forohub.infrastructure.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.infrastructure.exception.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = SecurityConfigurations.PUBLIC_ENDPOINTS;

    private boolean isPublicUrl(String requestUri, String requestMethod) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> {
            String uriPattern = endpoint.url()
                    .replace("{token}", "[^/]+")
                    .replace("{topicId}", "[^/]+")
                    .replace("{replyId}", "[^/]+")
                    .replace("**", ".*");

            return requestUri.matches(uriPattern) && requestMethod.equals(endpoint.method().name());
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (isPublicUrl(requestUri, requestMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Leer el access token desde el header "Authorization"
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header in request: {} {}", requestMethod, requestUri);
            sendUnauthorizedResponse(request, response, "Token inválido o ausente.");
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            authenticateUser(token);
        } catch (JWTVerificationException e) {
            log.warn("Token verification error: {}", e.getMessage());
            sendUnauthorizedResponse(request, response, "Token inválido o expirado.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) {
        String userId = tokenService.getSubjectFromToken(token);
        if (userId == null) {
            log.error("Token does not contain a valid user ID.");
            throw new JWTVerificationException("Token inválido.");
        }

        var user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                    log.error("User with ID {} not found in the database.", userId);
                    return new UsernameNotFoundException("Usuario no encontrado");
                });

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        log.warn("Authentication failure response: {}", message);

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error(HttpStatus.UNAUTHORIZED.name())
                .message(message)
                .errors(null)
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
