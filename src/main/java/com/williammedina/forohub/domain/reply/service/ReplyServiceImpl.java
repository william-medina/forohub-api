package com.williammedina.forohub.domain.reply.service;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import com.williammedina.forohub.domain.notification.service.NotificationService;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.reply.dto.CreateReplyDTO;
import com.williammedina.forohub.domain.reply.dto.ReplyDTO;
import com.williammedina.forohub.domain.reply.dto.UpdateReplyDTO;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
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
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final CommonHelperService commonHelperService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ContentValidationService contentValidationService;


    @Override
    @Transactional
    public ReplyDTO createReply(CreateReplyDTO data) throws MessagingException {
        UserEntity user = getAuthenticatedUser();
        TopicEntity topic = findTopicById(data.topicId());
        log.info("User ID: {} creating reply for topic ID: {}", user.getId(), topic.getId());

        isTopicClosed(topic);
        validateReplyContent(data.content()); // Validate the reply content using AI

        ReplyEntity reply = replyRepository.save(new ReplyEntity(user, topic, data.content()));
        log.info("Reply created with ID: {} by user ID: {}", reply.getId(), user.getId());

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            log.debug("Notifying topic owner ID: {} about new reply", topic.getId());
            notificationService.notifyTopicReply(topic, user);
            emailService.notifyTopicReply(topic, user);
        }

        Hibernate.initialize(topic.getFollowedTopics());
        log.debug("Notifying followers of topic ID: {}", topic.getId());
        notificationService.notifyFollowersTopicReply(topic, user);
        emailService.notifyFollowersTopicReply(topic, user);

        return ReplyDTO.fromEntity(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReplyDTO> getAllRepliesByUser(Pageable pageable) {
        UserEntity user = getAuthenticatedUser();
        log.debug("Fetching replies for user ID: {}", user.getId());
        return replyRepository.findByUserSortedByCreationDate(user, pageable).map(ReplyDTO::fromEntity);
    }

    @Override
    @Transactional
    public ReplyDTO updateReply(UpdateReplyDTO data, Long replyId) throws MessagingException {
        ReplyEntity reply = findReplyById(replyId);
        UserEntity user = checkModificationPermission(reply);
        log.info("User ID: {} updating reply ID: {}", user.getId(), replyId);

        validateReplyContent(data.content()); // Validate the updated reply content using AI

        reply.setContent(data.content());
        ReplyEntity updatedReply = replyRepository.save(reply);

        if(!user.getUsername().equals(reply.getUser().getUsername())) {
            log.debug("Notifying reply owner ID: {}", replyId);
            notificationService.notifyReplyEdited(reply);
            emailService.notifyReplyEdited(reply);
        }
        return ReplyDTO.fromEntity(updatedReply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) throws MessagingException {
        ReplyEntity reply = findReplyById(replyId);
        UserEntity user = checkModificationPermission(reply);
        if (reply.getSolution()) {
           throw new AppException("No puedes eliminar una respuesta marcada como solución", HttpStatus.CONFLICT);
        }

        reply.setIsDeleted(true); //replyRepository.delete(reply);
        log.info("Reply ID: {} marked as deleted by user ID: {}", replyId, user.getId());

        if(!user.getUsername().equals(reply.getUser().getUsername())) {
            log.debug("Notifying reply owner ID: {} about deletion", replyId);
            notificationService.notifyReplyDeleted(reply);
            emailService.notifyReplyDeleted(reply);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyDTO getReplyById(Long replyId) {
        log.debug("Fetching reply ID: {}", replyId);
        ReplyEntity reply = findReplyById(replyId);
        return ReplyDTO.fromEntity(reply);
    }

    @Override
    @Transactional
    public ReplyDTO setCorrectReply(Long replyId) throws MessagingException {
        UserEntity user = getAuthenticatedUser();
        if (!user.hasElevatedPermissions()) {
            log.warn("User ID: {} attempted to modify topic state without permission for reply ID: {}", user.getId(), replyId);
            throw new AppException("No tienes permiso para modificar el estado de la respuesta", HttpStatus.FORBIDDEN);
        }

        ReplyEntity reply = findReplyById(replyId);
        log.info("User ID: {} changing state of reply ID: {}", user.getId(), replyId);
        List<ReplyEntity> replies = replyRepository.findByTopicId(reply.getTopic().getId());

        boolean isCurrentlySolution = reply.getSolution();
        replies.forEach(re -> re.setSolution(false)); // Deactivate all solutions

        if (!isCurrentlySolution) {
            reply.setSolution(true);
            reply.getTopic().setStatus(TopicEntity.Status.CLOSED);
            log.info("Reply ID: {} marked as solution for topic ID: {}", reply.getId(), reply.getTopic().getId());
        } else {
            reply.getTopic().setStatus(TopicEntity.Status.ACTIVE);
            log.info("Reply ID: {} unmarked as solution for topic ID: {}", reply.getId(), reply.getTopic().getId());
        }

        replyRepository.saveAll(replies);

        if(reply.getSolution()) {
            log.debug("Sending notifications for reply solution");
            notificationService.notifyTopicSolved(reply.getTopic());
            notificationService.notifyReplySolved(reply, reply.getTopic());
            notificationService.notifyFollowersTopicSolved(reply.getTopic());
            emailService.notifyTopicSolved(reply.getTopic());
            emailService.notifyReplySolved(reply, reply.getTopic());
            emailService.notifyFollowersTopicSolved(reply.getTopic());
        }

        return ReplyDTO.fromEntity(reply);
    }

    private UserEntity getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private TopicEntity findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private ReplyEntity findReplyById(Long replyId) {
        return replyRepository.findByIdAndIsDeletedFalse(replyId)
                .orElseThrow(() ->  {
                    log.error("Reply not found with ID: {}", replyId);
                    return new AppException("Respuesta no encontrada", HttpStatus.NOT_FOUND);
                });
    }

    private UserEntity checkModificationPermission(ReplyEntity reply) {
        UserEntity user = getAuthenticatedUser();
        // If the user is the owner OR has elevated permissions, they are allowed to modify the reply
        if (!reply.getUser().equals(getAuthenticatedUser()) && !getAuthenticatedUser().hasElevatedPermissions()) {
            log.warn("User ID: {} without permissions attempted to modify reply ID: {}", user.getId(), reply.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta respuesta", HttpStatus.FORBIDDEN);
        }

        return user;
    }

    private void isTopicClosed(TopicEntity topic) {
        if(topic.isTopicClosed()) {
            log.warn("Attempt to reply to closed topic ID: {}", topic.getId());
            throw new AppException("No se puede crear una respuesta. El tópico está cerrado.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateReplyContent(String content) {
        String validationResult = contentValidationService.validateContent(content);
        if (!"approved".equals(validationResult)) {
            log.warn("Reply content not approved: {}", validationResult);
            throw new AppException("La respuesta " + validationResult, HttpStatus.FORBIDDEN);
        }
    }

}
