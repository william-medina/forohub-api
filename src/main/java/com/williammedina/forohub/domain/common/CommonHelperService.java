package com.williammedina.forohub.domain.common;

import com.williammedina.forohub.domain.topic.entity.Topic;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.user.entity.User;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CommonHelperService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        log.error("Failed to retrieve a valid authenticated user");
        throw new IllegalStateException("El usuario autenticado no es válido.");
    }

    public Topic findTopicById(Long topicId) {
        return topicRepository.findByIdAndNotDeleted(topicId)
                .orElseThrow(() -> {
                    log.warn("Topic not found with ID: {}", topicId);
                    return new AppException("Tópico no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    public User findUserByEmailOrUsername(String identifier) {
        return userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> {
                    log.error("User not registered: {}", identifier);
                    return new AppException("Usuario no está registrado.", HttpStatus.NOT_FOUND);
                });
    }
}
