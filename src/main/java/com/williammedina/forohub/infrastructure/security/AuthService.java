package com.williammedina.forohub.infrastructure.security;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.domain.user.service.context.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService, AuthenticatedUserProvider {

    private final UserRepository userRepository;

    @Override
    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserEntity) {
            return (UserEntity) authentication.getPrincipal();
        }

        log.error("Failed to retrieve a valid authenticated user");
        throw new IllegalStateException("El usuario autenticado no es válido.");
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        return userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format("Usuario con identificador '%s' no encontrado", identifier)
                        )
                );
    }

}
