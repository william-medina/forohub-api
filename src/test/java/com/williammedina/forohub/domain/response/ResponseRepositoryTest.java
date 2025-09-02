package com.williammedina.forohub.domain.response;

import com.williammedina.forohub.domain.course.entity.Course;
import com.williammedina.forohub.domain.response.entity.Response;
import com.williammedina.forohub.domain.response.repository.ResponseRepository;
import com.williammedina.forohub.domain.topic.entity.Topic;
import com.williammedina.forohub.domain.user.entity.User;
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
class ResponseRepositoryTest {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver las respuestas de un tópico específico")
    void findByTopicId_ReturnsResponsesForTopic() {
        // Arrange
        User user = createAndPersistUser();
        Topic topic = createAndPersistTopic(user);
        createAndPersistResponse(user, topic);
        createAndPersistResponse(user, topic);

        // Act
        List<Response> responses = responseRepository.findByTopicId(topic.getId());

        // Assert
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Debe devolver respuestas de un usuario")
    void findByUserSortedByCreationDate_ReturnsResponsesForUser() {
        // Arrange
        User user = createAndPersistUser();
        Topic topic = createAndPersistTopic(user);
        createAndPersistResponse(user, topic);
        createAndPersistResponse(user, topic);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Response> responsesPage = responseRepository.findByUserSortedByCreationDate(user, pageable);

        // Assert
        assertThat(responsesPage).isNotEmpty();
        assertThat(responsesPage.getContent().getFirst().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Debe contar las respuestas de un usuario")
    void countByUserId_ReturnsResponseCount() {
        // Arrange
        User user = createAndPersistUser();
        Topic topic = createAndPersistTopic(user);
        createAndPersistResponse(user, topic);
        createAndPersistResponse(user, topic);

        // Act
        long responseCount = responseRepository.countByUserId(user.getId());

        // Assert
        assertThat(responseCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe devolver una respuesta por id")
    void findByIdAndIsDeletedFalse_ReturnsResponse() {
        // Arrange
        User user = createAndPersistUser();
        Topic topic = createAndPersistTopic(user);
        Response response = createAndPersistResponse(user, topic);

        // Act
        Optional<Response> foundResponse = responseRepository.findByIdAndIsDeletedFalse(response.getId());

        // Assert
        assertThat(foundResponse).isPresent();
        assertThat(foundResponse.get()).isEqualTo(response);
    }

    private Topic createAndPersistTopic(User user) {
        Topic topic = new Topic(user, "Topic Title", "Topic Description", createAndPersistCourse());
        entityManager.persist(topic);
        return topic;
    }

    private Course createAndPersistCourse() {
        Course course = new Course("Course 1", "Category 1");
        entityManager.persist(course);
        return course;
    }

    private User createAndPersistUser() {
        User user = new User("WilliamM", "williamM@example.com", "password");
        entityManager.persist(user);
        return user;
    }

    private Response createAndPersistResponse(User user, Topic topic) {
        Response response = new Response(user, topic, "Response Content" );
        entityManager.persist(response);
        return response;
    }
}