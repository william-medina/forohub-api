package com.williammedina.forohub.config;

import com.williammedina.forohub.domain.course.CourseRepository;
import com.williammedina.forohub.domain.profile.Profile;
import com.williammedina.forohub.domain.response.ResponseRepository;
import com.williammedina.forohub.domain.topic.TopicRepository;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {

    @Bean
    public CommandLineRunner initializeTestData(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ResponseRepository responseRepository,
            TopicRepository topicRepository
    ) {
        return args -> {
            // Limpieza de datos existentes
            responseRepository.deleteAll();
            topicRepository.deleteAll();
            userRepository.deleteAll();

            // Creación de usuario admin si no existe
            User admin = new User("Admin", "admin@example.com", passwordEncoder.encode("password"));

            admin.setProfile(new Profile(1L, "ADMIN"));
            admin.setAccountConfirmed(true);
            userRepository.save(admin);

            User user = new User("William", "william@example.com", passwordEncoder.encode("password"));
            user.setAccountConfirmed(true);
            userRepository.save(user);

        };
    }
}
