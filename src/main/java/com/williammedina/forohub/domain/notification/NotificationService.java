package com.williammedina.forohub.domain.notification;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.notification.dto.NotificationDTO;
import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topicfollow.TopicFollow;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CommonHelperService commonHelperService;

    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotificationsByUser() {
        User user = getAuthenticatedUser();
        log.debug("Consultando todas las notificaciones del usuario con ID: {}", user.getId());
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream().map(NotificationDTO::fromEntity).toList();
    }

    @Transactional
    public void deleteNotification(Long notifyId) {
        Notification notification = findNotificationById(notifyId);
        checkModificationPermission(notification);

        notificationRepository.delete(notification);
        log.info("Notificación con ID: {} eliminada por el usuario con ID: {}", notifyId, getAuthenticatedUser().getId());
    }

    @Transactional
    public NotificationDTO markNotificationAsRead(Long notifyId) {
        Notification notification = findNotificationById(notifyId);
        checkModificationPermission(notification);

        notification.setIsRead(true);
        log.debug("Notificación con ID: {} marcada como leída por el usuario con ID: {}", notifyId, getAuthenticatedUser().getId());

        return NotificationDTO.fromEntity(notification);
    }

    @Transactional
    public void notifyTopicReply(Topic topic, User user) {
        String title = "Nueva respuesta a tu tópico";
        String message = "Tu tópico ha recibido una nueva respuesta. "
                + user.getUsername() + " respondió al tópico '"
                + topic.getTitle() + "' del curso: " + topic.getCourse().getName();

        createNotification(topic.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.REPLY);
        log.debug("Notificación de respuesta creada para el tópico ID: {} al usuario con ID: {}", topic.getId(), topic.getUser().getId());
    }

    @Transactional
    public void notifyTopicSolved(Topic topic) {
        String title = "Tu tópico ha sido marcado como solucionado";
        String message = "Tu tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido marcado como solucionado.";

        createNotification(topic.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.SOLVED);
        log.debug("Notificación de tópico solucionado creada para el tópico ID: {} al usuario con ID: {}", topic.getId(), topic.getUser().getId());
    }

    @Transactional
    public void notifyTopicEdited(Topic topic) {
        String title = "Tu tópico ha sido editado";
        String message = "Se ha realizado cambios en tu tópico titulado '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(topic.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.EDITED);
        log.debug("Notificación de edición creada para el tópico ID: {} al usuario con ID: {}", topic.getId(), topic.getUser().getId());
    }

    @Transactional
    public void notifyTopicDeleted(Topic topic) {
        String title = "Tu tópico ha sido eliminado";
        String message = "Lamentamos informarte que tu tópico titulado '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido eliminado. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(topic.getUser(), null, null, title, message, Notification.Type.TOPIC, Notification.Subtype.DELETED);
        log.debug("Notificación de eliminación creada para el usuario con ID: {} por el tópico eliminado ID: {}", topic.getUser().getId(), topic.getId());
    }

    @Transactional
    public void notifyResponseSolved(Response response, Topic topic) {
        String title = "Tu respuesta ha sido marcada como solución";
        String message = "Tu respuesta en el tópico '" + response.getTopic().getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido marcada como solución.";

        createNotification(response.getUser(), topic, response, title, message, Notification.Type.RESPONSE, Notification.Subtype.SOLVED);
        log.debug("Notificación de respuesta marcada como solución creada para usuario ID: {}", response.getUser().getId());
    }

    @Transactional
    public void notifyResponseEdited(Response response) {
        String title = "Tu respuesta ha sido editada";
        String message = "Se han realizado cambios en tu respuesta del tópico '" + response.getTopic().getTitle() + "' del curso: " + response.getTopic().getCourse().getName() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(response.getUser(), response.getTopic(), response, title, message, Notification.Type.RESPONSE, Notification.Subtype.EDITED);
        log.debug("Notificación de edición de respuesta creada para usuario ID: {}", response.getUser().getId());
    }

    @Transactional
    public void notifyResponseDeleted(Response response) {
        String title = "Tu respuesta ha sido eliminada";
        String message = "Lamentamos informarte que tu respuesta del tópico '" + response.getTopic().getTitle() + "' del curso: " + response.getTopic().getCourse().getName() + " ha sido eliminada. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(response.getUser(), response.getTopic(), null, title, message, Notification.Type.RESPONSE, Notification.Subtype.DELETED);
        log.debug("Notificación de eliminación de respuesta creada para usuario ID: {}", response.getUser().getId());
    }

    @Transactional
    public void notifyFollowersTopicReply(Topic topic, User user) {
        String title = "Nueva respuesta en un tópico que sigues";
        String message = "Se ha añadido una nueva respuesta al tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " que sigues.";

        for (TopicFollow follower : topic.getFollowedTopics()) {
            if (!follower.getUser().getUsername().equals(user.getUsername())) {
                createNotification(follower.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.REPLY);
            }
        }
    }

    @Transactional
    public void notifyFollowersTopicSolved(Topic topic)  {
        String title = "Un tópico que sigues ha sido marcado como solucionado";
        String message = "El tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " que sigues ha sido marcado como solucionado.";

        for (TopicFollow follower : topic.getFollowedTopics()) {
            createNotification(follower.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.SOLVED);
        }
    }

    private void createNotification(User user, Topic topic, Response response,String title, String message, Notification.Type type, Notification.Subtype subtype) {
        Notification notification = new Notification(user, topic, response, title, message, type, subtype);
        notificationRepository.save(notification);
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private void checkModificationPermission(Notification notification) {
        User user = getAuthenticatedUser();
        if (!notification.getUser().equals(getAuthenticatedUser())) {
            log.warn("Usuario con ID: {} intentó modificar notificación que no le pertenece (ID: {})", user.getId(), notification.getId());
            throw new AppException("No tienes permiso para eliminar esta notificación", HttpStatus.FORBIDDEN);
        }
    }

    private Notification findNotificationById(Long notifyId) {
        return notificationRepository.findById(notifyId)
                .orElseThrow(() -> {
                    log.warn("Notificación no encontrada con ID: {}", notifyId);
                    return new AppException("Notificación no encontrada", HttpStatus.NOT_FOUND);
                });
    }



}
