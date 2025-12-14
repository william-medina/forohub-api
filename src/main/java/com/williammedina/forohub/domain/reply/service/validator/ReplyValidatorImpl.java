package com.williammedina.forohub.domain.reply.service.validator;

import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyValidatorImpl implements ReplyValidator{

    private final ContentValidationService contentValidationService;

    @Override
    public void ensureReplyContentIsValid(String content) {
        String validationResult = contentValidationService.validateContent(content);
        if (!"approved".equals(validationResult)) {
            log.warn("Reply content not approved: {}", validationResult);
            throw new AppException("La respuesta " + validationResult, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void ensureTopicIsOpen(TopicEntity topic) {
        if(topic.isTopicClosed()) {
            log.warn("Attempt to reply to closed topic ID: {}", topic.getId());
            throw new AppException("No se puede crear una respuesta. El tópico está cerrado.", HttpStatus.FORBIDDEN);
        }
    }
}
