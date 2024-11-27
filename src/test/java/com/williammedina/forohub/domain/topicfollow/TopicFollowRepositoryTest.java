package com.williammedina.forohub.domain.topicfollow;

import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.infrastructure.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.williammedina.forohub.domain")
@ContextConfiguration(classes = {DatabaseConfig.class})
class TopicFollowRepositoryTest {

    @Autowired
    private TopicFollowRepository topicFollowRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe verificar si un usuario sigue un tema")
    void existsByUserIdAndTopicId_ReturnsTrueIfTopicIsFollowed() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        Topic topic = createAndPersistTopic("Topic title", "Topic description", user, course);
        createAndPersistTopicFollow(user, topic);

        // Act
        boolean exists = topicFollowRepository.existsByUserIdAndTopicId(user.getId(), topic.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver el número de tópicos que sigue un usuario")
    void countByUserId_ReturnsFollowedTopicCount() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        Topic topic1 = createAndPersistTopic("Topic title 1", "Topic description A", user, course);
        Topic topic2 = createAndPersistTopic("Topic title 2", "Topic description B", user, course);
        createAndPersistTopicFollow(user, topic1);
        createAndPersistTopicFollow(user, topic2);

        // Act
        long followedCount = topicFollowRepository.countByUserId(user.getId());

        // Assert
        assertThat(followedCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe devolver los tópicos seguidos por un usuario con filtros de palabra clave")
    void findByUserFilters_ReturnsFilteredFollowedTopics() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        Topic topic = createAndPersistTopic("Topic title", "Topic description", user, course);
        createAndPersistTopicFollow(user, topic);

        String keyword = "title";
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<TopicFollow> page = topicFollowRepository.findByUserFilters(user, keyword, pageable);

        // Assert
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().getTopic().getTitle()).contains(keyword);
    }

    @Test
    @DisplayName("Debe devolver los tópicos seguidos por un usuario")
    void findByUserSortedByCreationDate_ReturnsFollowedTopicsSortedByDate() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        Topic topic1 = createAndPersistTopic("Topic title 1", "Topic description A", user, course);
        Topic topic2 = createAndPersistTopic("Topic title 2", "Topic description B", user, course);
        createAndPersistTopicFollow(user, topic1);
        createAndPersistTopicFollow(user, topic2);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<TopicFollow> page = topicFollowRepository.findByUserSortedByCreationDate(user, pageable);

        // Assert
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Debe eliminar la relación de seguimiento entre un usuario y un tópico")
    void deleteByUserIdAndTopicId_RemovesTopicFollow() {
        // Arrange
        User user = createAndPersistUser();
        Course course = createAndPersistCourse();
        Topic topic = createAndPersistTopic("Topic title", "Topic description", user, course);
        createAndPersistTopicFollow(user, topic);

        // Act
        topicFollowRepository.deleteByUserIdAndTopicId(user.getId(), topic.getId());

        // Assert
        boolean exists = topicFollowRepository.existsByUserIdAndTopicId(user.getId(), topic.getId());
        assertThat(exists).isFalse();
    }

    private Topic createAndPersistTopic(String title, String description, User user, Course course) {
        Topic topic = new Topic(user, title, description, course);
        entityManager.persist(topic);
        return topic;
    }

    private Course createAndPersistCourse() {
        Course course = new Course("Course 1", "Category 1");
        entityManager.persist(course);
        return course;
    }

    private User createAndPersistUser() {
        User user = new User("William", "william@example.com", "password");
        entityManager.persist(user);
        return user;
    }

    private void createAndPersistTopicFollow(User user, Topic topic) {
        TopicFollow topicFollow = new TopicFollow(user, topic);
        entityManager.persist(topicFollow);
    }
}
