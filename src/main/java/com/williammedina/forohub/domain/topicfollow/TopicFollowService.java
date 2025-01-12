package com.williammedina.forohub.domain.topicfollow;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.errors.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicFollowService {

    private final TopicFollowRepository topicFollowRepository;
    private final CommonHelperService commonHelperService;

    public TopicFollowService(
            TopicFollowRepository topicFollowRepository,
            CommonHelperService commonHelperService
    ) {
        this.topicFollowRepository = topicFollowRepository;
        this.commonHelperService = commonHelperService;
    }

    @Transactional
    public TopicFollowDetailsDTO toggleFollowTopic(Long topicId) {
        User user = getAuthenticatedUser();
        Topic topic = findTopicById(topicId);

        if (user.getUsername().equals(topic.getUser().getUsername())) {
            throw new AppException("No puedes seguir un tópico que has creado." , "CONFLICT");
        }

        boolean isFollowing = topicFollowRepository.existsByUserIdAndTopicId(user.getId(), topic.getId());

        if (isFollowing) {
            topicFollowRepository.deleteByUserIdAndTopicId(user.getId(), topicId);
            return new TopicFollowDetailsDTO(toTopicDTO(topic), null);
        } else {
            TopicFollow newFollow = topicFollowRepository.save(new TopicFollow(user, topic));
            return new TopicFollowDetailsDTO(toTopicDTO(newFollow.getTopic()), newFollow.getFollowedAt());
        }

    }


    @Transactional(readOnly = true)
    public Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Pageable pageable, String keyword) {
        User user = getAuthenticatedUser();
        if (keyword != null ) {
            return topicFollowRepository.findByUserFilters(user, keyword, pageable).map(this::toTopicFollowDetailsDTO);
        }
        return topicFollowRepository.findByUserSortedByCreationDate(user, pageable).map(this::toTopicFollowDetailsDTO);
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private Topic findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private TopicDTO toTopicDTO(Topic topic) {
        return commonHelperService.toTopicDTO(topic);
    }

    private TopicFollowDetailsDTO toTopicFollowDetailsDTO(TopicFollow topicFollow) {
        return new TopicFollowDetailsDTO(toTopicDTO(topicFollow.getTopic()), topicFollow.getFollowedAt());
    }
}
