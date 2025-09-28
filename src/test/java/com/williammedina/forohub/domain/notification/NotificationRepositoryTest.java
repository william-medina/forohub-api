package com.williammedina.forohub.domain.notification;

import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import com.williammedina.forohub.domain.notification.repository.NotificationRepository;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver las notificaciones de un usuario")
    void findAllByUserOrderByCreatedAtDesc_ReturnsNotificationsSortedByDate() {
        // Arrange
        UserEntity user = createAndPersistUser();
        createAndPersistNotification(user);
        createAndPersistNotification(user);

        // Act
        List<NotificationEntity> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);

        // Assert
        assertThat(notifications).isNotEmpty();
        assertThat(notifications).hasSize(2);
    }

    private void createAndPersistNotification(UserEntity user) {
        NotificationEntity notification = new NotificationEntity(user, null, null,"New Notify", "Retry Topic", NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.REPLY);
        entityManager.persist(notification);
    }

    private UserEntity createAndPersistUser() {
        UserEntity user = new UserEntity("WilliamM", "williamM@example.com", "password");
        entityManager.persist(user);
        return user;
    }
}
