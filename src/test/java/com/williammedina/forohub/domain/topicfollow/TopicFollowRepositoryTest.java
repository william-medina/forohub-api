package com.williammedina.forohub.domain.topicfollow;

import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.forohub.domain.topicfollow.repository.TopicFollowRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TopicFollowRepositoryTest {

    @Autowired
    private TopicFollowRepository topicFollowRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe verificar si un usuario sigue un tema")
    void existsByUserIdAndTopicId_ReturnsTrueIfTopicIsFollowed() {
        // Arrange
        UserEntity user = createAndPersistUser();
        CourseEntity course = createAndPersistCourse();
        TopicEntity topic = createAndPersistTopic("Topic title", "Topic description", user, course);
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
        UserEntity user = createAndPersistUser();
        CourseEntity course = createAndPersistCourse();
        TopicEntity topic1 = createAndPersistTopic("Topic title 1", "Topic description A", user, course);
        TopicEntity topic2 = createAndPersistTopic("Topic title 2", "Topic description B", user, course);
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
        UserEntity user = createAndPersistUser();
        CourseEntity course = createAndPersistCourse();
        TopicEntity topic = createAndPersistTopic("Topic title", "Topic description", user, course);
        createAndPersistTopicFollow(user, topic);

        String keyword = "title";
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<TopicFollowEntity> page = topicFollowRepository.findByUserFilters(user, keyword, pageable);

        // Assert
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().getTopic().getTitle()).contains(keyword);
    }

    @Test
    @DisplayName("Debe devolver los tópicos seguidos por un usuario")
    void findByUserSortedByCreationDate_ReturnsFollowedTopicsSortedByDate() {
        // Arrange
        UserEntity user = createAndPersistUser();
        CourseEntity course = createAndPersistCourse();
        TopicEntity topic1 = createAndPersistTopic("Topic title 1", "Topic description A", user, course);
        TopicEntity topic2 = createAndPersistTopic("Topic title 2", "Topic description B", user, course);
        createAndPersistTopicFollow(user, topic1);
        createAndPersistTopicFollow(user, topic2);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<TopicFollowEntity> page = topicFollowRepository.findByUserSortedByCreationDate(user, pageable);

        // Assert
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Debe eliminar la relación de seguimiento entre un usuario y un tópico")
    void deleteByUserIdAndTopicId_RemovesTopicFollow() {
        // Arrange
        UserEntity user = createAndPersistUser();
        CourseEntity course = createAndPersistCourse();
        TopicEntity topic = createAndPersistTopic("Topic title", "Topic description", user, course);
        createAndPersistTopicFollow(user, topic);

        // Act
        topicFollowRepository.deleteByUserIdAndTopicId(user.getId(), topic.getId());

        // Assert
        boolean exists = topicFollowRepository.existsByUserIdAndTopicId(user.getId(), topic.getId());
        assertThat(exists).isFalse();
    }

    private TopicEntity createAndPersistTopic(String title, String description, UserEntity user, CourseEntity course) {
        TopicEntity topic = new TopicEntity(user, title, description, course);
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

    private void createAndPersistTopicFollow(UserEntity user, TopicEntity topic) {
        TopicFollowEntity topicFollow = new TopicFollowEntity(user, topic);
        entityManager.persist(topicFollow);
    }
}
