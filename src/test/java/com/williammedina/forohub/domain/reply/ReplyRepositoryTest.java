package com.williammedina.forohub.domain.reply;

import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver las respuestas de un tópico específico")
    void findByTopicId_ReturnsRepliesForTopic() {
        // Arrange
        UserEntity user = createAndPersistUser();
        TopicEntity topic = createAndPersistTopic(user);
        createAndPersistReply(user, topic);
        createAndPersistReply(user, topic);

        // Act
        List<ReplyEntity> responses = replyRepository.findByTopicId(topic.getId());

        // Assert
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Debe devolver respuestas de un usuario")
    void findByUserSortedByCreationDate_ReturnsRepliesForUser() {
        // Arrange
        UserEntity user = createAndPersistUser();
        TopicEntity topic = createAndPersistTopic(user);
        createAndPersistReply(user, topic);
        createAndPersistReply(user, topic);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ReplyEntity> responsesPage = replyRepository.findByUserSortedByCreationDate(user, pageable);

        // Assert
        assertThat(responsesPage).isNotEmpty();
        assertThat(responsesPage.getContent().getFirst().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Debe contar las respuestas de un usuario")
    void countByUserId_ReturnsRepliesCount() {
        // Arrange
        UserEntity user = createAndPersistUser();
        TopicEntity topic = createAndPersistTopic(user);
        createAndPersistReply(user, topic);
        createAndPersistReply(user, topic);

        // Act
        long responseCount = replyRepository.countByUserId(user.getId());

        // Assert
        assertThat(responseCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe devolver una respuesta por id")
    void findByIdAndIsDeletedFalse_ReturnsReply() {
        // Arrange
        UserEntity user = createAndPersistUser();
        TopicEntity topic = createAndPersistTopic(user);
        ReplyEntity response = createAndPersistReply(user, topic);

        // Act
        Optional<ReplyEntity> foundResponse = replyRepository.findByIdAndIsDeletedFalse(response.getId());

        // Assert
        assertThat(foundResponse).isPresent();
        assertThat(foundResponse.get()).isEqualTo(response);
    }

    private TopicEntity createAndPersistTopic(UserEntity user) {
        TopicEntity topic = new TopicEntity(user, "Topic Title", "Topic Description", createAndPersistCourse());
        entityManager.persist(topic);
        return topic;
    }

    private CourseEntity createAndPersistCourse() {
        CourseEntity course = new CourseEntity("Course 1", "Category 1");
        entityManager.persist(course);
        return course;
    }

    private UserEntity createAndPersistUser() {
        UserEntity user = new UserEntity("WilliamM", "williamM@example.com", "password");
        entityManager.persist(user);
        return user;
    }

    private ReplyEntity createAndPersistReply(UserEntity user, TopicEntity topic) {
        ReplyEntity reply = new ReplyEntity(user, topic, "Reply Content" );
        entityManager.persist(reply);
        return reply;
    }
}