package com.williammedina.forohub.domain.notification;

import com.williammedina.forohub.domain.helper.CommonHelperService;
import com.williammedina.forohub.domain.notification.dto.NotificationDTO;
import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topicfollow.TopicFollow;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.errors.AppException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CommonHelperService commonHelperService;

    public NotificationService(NotificationRepository notificationRepository, CommonHelperService commonHelperService) {
        this.notificationRepository = notificationRepository;
        this.commonHelperService = commonHelperService;
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotificationsByUser() {
        User user = getAuthenticatedUser();
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream().map(this::toNotificationDTO).toList();
    }

    @Transactional
    public void deleteNotification(Long notifyId) {
        Notification notification = findNotificationById(notifyId);
        checkModificationPermission(notification);

        notificationRepository.delete(notification);
    }

    @Transactional
    public NotificationDTO markNotificationAsRead(Long notifyId) {
        Notification notification = findNotificationById(notifyId);
        checkModificationPermission(notification);

        notification.setIsRead(true);
        return toNotificationDTO(notification);
    }

    @Transactional
    public void notifyTopicReply(Topic topic, User user) {
        String title = "Nueva respuesta a tu tópico";
        String message = "Tu tópico ha recibido una nueva respuesta. "
                + user.getUsername() + " respondió al tópico '"
                + topic.getTitle() + "' del curso: " + topic.getCourse().getName();

        createNotification(topic.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.REPLY);
    }

    @Transactional
    public void notifyTopicSolved(Topic topic) {
        String title = "Tu tópico ha sido marcado como solucionado";
        String message = "Tu tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido marcado como solucionado.";

        createNotification(topic.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.SOLVED);
    }

    @Transactional
    public void notifyTopicEdited(Topic topic) {
        String title = "Tu tópico ha sido editado";
        String message = "Se ha realizado cambios en tu tópico titulado '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(topic.getUser(), topic, null, title, message, Notification.Type.TOPIC, Notification.Subtype.EDITED);
    }

    @Transactional
    public void notifyTopicDeleted(Topic topic) {
        String title = "Tu tópico ha sido eliminado";
        String message = "Lamentamos informarte que tu tópico titulado '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido eliminado. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(topic.getUser(), null, null, title, message, Notification.Type.TOPIC, Notification.Subtype.DELETED);
    }

    @Transactional
    public void notifyResponseSolved(Response response, Topic topic) {
        String title = "Tu respuesta ha sido marcada como solución";
        String message = "Tu respuesta en el tópico '" + response.getTopic().getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido marcada como solución.";

        createNotification(response.getUser(), topic, response, title, message, Notification.Type.RESPONSE, Notification.Subtype.SOLVED);
    }

    @Transactional
    public void notifyResponseEdited(Response response) {
        String title = "Tu respuesta ha sido editada";
        String message = "Se han realizado cambios en tu respuesta del tópico '" + response.getTopic().getTitle() + "' del curso: " + response.getTopic().getCourse().getName() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(response.getUser(), response.getTopic(), response, title, message, Notification.Type.RESPONSE, Notification.Subtype.EDITED);
    }

    @Transactional
    public void notifyResponseDeleted(Response response) {
        String title = "Tu respuesta ha sido eliminada";
        String message = "Lamentamos informarte que tu respuesta del tópico '" + response.getTopic().getTitle() + "' del curso: " + response.getTopic().getCourse().getName() + " ha sido eliminada. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(response.getUser(), response.getTopic(), null, title, message, Notification.Type.RESPONSE, Notification.Subtype.DELETED);
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
        if (!notification.getUser().equals(getAuthenticatedUser())) {
            throw new AppException("No tienes permiso para eliminar esta notificación", "FORBIDDEN");
        }
    }

    private Notification findNotificationById(Long notifyId) {
        return notificationRepository.findById(notifyId)
                .orElseThrow(() -> new AppException("Notificación no encontrada", "NOT_FOUND"));
    }

    private NotificationDTO toNotificationDTO(Notification notification) {

        // Long topicId = (notification.getTopic() != null) ? notification.getTopic().getId() : null;
        Long topicId = (notification.getTopic() == null) ? null : notification.getTopic().getId();

        return new NotificationDTO(
                notification.getId(),
                notification.getUser().getUsername(),
                topicId,
                notification.getType(),
                notification.getSubtype(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }

}
