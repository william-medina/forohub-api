package com.williammedina.forohub.domain.topic.service;

import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDetailsDTO;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TopicService {

    TopicDTO createTopic(InputTopicDTO data);
    Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, TopicEntity.Status status);
    Page<TopicDTO> getAllTopicsByUser(Pageable pageable, String keyword);
    TopicDetailsDTO getTopicById(Long topicId);
    TopicDetailsDTO updateTopic(@Valid InputTopicDTO data, Long topicId) throws MessagingException;
    void deleteTopic(Long topicId) throws MessagingException;

}
