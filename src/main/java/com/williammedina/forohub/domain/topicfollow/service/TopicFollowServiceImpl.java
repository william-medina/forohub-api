package com.williammedina.forohub.domain.topicfollow.service;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topic.service.finder.TopicFinder;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.forohub.domain.topicfollow.repository.TopicFollowRepository;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.context.AuthenticatedUserProvider;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TopicFollowServiceImpl implements TopicFollowService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TopicFollowRepository topicFollowRepository;
    private final TopicFinder topicFinder;

    @Override
    @Transactional
    public TopicFollowDetailsDTO toggleFollowTopic(Long topicId) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        TopicEntity topic = topicFinder.findTopicById(topicId);

        if (currentUser.equals(topic.getUser())) {
            log.warn("User ID: {} attempted to follow their own topic with ID: {}", currentUser.getId(), topicId);
            throw new AppException("No puedes seguir un tópico que has creado." , HttpStatus.CONFLICT);
        }

        boolean alreadyFollowing = topicFollowRepository.existsByUserIdAndTopicId(currentUser.getId(), topic.getId());

        if (alreadyFollowing) {
            topicFollowRepository.deleteByUserIdAndTopicId(currentUser.getId(), topicId);
            log.info("User ID: {} unfollowed topic ID: {}", currentUser.getId(), topicId);
            return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topic), null);
        }

        TopicFollowEntity newFollow = topicFollowRepository.save(new TopicFollowEntity(currentUser, topic));
        log.info("User ID: {} followed topic ID: {}", currentUser.getId(), topicId);
        return new TopicFollowDetailsDTO(TopicDTO.fromEntity(newFollow.getTopic()), newFollow.getFollowedAt());

    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Pageable pageable, String keyword) {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        log.debug("Fetching followed topics for user ID: {}", currentUser.getId());

        if (keyword != null ) {
            return topicFollowRepository.findByUserFilters(currentUser, keyword, pageable).map(TopicFollowDetailsDTO::fromEntity);
        }
        return topicFollowRepository.findByUserSortedByCreationDate(currentUser, pageable).map(TopicFollowDetailsDTO::fromEntity);
    }

}
