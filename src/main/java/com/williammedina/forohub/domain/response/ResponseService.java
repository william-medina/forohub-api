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
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final CommonHelperService commonHelperService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ContentValidationService contentValidationService;


    @Transactional
    public ResponseDTO createResponse(@Valid CreateResponseDTO data) throws MessagingException {
        User user = getAuthenticatedUser();
        Topic topic = findTopicById(data.topicId());
        log.info("Usuario con ID: {} creando respuesta para tópico ID: {}", user.getId(), topic.getId());

        isTopicClosed(topic);
        validateResponseContent(data.content());// Validar el contenido de la respuesta con IA

        Response response = responseRepository.save(new Response(user, topic, data.content()));
        log.info("Respuesta creada con ID: {} por usuario ID: {}", response.getId(), user.getId());

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            log.debug("Notificando al propietario del tópico ID: {} sobre nueva respuesta", topic.getId());
            notificationService.notifyTopicReply(topic, user);
            emailService.notifyTopicReply(topic, user);
        }

        Hibernate.initialize(topic.getFollowedTopics());
        log.debug("Notificando a seguidores del tópico ID: {}", topic.getId());
        notificationService.notifyFollowersTopicReply(topic, user);
        emailService.notifyFollowersTopicReply(topic, user);

        return ResponseDTO.fromEntity(response);
    }

    @Transactional(readOnly = true)
    public Page<ResponseDTO> getAllResponsesByUser(Pageable pageable) {
        User user = getAuthenticatedUser();
        log.debug("Obteniendo respuestas del usuario con ID: {}", user.getId());
        return responseRepository.findByUserSortedByCreationDate(user, pageable).map(ResponseDTO::fromEntity);
    }

    @Transactional
    public ResponseDTO updateResponse(@Valid UpdateResponseDTO data, Long responseId) throws MessagingException {
        Response response = findResponseById(responseId);
        User user = checkModificationPermission(response);
        log.info("Usuario con ID: {} actualizando respuesta ID: {}", user.getId(), responseId);

        validateResponseContent(data.content()); // Validar el contenido de la respuesta actualizada con IA

        response.setContent(data.content());
        Response updatedResponse = responseRepository.save(response);

        if(!user.getUsername().equals(response.getUser().getUsername())) {
            log.debug("Notificando al propietario de la respuesta ID: {}", responseId);
            notificationService.notifyResponseEdited(response);
            emailService.notifyResponseEdited(response);
        }
        return ResponseDTO.fromEntity(updatedResponse);
    }

    @Transactional
    public void deleteResponse(Long responseId) throws MessagingException {
        Response response = findResponseById(responseId);
        User user = checkModificationPermission(response);
        if (response.getSolution()) {
           throw new AppException("No puedes eliminar una respuesta marcada como solución", HttpStatus.CONFLICT);
        }
        response.setIsDeleted(true);
        log.info("Respuesta ID: {} marcada como eliminada por usuario ID: {}", responseId, user.getId());
        //responseRepository.delete(response);

        if(!user.getUsername().equals(response.getUser().getUsername())) {
            log.debug("Notificando eliminación al propietario de la respuesta ID: {}", responseId);
            notificationService.notifyResponseDeleted(response);
            emailService.notifyResponseDeleted(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseDTO getResponseById(Long responseId) {
        log.debug("Obteniendo respuesta con ID: {}", responseId);
        Response response = findResponseById(responseId);
        return ResponseDTO.fromEntity(response);
    }

    @Transactional
    public ResponseDTO setCorrectResponse(Long responseId) throws MessagingException {
        User user = getAuthenticatedUser();
        if (!user.hasElevatedPermissions()) {
            log.warn("Usuario con ID: {} intentó cambiar estado del topico sin permisos con la respuesta ID: {}", user.getId(), responseId);
            throw new AppException("No tienes permiso para modificar el estado de la respuesta", HttpStatus.FORBIDDEN);
        }

        Response response = findResponseById(responseId);
        log.info("Usuario con ID: {} cambiar estado de respuesta ID: {}", user.getId(), responseId);
        List<Response> responses = responseRepository.findByTopicId(response.getTopic().getId());

        boolean isCurrentlySolution = response.getSolution();
        responses.forEach(res -> res.setSolution(false)); // Desactivar todas las soluciones

        if (!isCurrentlySolution) {
            response.setSolution(true);
            response.getTopic().setStatus(Topic.Status.CLOSED);
            log.info("Respuesta ID: {} marcada como solución para tópico ID: {}", response.getId(), response.getTopic().getId());
        } else {
            response.getTopic().setStatus(Topic.Status.ACTIVE);
            log.info("Respuesta ID: {} desmarcada como solución para tópico ID: {}", response.getId(), response.getTopic().getId());
        }

        responseRepository.saveAll(responses);

        if(response.getSolution()) {
            log.debug("Enviando notificaciones por solución de respuesta");
            notificationService.notifyTopicSolved(response.getTopic());
            notificationService.notifyResponseSolved(response, response.getTopic());
            notificationService.notifyFollowersTopicSolved(response.getTopic());
            emailService.notifyTopicSolved(response.getTopic());
            emailService.notifyResponseSolved(response, response.getTopic());
            emailService.notifyFollowersTopicSolved(response.getTopic());
        }

        return ResponseDTO.fromEntity(response);
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private Topic findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private Response findResponseById(Long responseId) {
        return responseRepository.findByIdAndIsDeletedFalse(responseId)
                .orElseThrow(() ->  {
                    log.error("Respuesta no encontrada con ID: {}", responseId);
                    return new AppException("Respuesta no encontrada", HttpStatus.NOT_FOUND);
                });
    }

    private User checkModificationPermission(Response response) {
        User user = getAuthenticatedUser();
        // Si el usuario es el propietario O tiene permisos elevados, puede modificar la respuesta
        if (!response.getUser().equals(getAuthenticatedUser()) && !getAuthenticatedUser().hasElevatedPermissions()) {
            log.warn("Usuario con ID: {} sin permisos intentó modificar respuesta ID: {}", user.getId(), response.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta respuesta", HttpStatus.FORBIDDEN);
        }

        return user;
    }

    private void isTopicClosed(Topic topic) {
        if(topic.isTopicClosed()) {
            log.warn("Intento de respuesta en tópico cerrado - ID: {}", topic.getId());
            throw new AppException("No se puede crear una respuesta. El tópico está cerrado.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateResponseContent(String content) {
        String validationResponse = contentValidationService.validateContent(content);
        if (!"approved".equals(validationResponse)) {
            log.warn("Contenido no aprobado en respuesta: {}", validationResponse);
            throw new AppException("La respuesta " + validationResponse, HttpStatus.FORBIDDEN);
        }
    }

}
