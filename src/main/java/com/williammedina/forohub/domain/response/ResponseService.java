package com.williammedina.forohub.domain.response;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.common.ContentValidationService;
import com.williammedina.forohub.domain.notification.NotificationService;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.response.dto.CreateResponseDTO;
import com.williammedina.forohub.domain.response.dto.ResponseDTO;
import com.williammedina.forohub.domain.response.dto.UpdateResponseDTO;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.email.EmailService;
import com.williammedina.forohub.infrastructure.errors.AppException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final CommonHelperService commonHelperService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ContentValidationService contentValidationService;

    public ResponseService(
            ResponseRepository responseRepository,
            CommonHelperService commonHelperService,
            NotificationService notificationService,
            EmailService emailService,
            ContentValidationService contentValidationService
    ) {
        this.responseRepository = responseRepository;
        this.commonHelperService = commonHelperService;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.contentValidationService = contentValidationService;
    }

    @Transactional
    public ResponseDTO createResponse(@Valid CreateResponseDTO data) throws MessagingException {
        User user = getAuthenticatedUser();
        Topic topic = findTopicById(data.topicId());
        isTopicClosed(topic);
        validateResponseContent(data.content());// Validar el contenido de la respuesta con IA

        Response response = responseRepository.save(new Response(user, topic, data.content()));

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            notificationService.notifyTopicReply(topic, user);
            emailService.notifyTopicReply(topic, user);
        }

        Hibernate.initialize(topic.getFollowedTopics());
        notificationService.notifyFollowersTopicReply(topic, user);
        emailService.notifyFollowersTopicReply(topic, user);

        return toResponseDTO(response);
    }

    @Transactional(readOnly = true)
    public Page<ResponseDTO> getAllResponsesByUser(Pageable pageable) {
        User user = getAuthenticatedUser();
        return responseRepository.findByUserSortedByCreationDate(user, pageable).map(this::toResponseDTO);
    }

    @Transactional
    public ResponseDTO updateResponse(@Valid UpdateResponseDTO data, Long responseId) throws MessagingException {
        Response response = findResponseById(responseId);
        User user = checkModificationPermission(response);

        validateResponseContent(data.content()); // Validar el contenido de la respuesta actualizada con IA

        response.setContent(data.content());
        Response updatedResponse = responseRepository.save(response);

        if(!user.getUsername().equals(response.getUser().getUsername())) {
            notificationService.notifyResponseEdited(response);
            emailService.notifyResponseEdited(response);
        }
        return toResponseDTO(updatedResponse);
    }

    @Transactional
    public void deleteResponse(Long responseId) throws MessagingException {
        Response response = findResponseById(responseId);
        User user = checkModificationPermission(response);
        Topic topic = findTopicById(response.getTopic().getId());
        topic.setStatus(Topic.Status.ACTIVE);
        response.setIsDeleted(true);
        //responseRepository.delete(response);

        if(!user.getUsername().equals(response.getUser().getUsername())) {
            notificationService.notifyResponseDeleted(response);
            emailService.notifyResponseDeleted(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO getResponseById(Long responseId) {
        Response response = findResponseById(responseId);
        return toResponseDTO(response);
    }

    @Transactional
    public ResponseDTO setCorrectResponse(Long responseId) throws MessagingException {
        User user = getAuthenticatedUser();
        if (!user.hasElevatedPermissions()) {
            throw new AppException("No tienes permiso para modificar el estado del tópico", "FORBIDDEN");
        }

        Response response = findResponseById(responseId);
        List<Response> responses = responseRepository.findByTopicId(response.getTopic().getId());

        boolean isCurrentlySolution = response.getSolution();
        responses.forEach(res -> res.setSolution(false)); // Desactivar todas las soluciones
        response.getTopic().setStatus(Topic.Status.ACTIVE);

        if (!isCurrentlySolution) {
            response.setSolution(true);
            response.getTopic().setStatus(Topic.Status.CLOSED);
        }

        responseRepository.saveAll(responses);

        if(response.getSolution()) {
            notificationService.notifyTopicSolved(response.getTopic());
            notificationService.notifyResponseSolved(response, response.getTopic());
            notificationService.notifyFollowersTopicSolved(response.getTopic());
            emailService.notifyTopicSolved(response.getTopic());
            emailService.notifyResponseSolved(response, response.getTopic());
            emailService.notifyFollowersTopicSolved(response.getTopic());
        }

        return toResponseDTO(response);
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private Topic findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private Response findResponseById(Long responseId) {
        return responseRepository.findByIdAndIsDeletedFalse(responseId)
                .orElseThrow(() -> new AppException("Respuesta no encontrada", "NOT_FOUND"));
    }

    private User checkModificationPermission(Response response) {
        // Si el usuario es el propietario O tiene permisos elevados, puede modificar la respuesta
        if (!response.getUser().equals(getAuthenticatedUser()) && !getAuthenticatedUser().hasElevatedPermissions()) {
            throw new AppException("No tienes permiso para realizar cambios en esta respuesta", "FORBIDDEN");
        }

        return getAuthenticatedUser();
    }

    private void isTopicClosed(Topic topic) {
        if(topic.isTopicClosed()) {
            throw new AppException("No se puede crear una respuesta. El tópico está cerrado.", "FORBIDDEN");
        }
    }

    private void validateResponseContent(String content) {
        if (!contentValidationService.validateContent(content)) {
            throw new AppException("La respuesta tiene contenido inapropiado.", "FORBIDDEN");
        }
    }

    private ResponseDTO toResponseDTO(Response response) {
        return commonHelperService.toResponseDTO(response);
    }

}
