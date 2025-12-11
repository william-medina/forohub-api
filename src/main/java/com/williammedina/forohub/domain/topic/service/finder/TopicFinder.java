package com.williammedina.forohub.domain.topic.service.finder;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;

public interface TopicFinder {

    TopicEntity findTopicById(Long topicId);

}
