package com.williammedina.forohub.domain.user.service.stats;

import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.topicfollow.repository.TopicFollowRepository;
import com.williammedina.forohub.domain.user.dto.UserStatsDTO;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.service.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService{

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TopicRepository topicRepository;
    private final ReplyRepository replyRepository;
    private final TopicFollowRepository topicFollowRepository;

    @Override
    @Transactional(readOnly = true)
    public UserStatsDTO getUserStats() {
        UserEntity currentUser = authenticatedUserProvider.getAuthenticatedUser();
        log.debug("Fetching stats for user ID: {}", currentUser.getId());

        long topicsCount = topicRepository.countByUserId(currentUser.getId());
        long repliesCount = replyRepository.countByUserId(currentUser.getId());
        long followedTopicsCount = topicFollowRepository.countByUserId(currentUser.getId());
        return new UserStatsDTO(topicsCount, repliesCount, followedTopicsCount);
    }
}
