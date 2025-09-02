package com.williammedina.forohub.domain.topicfollow.service;

import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TopicFollowService {

    TopicFollowDetailsDTO toggleFollowTopic(Long topicId);
    Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Pageable pageable, String keyword);

}
