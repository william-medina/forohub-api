package com.williammedina.forohub.domain.topicfollow;

import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.forohub.domain.user.User;
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
public class TopicFollowService {

    private final TopicFollowRepository topicFollowRepository;
    private final CommonHelperService commonHelperService;

    @Transactional
    public TopicFollowDetailsDTO toggleFollowTopic(Long topicId) {
        User user = getAuthenticatedUser();
        Topic topic = findTopicById(topicId);

        if (user.getUsername().equals(topic.getUser().getUsername())) {
            log.warn("El usuario ID: {} intentó seguir su propio tópico con ID {}", user.getId(), topicId);
            throw new AppException("No puedes seguir un tópico que has creado." , HttpStatus.CONFLICT);
        }

        boolean isFollowing = topicFollowRepository.existsByUserIdAndTopicId(user.getId(), topic.getId());

        if (isFollowing) {
            topicFollowRepository.deleteByUserIdAndTopicId(user.getId(), topicId);
            log.info("Usuario ID: {} dejó de seguir el tópico con ID {}", user.getId(), topicId);
            return new TopicFollowDetailsDTO(toTopicDTO(topic), null);
        } else {
            TopicFollow newFollow = topicFollowRepository.save(new TopicFollow(user, topic));
            log.info("Usuario ID: {} comenzó a seguir el tópico con ID {}", user.getId(), topicId);
            return new TopicFollowDetailsDTO(toTopicDTO(newFollow.getTopic()), newFollow.getFollowedAt());
        }

    }


    @Transactional(readOnly = true)
    public Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Pageable pageable, String keyword) {
        User user = getAuthenticatedUser();
        log.debug("Consultando tópicos seguidos por el usuario ID: {}", user.getId());

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
