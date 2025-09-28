package com.williammedina.forohub.domain.topicfollow.service;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.forohub.domain.topicfollow.repository.TopicFollowRepository;
import com.williammedina.forohub.domain.user.entity.UserEntity;
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

    private final TopicFollowRepository topicFollowRepository;
    private final CommonHelperService commonHelperService;

    @Override
    @Transactional
    public TopicFollowDetailsDTO toggleFollowTopic(Long topicId) {
        UserEntity user = getAuthenticatedUser();
        TopicEntity topic = findTopicById(topicId);

        if (user.getUsername().equals(topic.getUser().getUsername())) {
            log.warn("User ID: {} attempted to follow their own topic with ID: {}", user.getId(), topicId);
            throw new AppException("No puedes seguir un tópico que has creado." , HttpStatus.CONFLICT);
        }

        boolean isFollowing = topicFollowRepository.existsByUserIdAndTopicId(user.getId(), topic.getId());

        if (isFollowing) {
            topicFollowRepository.deleteByUserIdAndTopicId(user.getId(), topicId);
            log.info("User ID: {} unfollowed topic ID: {}", user.getId(), topicId);
            return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topic), null);
        } else {
            TopicFollowEntity newFollow = topicFollowRepository.save(new TopicFollowEntity(user, topic));
            log.info("User ID: {} followed topic ID: {}", user.getId(), topicId);
            return new TopicFollowDetailsDTO(TopicDTO.fromEntity(newFollow.getTopic()), newFollow.getFollowedAt());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Pageable pageable, String keyword) {
        UserEntity user = getAuthenticatedUser();
        log.debug("Fetching followed topics for user ID: {}", user.getId());

        if (keyword != null ) {
            return topicFollowRepository.findByUserFilters(user, keyword, pageable).map(this::toTopicFollowDetailsDTO);
        }
        return topicFollowRepository.findByUserSortedByCreationDate(user, pageable).map(this::toTopicFollowDetailsDTO);
    }

    private UserEntity getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private TopicEntity findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private TopicFollowDetailsDTO toTopicFollowDetailsDTO(TopicFollowEntity topicFollow) {
        return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topicFollow.getTopic()), topicFollow.getFollowedAt());
    }
}
