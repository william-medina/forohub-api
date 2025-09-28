package com.williammedina.forohub.domain.topic.service;

import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.notification.service.NotificationService;
import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDetailsDTO;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.reply.dto.ReplyDTO;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.email.EmailService;
import com.williammedina.forohub.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final CommonHelperService commonHelperService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ContentValidationService contentValidationService;

    @Override
    @Transactional
    public TopicDTO createTopic(InputTopicDTO data) {
        UserEntity user = getAuthenticatedUser();
        log.info("Creating topic by user ID: {}", user.getId());

        existsByTitle(data.title());
        existsByDescription(data.description());

        // Validate the title and description content using AI
        validateTitleContent(data.title());
        validateDescriptionContent(data.description());

        CourseEntity course = findCourseById(data.courseId());

        TopicEntity topic = topicRepository.save(new TopicEntity(user, data.title(), data.description(), course));
        log.info("Topic created with ID: {} for course ID: {} by user ID: {}", topic.getId(), course.getId(), user.getId());

        return TopicDTO.fromEntity(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, TopicEntity.Status status) {
        log.debug("Fetching topics - page: {}, size: {}, courseId: {}, keyword: {}, status: {}",
                pageable.getPageNumber(), pageable.getPageSize(), courseId, keyword, status);

        if (courseId != null || keyword != null || status != null) {
            return topicRepository.findByFilters(courseId, keyword, status, pageable).map(TopicDTO::fromEntity);
        }
        return topicRepository.findAllSortedByCreationDate(pageable).map(TopicDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopicsByUser(Pageable pageable, String keyword) {
        UserEntity user = getAuthenticatedUser();
        log.debug("Fetching topics for user ID: {} - keyword: {}", user.getId(), keyword);

        if (keyword != null ) {
            return topicRepository.findByUserFilters(user, keyword, pageable).map(TopicDTO::fromEntity);
        }
        return topicRepository.findByUserSortedByCreationDate(user, pageable).map(TopicDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDetailsDTO getTopicById(Long topicId) {
        log.debug("Fetching topic details with ID: {}", topicId);
        TopicEntity topic = findTopicById(topicId);
        List<ReplyDTO> replies = topic.getReplies().stream()
                .sorted(Comparator.comparing(ReplyEntity::getCreatedAt))
                .map(ReplyDTO::fromEntity)
                .toList();

        return TopicDetailsDTO.fromEntity(topic, replies);
    }

    @Override
    @Transactional
    public TopicDetailsDTO updateTopic(InputTopicDTO data, Long topicId) throws MessagingException {
        TopicEntity topic = findTopicById(topicId);
        UserEntity user = checkModificationPermission(topic);
        log.info("Updating topic ID: {} by user ID: {}", topicId, user.getId());

        CourseEntity course = findCourseById(data.courseId());

        if(!data.title().equals(topic.getTitle())) {
            existsByTitle(data.title());
            validateTitleContent(data.title()); // Validate the new title content using AI
        }

        if(!data.description().equals(topic.getDescription())) {
            existsByDescription(data.description());
            validateDescriptionContent(data.description()); // Validate the new description content using AI
        }

        topic.setTitle(data.title());
        topic.setDescription(data.description());
        topic.setCourse(course);

        TopicEntity updatedTopic = topicRepository.save(topic);
        log.info("Topic updated ID: {} by user ID: {}", updatedTopic.getId(), user.getId());

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            log.debug("Notifying topic owner ID: {} about update", topic.getId());
            emailService.notifyTopicEdited(topic);
            notificationService.notifyTopicEdited(topic);
        }

        List<ReplyDTO> replies = updatedTopic.getReplies().stream()
                .filter(response -> !response.getIsDeleted())
                .map(ReplyDTO::fromEntity)
                .toList();

        return TopicDetailsDTO.fromEntity(updatedTopic, replies);
    }

    @Override
    @Transactional
    public void deleteTopic(Long topicId) throws MessagingException {
        TopicEntity topic = findTopicById(topicId);
        UserEntity user = checkModificationPermission(topic);
        topic.setIsDeleted(true); //topicRepository.delete(topic);
        log.info("Topic marked as deleted - ID: {} by user ID: {}", topicId, user.getId());

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            log.debug("Notifying topic owner ID: {} about deletion", topicId);
            notificationService.notifyTopicDeleted(topic);
            emailService.notifyTopicDeleted(topic);
        }
    }

    private UserEntity getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private TopicEntity findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private CourseEntity findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found with ID: {}", courseId);
                    return new AppException("Curso no encontrado.", HttpStatus.NOT_FOUND);
                });
    }

    private UserEntity checkModificationPermission(TopicEntity topic) {
        UserEntity user = getAuthenticatedUser();
        // If the user is the owner OR has elevated permissions, they are allowed to modify the topic
        if (!topic.getUser().equals(getAuthenticatedUser()) && !getAuthenticatedUser().hasElevatedPermissions()) {
            log.warn("User ID: {} attempted to modify topic ID: {} without permission", user.getId(), topic.getId());
            throw new AppException("No tienes permiso para realizar cambios en este tópico", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private void existsByTitle(String title) {
        if (topicRepository.existsByTitle(title)) {
            log.warn("Topic already exists with title: {}", title);
            throw new AppException("El titulo ya existe.", HttpStatus.CONFLICT);
        }
    }

    private void existsByDescription(String description) {
        if (topicRepository.existsByDescription(description)) {
            log.warn("Topic already exists with description: {}", description);
            throw new AppException("La descripción ya existe.", HttpStatus.CONFLICT);
        }
    }

    private void validateTitleContent(String title) {
        String validationResult = contentValidationService.validateContent(title);
        if (!"approved".equals(validationResult)) {
            log.warn("Title content not approved: {}", validationResult);
            throw new AppException("El título " + validationResult, HttpStatus.FORBIDDEN);
        }
    }


    private void validateDescriptionContent(String description) {
        String validationResult = contentValidationService.validateContent(description);
        if (!"approved".equals(validationResult)) {
            log.warn("Description content not approved: {}", validationResult);
            throw new AppException("La descripción " + validationResult, HttpStatus.FORBIDDEN);
        }
    }

}
