package com.williammedina.forohub.domain.reply.service;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.domain.reply.service.finder.ReplyFinder;
import com.williammedina.forohub.domain.reply.service.notifier.ReplyNotifier;
import com.williammedina.forohub.domain.reply.service.permission.ReplyPermissionService;
import com.williammedina.forohub.domain.reply.service.validator.ReplyValidator;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.reply.dto.CreateReplyDTO;
import com.williammedina.forohub.domain.reply.dto.ReplyDTO;
import com.williammedina.forohub.domain.reply.dto.UpdateReplyDTO;
import com.williammedina.forohub.domain.topic.service.finder.TopicFinderImpl;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.AuthenticatedUserProvider;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ReplyRepository replyRepository;
    private final ReplyFinder replyFinder;
    private final TopicFinderImpl topicFinder;
    private final ReplyPermissionService replyPermissionService;
    private final ReplyValidator validator;
    private final ReplyNotifier notifier;


    @Override
    @Transactional
    public ReplyDTO createReply(CreateReplyDTO replyRequest) throws MessagingException {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        TopicEntity topicToReply = topicFinder.findTopicById(replyRequest.topicId());
        log.info("User ID: {} creating reply for topic ID: {}", currentUser.getId(), topicToReply.getId());

        validator.checkTopicClosed(topicToReply);
        validator.validateContent(replyRequest.content()); // Validate the reply content using AI

        ReplyEntity newReply = replyRepository.save(new ReplyEntity(currentUser, topicToReply, replyRequest.content()));
        log.info("Reply created with ID: {} by user ID: {}", newReply.getId(), currentUser.getId());

        Hibernate.initialize(topicToReply.getFollowedTopics());
        notifier.notifyNewReply(topicToReply, currentUser);

        return ReplyDTO.fromEntity(newReply);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReplyDTO> getAllRepliesByUser(Pageable pageable) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        log.debug("Fetching replies for user ID: {}", currentUser.getId());
        return replyRepository.findByUserSortedByCreationDate(currentUser, pageable).map(ReplyDTO::fromEntity);
    }

    @Override
    @Transactional
    public ReplyDTO updateReply(UpdateReplyDTO replyRequest, Long replyId) throws MessagingException {
        ReplyEntity replyToUpdate = replyFinder.findReplyById(replyId);
        UserEntity currentUser = replyPermissionService.checkCanModify(replyToUpdate);
        log.info("User ID: {} updating reply ID: {}", currentUser.getId(), replyId);

        validator.validateContent(replyRequest.content()); // Validate the updated reply content using AI

        replyToUpdate.setContent(replyRequest.content());
        ReplyEntity updatedReply = replyRepository.save(replyToUpdate);

        notifier.notifyReplyUpdated(replyToUpdate, currentUser);

        return ReplyDTO.fromEntity(updatedReply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) throws MessagingException {
        ReplyEntity replyToDelete = replyFinder.findReplyById(replyId);
        UserEntity currentUser = replyPermissionService.checkCanModify(replyToDelete);

        replyPermissionService.checkCannotDeleteSolution(replyToDelete);

        replyToDelete.markAsDeleted(); //replyRepository.delete(reply);
        log.info("Reply ID: {} marked as deleted by user ID: {}", replyId, currentUser.getId());

        notifier.notifyReplyDeleted(replyToDelete, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyDTO getReplyById(Long replyId) {
        log.debug("Fetching reply ID: {}", replyId);
        ReplyEntity reply = replyFinder.findReplyById(replyId);
        return ReplyDTO.fromEntity(reply);
    }

    @Override
    @Transactional
    public ReplyDTO setCorrectReply(Long replyId) throws MessagingException {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        replyPermissionService.checkElevatedPermissionsForSolution(currentUser, replyId);

        ReplyEntity replyToSet = replyFinder.findReplyById(replyId);
        log.info("User ID: {} changing state of reply ID: {}", currentUser.getId(), replyId);
        List<ReplyEntity> replies = replyRepository.findByTopicId(replyToSet.getTopic().getId());

        boolean isCurrentlySolution = replyToSet.getSolution();
        replies.forEach(re -> re.setSolution(false)); // Deactivate all solutions

        if (!isCurrentlySolution) {
            replyToSet.setSolution(true);
            replyToSet.getTopic().setStatus(TopicEntity.Status.CLOSED);
            log.info("Reply ID: {} marked as solution for topic ID: {}", replyToSet.getId(), replyToSet.getTopic().getId());
        } else {
            replyToSet.getTopic().setStatus(TopicEntity.Status.ACTIVE);
            log.info("Reply ID: {} unmarked as solution for topic ID: {}", replyToSet.getId(), replyToSet.getTopic().getId());
        }

        replyRepository.saveAll(replies);
        notifier.notifyReplySolution(replyToSet);

        return ReplyDTO.fromEntity(replyToSet);
    }

}
