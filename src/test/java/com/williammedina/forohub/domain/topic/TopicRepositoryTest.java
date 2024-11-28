package com.williammedina.forohub.domain.topic;

import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.user.User;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver true si existe ese título en otro tópico")
    void existsByTitle_WhenExists_ReturnsTrue() {
        // Arrange
        createAndPersistTopic("Error 404", "Description A" , createAndPersistUser(), createAndPersistCourse());

        // Act
        boolean titleExists = topicRepository.existsByTitle("Error 404");

        // Assert
        assertThat(titleExists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si no existe ese título en otro tópico")
    void existsByTitle_WhenNotExists_ReturnsFalse() {
        // Arrange
        createAndPersistTopic("Error 404", "Description A", createAndPersistUser(), createAndPersistCourse());

        // Act
        boolean titleExists = topicRepository.existsByTitle("Error 500");

        // Assert
        assertThat(titleExists).isFalse();
    }

    @Test
    @DisplayName("Debe devolver true si existe esa descripción en otro tópico")
    void existsByDescription_WhenExists_ReturnsTrue() {
        // Arrange
        createAndPersistTopic("Error 404", "Descripción A", createAndPersistUser(), createAndPersistCourse());

        // Act
        boolean descriptionExists = topicRepository.existsByDescription("Descripción A");

        // Assert
        assertThat(descriptionExists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver false si no existe esa descripción en otro tópico")
    void existsByDescription_WhenNotExists_ReturnsFalse() {
        // Arrange
        createAndPersistTopic("Error 404", "Descripción A", createAndPersistUser(), createAndPersistCourse());

        // Act
        boolean descriptionExists = topicRepository.existsByDescription("Descripción B");

        // Assert
        assertThat(descriptionExists).isFalse();
    }

    @Test
    @DisplayName("Debe devolver el número correcto de tópicos no eliminados para un usuario")
    void countByUserId_WhenExists_ReturnsCorrectCount() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("Topic 1", "Description 1", user, course);
        createAndPersistTopic("Topic 2", "Description 2", user, course);

        // Act
        long topicCount = topicRepository.countByUserId(user.getId());

        // Assert
        assertThat(topicCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe devolver 0 si el usuario no tiene tópicos no eliminados")
    void countByUserId_WhenNoActiveTopics_ReturnsZero() {
        // Arrange
        User user = createAndPersistUser();
        Topic topic = createAndPersistTopic("Deleted Topic", "Description 3", user, createAndPersistCourse());
        topic.setIsDeleted(true);
        entityManager.persist(topic);

        // Act
        long topicCount = topicRepository.countByUserId(user.getId());

        // Assert
        assertThat(topicCount).isEqualTo(0);
    }

    @Test
    @DisplayName("Debe devolver un Optional con el tópico si existe y no está eliminado")
    void findByIdAndNotDeleted_WhenExists_ReturnsTopic() {
        // Arrange
        Topic topic = createAndPersistTopic("Topic 1", "Description 1", createAndPersistUser(), createAndPersistCourse());

        // Act
        Optional<Topic> foundTopic = topicRepository.findByIdAndNotDeleted(topic.getId());

        // Assert
        assertThat(foundTopic).isPresent();
        assertThat(foundTopic.get().getTitle()).isEqualTo("Topic 1");
    }

    @Test
    @DisplayName("Debe devolver un Optional vacío si el tópico está eliminado")
    void findByIdAndNotDeleted_WhenDeleted_ReturnsEmpty() {
        // Arrange
        User user = createAndPersistUser();
        Topic topic = createAndPersistTopic("Deleted Topic", "Description 2", user, createAndPersistCourse());
        topic.setIsDeleted(true);
        entityManager.persist(topic);

        // Act
        Optional<Topic> foundTopic = topicRepository.findByIdAndNotDeleted(topic.getId());

        // Assert
        assertThat(foundTopic).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver una página de tópicos no eliminados")
    void findAllSortedByCreationDate_WhenExists_ReturnsSortedPage() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("Topi 1", "Description A", user, course);
        createAndPersistTopic("Topic 2", "Description B", user, course);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Topic> topicsPage = topicRepository.findAllSortedByCreationDate(pageable);

        // Assert
        assertThat(topicsPage).isNotEmpty();
    }

    @Test
    @DisplayName("Debe devolver una página de tópicos de un usuario")
    void findByUserSortedByCreationDate_WhenExists_ReturnsSortedPage() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("User Topic 1", "Description A", user, course);
        createAndPersistTopic("User Topic 2", "Description B", user, course);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Topic> topicsPage = topicRepository.findByUserSortedByCreationDate(user, pageable);

        // Assert
        assertThat(topicsPage).isNotEmpty();
    }

    @Test
    @DisplayName("Debe devolver una página vacía si el usuario no tiene tópicos no eliminados")
    void findByUserSortedByCreationDate_WhenNoneExists_ReturnsEmptyPage() {
        // Arrange
        User user = createAndPersistUser();
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Topic> topicsPage = topicRepository.findByUserSortedByCreationDate(user, pageable);

        // Assert
        assertThat(topicsPage).isEmpty();
    }

    @Test
    @DisplayName("Debe devolver tópicos de un usuario filtrados por palabra clave")
    void findByUserFilters_WithKeyword_ReturnsFilteredTopics() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("Java Basics", "Description A", user, course);
        createAndPersistTopic("Spring Framework", "Description B", user, course);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Topic> topicsPage = topicRepository.findByUserFilters(user, "Java", pageable);

        // Assert
        assertThat(topicsPage).isNotEmpty();
        assertThat(topicsPage.getContent().getFirst().getTitle()).isEqualTo("Java Basics");
    }

    @Test
    @DisplayName("Debe devolver tópicos de un usuario cuando no hay filtros aplicados")
    void findByUserFilters_WithoutKeyword_ReturnsAllTopics() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("Java Basics", "Description A", user, course);
        createAndPersistTopic("Spring Framework", "Description B", user, course);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Topic> topicsPage = topicRepository.findByUserFilters(user, null, pageable);

        // Assert
        assertThat(topicsPage).hasSize(2);
    }

    @Test
    @DisplayName("Debe devolver tópicos filtrados por curso, palabra clave y estado")
    void findByFilters_WithAllFilters_ReturnsFilteredTopics() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("Java Basics", "Description A", user, course);
        Topic topic = createAndPersistTopic("Spring Framework", "Description B", user, course);
        topic.setStatus(Topic.Status.CLOSED);
        entityManager.persist(topic);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Topic> topicsPage = topicRepository.findByFilters(course.getId(), "Spring", Topic.Status.CLOSED, pageable);

        // Assert
        assertThat(topicsPage).isNotEmpty();
        assertThat(topicsPage.getContent().getFirst().getTitle()).isEqualTo("Spring Framework");
    }

    @Test
    @DisplayName("Debe devolver todos los tópicos cuando no se aplican filtros")
    void findByFilters_WithoutFilters_ReturnsAllTopics() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        createAndPersistTopic("Java Basics", "Description A", user, course);
        createAndPersistTopic("Spring Framework", "Description B", user, course);

        Pageable pageable = PageRequest.of(0, 2);

        // Act
        Page<Topic> topicsPage = topicRepository.findByFilters(null, null, null, pageable);

        // Assert
        assertThat(topicsPage).hasSize(2);
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

    private Topic createAndPersistTopic(String title, String description, User user, Course course) {
        Topic topic = new Topic(user, title, description, course);
        entityManager.persist(topic);
        return topic;
    }
}
