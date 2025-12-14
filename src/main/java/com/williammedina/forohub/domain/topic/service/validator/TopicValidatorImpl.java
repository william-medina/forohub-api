package com.williammedina.forohub.domain.topic.service.validator;

import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicValidatorImpl implements TopicValidator {

    private final TopicRepository topicRepository;
    private final ContentValidationService contentValidationService;

    @Override
    public void ensureTitleIsValid(String title) {
        if (topicRepository.existsByTitle(title)) {
            log.warn("Topic already exists with title: {}", title);
            throw new AppException("El título ya existe.", HttpStatus.CONFLICT);
        }

        String result = contentValidationService.validateContent(title);
        if (!"approved".equals(result)) {
            log.warn("Title content not approved: {}", result);
            throw new AppException("El título " + result, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void ensureDescriptionIsValid(String description) {
        if (topicRepository.existsByDescription(description)) {
            log.warn("Topic already exists with description: {}", description);
            throw new AppException("La descripción ya existe.", HttpStatus.CONFLICT);
        }

        String result = contentValidationService.validateContent(description);
        if (!"approved".equals(result)) {
            log.warn("Description content not approved: {}", result);
            throw new AppException("La descripción " + result, HttpStatus.FORBIDDEN);
        }
    }
}
