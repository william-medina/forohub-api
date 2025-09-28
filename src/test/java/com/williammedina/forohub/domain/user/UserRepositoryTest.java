package com.williammedina.forohub.domain.user;

import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver un usuario por nombre de usuario")
    void findByUsername_ReturnsUser() {
        // Arrange
        String username = "User";
        createAndPersistUser(username, "user@example.com");

        // Act
        Optional<UserEntity> user = userRepository.findByUsername(username);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Debe devolver un usuario por email")
    void findByEmail_ReturnsUser() {
        // Arrange
        String email = "user@example.com";
        createAndPersistUser("User", email);

        // Act
        Optional<UserEntity> user = userRepository.findByEmail(email);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Debe verificar si un nombre de usuario ya existe")
    void existsByUsername_ReturnsTrueIfUsernameExists() {
        // Arrange
        String username = "User";
        createAndPersistUser(username, "user@example.com");

        // Act
        boolean exists = userRepository.existsByUsername(username);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe verificar si un email ya existe")
    void existsByEmail_ReturnsTrueIfEmailExists() {
        // Arrange
        String email = "user@example.com";
        createAndPersistUser("User", email);

        // Act
        boolean exists = userRepository.existsByEmail(email);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe devolver un usuario por token")
    void findByToken_ReturnsUser() {
        // Arrange
        String token = "123456";
        createAndPersistUserWithToken("User", "user@example.com", token);

        // Act
        Optional<UserEntity> user = userRepository.findByToken(token);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getToken()).isEqualTo(token);
    }


    private void createAndPersistUser(String username, String email) {
        UserEntity user = new UserEntity(username, email, "password");
        entityManager.persist(user);
    }

    private void createAndPersistUserWithToken(String username, String email, String token) {
        UserEntity user = new UserEntity(username, email, "password");
        user.setToken(token);
        entityManager.persist(user);
    }
}
