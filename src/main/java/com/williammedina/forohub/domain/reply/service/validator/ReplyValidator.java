package com.williammedina.forohub.domain.reply.service.validator;

import com.williammedina.forohub.domain.topic.entity.TopicEntity;

public interface ReplyValidator {

    void ensureReplyContentIsValid(String content);
    void ensureTopicIsOpen(TopicEntity topic);

}
