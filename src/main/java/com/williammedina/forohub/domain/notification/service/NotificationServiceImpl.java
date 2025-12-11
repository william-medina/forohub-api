package com.williammedina.forohub.domain.notification.service;

import com.williammedina.forohub.domain.notification.dto.NotificationDTO;
import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import com.williammedina.forohub.domain.notification.repository.NotificationRepository;
import com.williammedina.forohub.domain.notification.service.finder.NotificationFinder;
import com.williammedina.forohub.domain.notification.service.permission.NotificationPermissionService;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.AuthenticatedUserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final NotificationRepository notificationRepository;
    private final NotificationFinder notificationFinder;
    private final NotificationPermissionService notificationPermissionService;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotificationsByUser() {
        UserEntity user = authenticatedUserProvider.getAuthenticatedUser();
        log.debug("Fetching all notifications for user ID: {}", user.getId());
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream().map(NotificationDTO::fromEntity).toList();
    }

    @Override
    @Transactional
    public void deleteNotification(Long notifyId) {
        NotificationEntity notification = notificationFinder.findNotificationById(notifyId);
        notificationPermissionService.checkModificationPermission(notification);

        notificationRepository.delete(notification);
        log.info("Notification ID: {} deleted by user ID: {}", notifyId, authenticatedUserProvider.getAuthenticatedUser().getId());
    }

    @Override
    @Transactional
    public NotificationDTO markNotificationAsRead(Long notifyId) {
        NotificationEntity notification = notificationFinder.findNotificationById(notifyId);
        notificationPermissionService.checkModificationPermission(notification);

        notification.setIsRead(true);
        log.debug("Notification ID: {} marked as read by user ID: {}", notifyId, authenticatedUserProvider.getAuthenticatedUser().getId());

        return NotificationDTO.fromEntity(notification);
    }

    @Override
    @Transactional
    public void notifyTopicReply(TopicEntity topic, UserEntity user) {
        String title = "Nueva respuesta a tu tópico";
        String message = "Tu tópico ha recibido una nueva respuesta. "
                + user.getUsername() + " respondió al tópico '"
                + topic.getTitle() + "' del curso: " + topic.getCourse().getName();

        createNotification(topic.getUser(), topic, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.REPLY);
        log.debug("Reply notification created for topic ID: {} to user ID: {}", topic.getId(), topic.getUser().getId());
    }

    @Override
    @Transactional
    public void notifyTopicSolved(TopicEntity topic) {
        String title = "Tu tópico ha sido marcado como solucionado";
        String message = "Tu tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido marcado como solucionado.";

        createNotification(topic.getUser(), topic, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.SOLVED);
        log.debug("Topic solved notification created for topic ID: {} to user ID: {}", topic.getId(), topic.getUser().getId());
    }

    @Override
    @Transactional
    public void notifyTopicEdited(TopicEntity topic) {
        String title = "Tu tópico ha sido editado";
        String message = "Se ha realizado cambios en tu tópico titulado '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(topic.getUser(), topic, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.EDITED);
        log.debug("Topic edited notification created for topic ID: {} to user ID: {}", topic.getId(), topic.getUser().getId());
    }

    @Override
    @Transactional
    public void notifyTopicDeleted(TopicEntity topic) {
        String title = "Tu tópico ha sido eliminado";
        String message = "Lamentamos informarte que tu tópico titulado '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido eliminado. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(topic.getUser(), null, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.DELETED);
        log.debug("Topic deleted notification created for user ID: {} for deleted topic ID: {}", topic.getUser().getId(), topic.getId());
    }

    @Override
    @Transactional
    public void notifyReplySolved(ReplyEntity reply, TopicEntity topic) {
        String title = "Tu respuesta ha sido marcada como solución";
        String message = "Tu respuesta en el tópico '" + reply.getTopic().getTitle() + "' del curso: " + topic.getCourse().getName() + " ha sido marcada como solución.";

        createNotification(reply.getUser(), topic, reply, title, message, NotificationEntity.Type.REPLY, NotificationEntity.Subtype.SOLVED);
        log.debug("Reply solved notification created for user ID: {}", reply.getUser().getId());
    }

    @Override
    @Transactional
    public void notifyReplyEdited(ReplyEntity reply) {
        String title = "Tu respuesta ha sido editada";
        String message = "Se han realizado cambios en tu respuesta del tópico '" + reply.getTopic().getTitle() + "' del curso: " + reply.getTopic().getCourse().getName() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(reply.getUser(), reply.getTopic(), reply, title, message, NotificationEntity.Type.REPLY, NotificationEntity.Subtype.EDITED);
        log.debug("Reply edited notification created for user ID: {}", reply.getUser().getId());
    }

    @Override
    @Transactional
    public void notifyReplyDeleted(ReplyEntity reply) {
        String title = "Tu respuesta ha sido eliminada";
        String message = "Lamentamos informarte que tu respuesta del tópico '" + reply.getTopic().getTitle() + "' del curso: " + reply.getTopic().getCourse().getName() + " ha sido eliminada. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(reply.getUser(), reply.getTopic(), null, title, message, NotificationEntity.Type.REPLY, NotificationEntity.Subtype.DELETED);
        log.debug("Reply deleted notification created for user ID: {}", reply.getUser().getId());
    }

    @Override
    @Transactional
    public void notifyFollowersTopicReply(TopicEntity topic, UserEntity user) {
        String title = "Nueva respuesta en un tópico que sigues";
        String message = "Se ha añadido una nueva respuesta al tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " que sigues.";

        for (TopicFollowEntity follower : topic.getFollowedTopics()) {
            if (!follower.getUser().getUsername().equals(user.getUsername())) {
                createNotification(follower.getUser(), topic, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.REPLY);
            }
        }
    }

    @Override
    @Transactional
    public void notifyFollowersTopicSolved(TopicEntity topic)  {
        String title = "Un tópico que sigues ha sido marcado como solucionado";
        String message = "El tópico '" + topic.getTitle() + "' del curso: " + topic.getCourse().getName() + " que sigues ha sido marcado como solucionado.";

        for (TopicFollowEntity follower : topic.getFollowedTopics()) {
            createNotification(follower.getUser(), topic, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.SOLVED);
        }
    }

    private void createNotification(UserEntity user, TopicEntity topic, ReplyEntity response, String title, String message, NotificationEntity.Type type, NotificationEntity.Subtype subtype) {
        NotificationEntity notification = new NotificationEntity(user, topic, response, title, message, type, subtype);
        notificationRepository.save(notification);
    }

}
