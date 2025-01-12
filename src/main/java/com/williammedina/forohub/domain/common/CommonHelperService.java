package com.williammedina.forohub.domain.common;

import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.response.dto.ResponseDTO;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topic.TopicRepository;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.domain.user.dto.AuthorDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import com.williammedina.forohub.infrastructure.errors.AppException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonHelperService {

    private final TopicRepository topicRepository;

    public CommonHelperService(
            TopicRepository topicRepository
    ) {
        this.topicRepository = topicRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        throw new IllegalStateException("El usuario autenticado no es válido.");
    }

    public Topic findTopicById(Long topicId) {
        return topicRepository.findByIdAndNotDeleted(topicId)
                .orElseThrow(() -> new AppException("Tópico no encontrado", "NOT_FOUND"));
    }

    public UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getProfile().getName()
        );
    }

    public TopicDTO toTopicDTO(Topic topic) {
        return new TopicDTO(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getCourse().getName(),
                topic.getCourse().getCategory(),
                topic.getUser().getUsername(),
                topic.getResponses().size(),
                topic.getStatus(),
                topic.getCreatedAt(),
                topic.getUpdatedAt()
        );
    }

    public ResponseDTO toResponseDTO(Response response) {

        AuthorDTO author = new AuthorDTO(
                response.getUser().getUsername(),
                response.getUser().getProfile().getName()
        );

        return new ResponseDTO(
                response.getId(),
                response.getContent(),
                author,
                response.getSolution(),
                response.getCreatedAt(),
                response.getUpdatedAt()
        );
    }
}
