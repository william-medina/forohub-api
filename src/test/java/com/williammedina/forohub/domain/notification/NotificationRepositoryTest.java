package com.williammedina.forohub.domain.notification;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.williammedina.forohub.domain")
@ContextConfiguration(classes = {DatabaseConfig.class})
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver las notificaciones de un usuario")
    void findAllByUserOrderByCreatedAtDesc_ReturnsNotificationsSortedByDate() {
        // Arrange
        User user = createAndPersistUser();
        createAndPersistNotification(user);
        createAndPersistNotification(user);

        // Act
        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);

        // Assert
        assertThat(notifications).isNotEmpty();
        assertThat(notifications).hasSize(2);
    }

    private void createAndPersistNotification(User user) {
        Notification notification = new Notification(user, null, null,"New Notify", "Retry Topic", Notification.Type.TOPIC, Notification.Subtype.REPLY);
        entityManager.persist(notification);
    }

    private User createAndPersistUser() {
        User user = new User("William", "william@example.com", "password");
        entityManager.persist(user);
        return user;
    }
}
