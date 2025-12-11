package com.williammedina.forohub.domain.reply.service.validator;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;

public interface ReplyValidator {

    void validateContent(String content);
    void checkTopicClosed(TopicEntity topic);

}
