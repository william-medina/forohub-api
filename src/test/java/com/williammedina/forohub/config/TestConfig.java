package com.williammedina.forohub.config;

import com.williammedina.forohub.domain.profile.entity.ProfileEntity;
import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
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
            ReplyRepository replyRepository,
            TopicRepository topicRepository
    ) {
        return args -> {
            // Clean up existing data
            replyRepository.deleteAll();
            topicRepository.deleteAll();
            userRepository.deleteAll();

            // Create admin user if it does not exist
            UserEntity admin = new UserEntity("Admin", "admin@example.com", passwordEncoder.encode("password"));
            admin.setProfile(new ProfileEntity(1L, "ADMIN"));
            admin.setAccountConfirmed(true);
            userRepository.save(admin);

            UserEntity user = new UserEntity("William", "william@example.com", passwordEncoder.encode("password"));
            user.setAccountConfirmed(true);
            userRepository.save(user);

            UserEntity unconfirmed = new UserEntity("Unconfirmed", "unconfirmed@example.com", passwordEncoder.encode("password"));
            unconfirmed.setAccountConfirmed(false);
            userRepository.save(unconfirmed);


        };
    }
}