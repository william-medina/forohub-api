package com.williammedina.forohub.controller;

import com.williammedina.forohub.config.TestConfig;
import com.williammedina.forohub.config.TestUtil;
import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
import com.williammedina.forohub.domain.notification.entity.NotificationEntity;
import com.williammedina.forohub.domain.notification.repository.NotificationRepository;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.topic.repository.TopicRepository;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import com.williammedina.forohub.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestUtil testUtil;

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando las notificaciones se recuperan exitosamente")
    void getAllNotificationsByUser_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        createNotification("William", "Notification 1");
        createNotification("William", "Notification 2");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(get("/api/notify"), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 204 cuando la notificación se elimina exitosamente")
    void deleteNotification_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        NotificationEntity notification = createNotification("William", "Notification to Delete");

        var mvcResponse = mvc.perform(
                testUtil.withAuth(delete("/api/notify/{notifyId}", notification.getId()), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        Optional<NotificationEntity> deletedNotification = notificationRepository.findById(notification.getId());
        assertThat(deletedNotification).isEmpty();
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario intenta eliminar una notificación que no le pertenece")
    void deleteNotification_Forbidden() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        NotificationEntity notification = createNotification("Admin", "Notification to Delete");

        var mvcResponse = mvc.perform(
                testUtil.withAuth(delete("/api/notify/{notifyId}", notification.getId()), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la notificación no existe")
    void deleteNotification_NotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");

        var mvcResponse = mvc.perform(
                testUtil.withAuth(delete("/api/notify/{notifyId}", 0L), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando la notificación se marca como leída exitosamente")
    void markNotificationAsRead_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        NotificationEntity notification = createNotification("William", "Notification to Mark as Read");

        var mvcResponse = mvc.perform(
                testUtil.withAuth(patch("/api/notify/{notifyId}", notification.getId()), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        NotificationEntity updatedNotification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updatedNotification.getIsRead()).isTrue();
    }


    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario intenta marcar como leída una notificación que no le pertenece")
    void markNotificationAsRead_Forbidden() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");

        NotificationEntity notification = createNotification("Admin", "Notification to Mark as Read");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(patch("/api/notify/{notifyId}", notification.getId()), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la notificación no existe")
    void markNotificationAsRead_NotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");

        var mvcResponse = mvc.perform(
                testUtil.withAuth(patch("/api/notify/{notifyId}", 0L), user)
        ).andReturn().getResponse();

        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public NotificationEntity createNotification(String username, String title) {
        UserEntity user = testUtil.getAuthenticatedUser(username);
        Optional<CourseEntity> course = courseRepository.findById(1L);

        if (user != null && course.isPresent()) {
            TopicEntity topic = new TopicEntity(user, "title", "description", course.get());
            TopicEntity topicSaved = topicRepository.save(topic);
            NotificationEntity notification = new NotificationEntity(user, topicSaved, null, title, "Content", NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.REPLY);
            return notificationRepository.save(notification);
        } else {
            throw new IllegalArgumentException("Usuario o curso no encontrado");
        }
    }
}