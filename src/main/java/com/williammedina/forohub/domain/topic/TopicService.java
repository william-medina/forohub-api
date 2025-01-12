package com.williammedina.forohub.domain.topic;

import com.williammedina.forohub.domain.common.ContentValidationService;
import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.course.CourseRepository;
import com.williammedina.forohub.domain.course.dto.CourseDTO;
import com.williammedina.forohub.domain.common.CommonHelperService;
import com.williammedina.forohub.domain.notification.NotificationService;
import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDTO;
import com.williammedina.forohub.domain.topic.dto.TopicDetailsDTO;
import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.response.dto.ResponseDTO;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.domain.user.dto.AuthorDTO;
import com.williammedina.forohub.domain.user.dto.UserDTO;
import com.williammedina.forohub.infrastructure.email.EmailService;
import com.williammedina.forohub.infrastructure.errors.AppException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;


@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final CommonHelperService commonHelperService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ContentValidationService contentValidationService;

    public TopicService(
            TopicRepository topicRepository,
            CourseRepository courseRepository,
            CommonHelperService commonHelperService,
            NotificationService notificationService,
            EmailService emailService,
            ContentValidationService contentValidationService
    ) {
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
        this.commonHelperService = commonHelperService;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.contentValidationService = contentValidationService;
    }

    @Transactional
    public TopicDTO createTopic(InputTopicDTO data) {
        User user = getAuthenticatedUser();
        existsByTitle(data.title());
        existsByDescription(data.description());

        // Validar el contenido del título y la descripción con IA
        validateTitleContent(data.title());
        validateDescriptionContent(data.description());

        Course course = findCourseById(data.courseId());

        Topic topic = topicRepository.save(new Topic(user, data.title(), data.description(), course));
        return toTopicDTO(topic);
    }

    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, Topic.Status status) {
        if (courseId != null || keyword != null || status != null) {
            return topicRepository.findByFilters(courseId, keyword, status, pageable).map(this::toTopicDTO);
        }
        return topicRepository.findAllSortedByCreationDate(pageable).map(this::toTopicDTO);
    }

    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopicsByUser(Pageable pageable, String keyword) {
        User user = getAuthenticatedUser();
        if (keyword != null ) {
            return topicRepository.findByUserFilters(user, keyword, pageable).map(this::toTopicDTO);
        }
        return topicRepository.findByUserSortedByCreationDate(user, pageable).map(this::toTopicDTO);
    }

    @Transactional(readOnly = true)
    public TopicDetailsDTO getTopicById(Long topicId) {
        Topic topic = findTopicById(topicId);
        List<ResponseDTO> responses = topic.getResponses().stream()
                .sorted(Comparator.comparing(Response::getCreatedAt))
                .map(this::toResponseDTO)
                .toList();

        return toTopicDetailsDTO(topic, responses);
    }

    @Transactional
    public TopicDetailsDTO updateTopic(@Valid InputTopicDTO data, Long topicId) throws MessagingException {
        Topic topic = findTopicById(topicId);
        User user = checkModificationPermission(topic);
        Course course = findCourseById(data.courseId());

        if(!data.title().equals(topic.getTitle())) {
            existsByTitle(data.title());
            validateTitleContent(data.title()); // Validar el contenido del nuevo título con IA
        }

        if(!data.description().equals(topic.getDescription())) {
            existsByDescription(data.description());
            validateDescriptionContent(data.description()); // Validar el contenido de la nueva descripción con IA
        }

        topic.setTitle(data.title());
        topic.setDescription(data.description());
        topic.setCourse(course);

        Topic updatedTopic = topicRepository.save(topic);

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            emailService.notifyTopicEdited(topic);
            notificationService.notifyTopicEdited(topic);
        }

        List<ResponseDTO> responses = updatedTopic.getResponses().stream()
                .filter(response -> !response.getIsDeleted())
                .map(this::toResponseDTO)
                .toList();

        return toTopicDetailsDTO(updatedTopic, responses);
    }

    @Transactional
    public void deleteTopic(Long topicId) throws MessagingException {
        Topic topic = findTopicById(topicId);
        User user = checkModificationPermission(topic);
        topic.setIsDeleted(true);
        //topicRepository.delete(topic);

        if(!user.getUsername().equals(topic.getUser().getUsername())) {
            notificationService.notifyTopicDeleted(topic);
            emailService.notifyTopicDeleted(topic);
        }
    }

    private User getAuthenticatedUser() {
        return commonHelperService.getAuthenticatedUser();
    }

    private Topic findTopicById(Long topicId) {
        return commonHelperService.findTopicById(topicId);
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException("Curso no encontrado.", "NOT_FOUND"));
    }

    private User checkModificationPermission(Topic topic) {
        // Si el usuario es el propietario O tiene permisos elevados, puede modificar el topic
        if (!topic.getUser().equals(getAuthenticatedUser()) && !getAuthenticatedUser().hasElevatedPermissions()) {
            throw new AppException("No tienes permiso para realizar cambios en este tópico", "FORBIDDEN");
        }
        return getAuthenticatedUser();
    }

    private void existsByTitle(String title) {
        if (topicRepository.existsByTitle(title)) {
            throw new AppException("El titulo ya existe.", "CONFLICT");
        }
    }

    private void existsByDescription(String description) {
        if (topicRepository.existsByDescription(description)) {
            throw new AppException("La descripción ya existe.", "CONFLICT");
        }
    }

    private void validateTitleContent(String title) {
        if (!contentValidationService.validateContent(title)) {
            throw new AppException("El título tiene contenido inapropiado.", "FORBIDDEN");
        }
    }

    private void validateDescriptionContent(String description) {
        if (!contentValidationService.validateContent(description)) {
            throw new AppException("La descripción tiene contenido inapropiado.", "FORBIDDEN");
        }
    }

    private TopicDTO toTopicDTO(Topic topic) {
        return commonHelperService.toTopicDTO(topic);
    }

    private TopicDetailsDTO toTopicDetailsDTO(Topic topic, List<ResponseDTO> responses) {

        AuthorDTO author = new AuthorDTO(
                topic.getUser().getUsername(),
                topic.getUser().getProfile().getName()
        );

        CourseDTO course = new CourseDTO(
                topic.getCourse().getId(),
                topic.getCourse().getName(),
                topic.getCourse().getCategory()
        );

        List<TopicFollowerDTO> followers = topic.getFollowedTopics().stream()
                .map(topicFollow -> new TopicFollowerDTO(
                        new UserDTO(
                                topicFollow.getUser().getId(),
                                topicFollow.getUser().getUsername(),
                                topicFollow.getUser().getProfile().getName()
                        ),
                        topicFollow.getFollowedAt()
                ))
                .toList();


        return new TopicDetailsDTO(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                course,
                author,
                responses,
                topic.getStatus(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                followers
        );
    }

    private ResponseDTO toResponseDTO(Response response) {
        return commonHelperService.toResponseDTO(response);
    }

}
