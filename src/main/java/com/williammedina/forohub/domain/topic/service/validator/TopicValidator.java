package com.williammedina.forohub.domain.topic.service.validator;

public interface TopicValidator {

    void ensureTitleIsValid(String title);
    void ensureDescriptionIsValid(String description);

}
