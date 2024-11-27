package com.williammedina.forohub.domain.user;

import com.williammedina.forohub.infrastructure.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.williammedina.forohub.domain")
@ContextConfiguration(classes = {DatabaseConfig.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver un usuario por nombre de usuario")
    void findByUsername_ReturnsUser() {
        // Arrange
        String username = "William";
        createAndPersistUser(username, "william@example.com");

        // Act
        UserDetails userDetails = userRepository.findByUsername(username);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Debe devolver un usuario por email")
    void findByEmail_ReturnsUser() {
        // Arrange
        String email = "william@example.com";
        createAndPersistUser("William", email);

        // Act
        Optional<User> user = userRepository.findByEmail(email);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Debe verificar si un nombre de usuario ya existe")
    void existsByUsername_ReturnsTrueIfUsernameExists() {
        // Arrange
        String username = "William";
        createAndPersistUser(username, "william@example.com");

        // Act
        boolean exists = userRepository.existsByUsername(username);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe verificar si un email ya existe")
    void existsByEmail_ReturnsTrueIfEmailExists() {
        // Arrange
        String email = "william@example.com";
        createAndPersistUser("William", email);

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
        createAndPersistUserWithToken("William", "william@example.com", token);

        // Act
        Optional<User> user = userRepository.findByToken(token);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getToken()).isEqualTo(token);
    }


    private void createAndPersistUser(String username, String email) {
        User user = new User(username, email, "password");
        entityManager.persist(user);
    }

    private void createAndPersistUserWithToken(String username, String email, String token) {
        User user = new User(username, email, "password");
        user.setToken(token);
        entityManager.persist(user);
    }
}
