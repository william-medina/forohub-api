package com.williammedina.forohub.domain.topic.service;

import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.course.service.finder.CourseFinder;
import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDetailsDTO;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.topic.service.finder.TopicFinder;
import com.williammedina.forohub.domain.topic.service.notifier.TopicNotifier;
import com.williammedina.forohub.domain.topic.service.permission.TopicPermissionService;
import com.williammedina.forohub.domain.topic.service.validator.TopicValidator;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicFinder topicFinder;
    private final TopicValidator validator;
    private final TopicNotifier notifier;
    private final CourseFinder courseFinder;
    private final TopicPermissionService topicPermissionService;

    @Override
    @Transactional
    public TopicDTO createTopic(InputTopicDTO topicRequest) {
        UserEntity currentUser = topicPermissionService.getCurrentUser();
        log.info("Creating topic by user ID: {}", currentUser.getId());

        validator.validateTitle(topicRequest.title());
        validator.validateDescription(topicRequest.description());

        CourseEntity course = courseFinder.findCourseById(topicRequest.courseId());

        TopicEntity createdTopic = topicRepository.save(new TopicEntity(currentUser, topicRequest.title(), topicRequest.description(), course));
        log.info("Topic created with ID: {} for course ID: {} by user ID: {}", createdTopic.getId(), course.getId(), currentUser.getId());

        return TopicDTO.fromEntity(createdTopic);
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
        UserEntity currentUser = topicPermissionService.getCurrentUser();
        log.debug("Fetching topics for user ID: {} - keyword: {}", currentUser.getId(), keyword);

        if (keyword != null ) {
            return topicRepository.findByUserFilters(currentUser, keyword, pageable).map(TopicDTO::fromEntity);
        }
        return topicRepository.findByUserSortedByCreationDate(currentUser, pageable).map(TopicDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDetailsDTO getTopicById(Long topicId) {
        log.debug("Fetching topic details with ID: {}", topicId);
        TopicEntity topic = topicFinder.findTopicById(topicId);
        return TopicDetailsDTO.fromEntity(topic);
    }

    @Override
    @Transactional
    public TopicDetailsDTO updateTopic(InputTopicDTO topicRequest, Long topicId) throws MessagingException {
        TopicEntity topicToUpdate = topicFinder.findTopicById(topicId);
        UserEntity currentUser = topicPermissionService.checkCanModify(topicToUpdate);
        log.info("Updating topic ID: {} by user ID: {}", topicId, currentUser.getId());

        CourseEntity course = courseFinder.findCourseById(topicRequest.courseId());

        if (!topicRequest.title().equals(topicToUpdate.getTitle())) {
            validator.validateTitle(topicRequest.title());
        }

        if(!topicRequest.description().equals(topicToUpdate.getDescription())) {
            validator.validateDescription(topicRequest.description());
        }

        topicToUpdate.setTitle(topicRequest.title());
        topicToUpdate.setDescription(topicRequest.description());
        topicToUpdate.setCourse(course);

        TopicEntity updatedTopic = topicRepository.save(topicToUpdate);
        log.info("Topic updated ID: {} by user ID: {}", updatedTopic.getId(), currentUser.getId());

        notifier.notifyTopicEdited(updatedTopic, currentUser);

        return TopicDetailsDTO.fromEntity(updatedTopic);
    }

    @Override
    @Transactional
    public void deleteTopic(Long topicId) throws MessagingException {
        TopicEntity topicToDelete = topicFinder.findTopicById(topicId);
        UserEntity currentUser = topicPermissionService.checkCanModify(topicToDelete);
        topicToDelete.markAsDeleted(); //topicRepository.delete(topic);
        log.info("Topic marked as deleted - ID: {} by user ID: {}", topicId, currentUser.getId());

        notifier.notifyTopicDeleted(topicToDelete, currentUser);
    }

}
