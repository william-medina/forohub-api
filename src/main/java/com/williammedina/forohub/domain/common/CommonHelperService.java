package com.williammedina.forohub.domain.common;

import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topic.TopicRepository;
import com.williammedina.forohub.domain.user.User;
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

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        log.error("No se pudo obtener un usuario autenticado válido");
        throw new IllegalStateException("El usuario autenticado no es válido.");
    }

    public Topic findTopicById(Long topicId) {
        return topicRepository.findByIdAndNotDeleted(topicId)
                .orElseThrow(() -> {
                    log.warn("Tópico no encontrado con ID: {}", topicId);
                    return new AppException("Tópico no encontrado", HttpStatus.NOT_FOUND);
                });
    }
}
