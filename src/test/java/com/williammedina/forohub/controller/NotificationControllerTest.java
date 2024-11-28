package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.course.CourseRepository;
import com.williammedina.forohub.domain.notification.Notification;
import com.williammedina.forohub.domain.notification.NotificationRepository;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topic.TopicRepository;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando las notificaciones se recuperan exitosamente")
    void getAllNotificationsByUser_Success() throws Exception {
        createNotification("William", "Notification 1");
        createNotification("William", "Notification 2");
        var mvcResponse = mvc.perform(get("/api/notify"))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 204 cuando la notificación se elimina exitosamente")
    void deleteNotification_Success() throws Exception {
        Notification notification = createNotification("William", "Notification to Delete");
        var mvcResponse = mvc.perform(delete("/api/notify/{notifyId}", notification.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        Optional<Notification> deletedNotification = notificationRepository.findById(notification.getId());
        assertThat(deletedNotification).isEmpty();
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario intenta eliminar una notificación que no le pertenece")
    void deleteNotification_Forbidden() throws Exception {
        Notification notification = createNotification("Admin", "Notification to Delete");
        var mvcResponse = mvc.perform(delete("/api/notify/{notifyId}", notification.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la notificación no existe")
    void deleteNotification_NotFound() throws Exception {
        var mvcResponse = mvc.perform(delete("/api/notify/{notifyId}", 0L))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando la notificación se marca como leída exitosamente")
    void markNotificationAsRead_Success() throws Exception {
        Notification notification = createNotification("William", "Notification to Mark as Read");
        var mvcResponse = mvc.perform(patch("/api/notify/{notifyId}", notification.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        Notification updatedNotification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updatedNotification.getIsRead()).isTrue();
    }


    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario intenta marcar como leída una notificación que no le pertenece")
    void markNotificationAsRead_Forbidden() throws Exception {
        Notification notification = createNotification("Admin", "Notification to Mark as Read");
        var mvcResponse = mvc.perform(patch("/api/notify/{notifyId}", notification.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la notificación no existe")
    void markNotificationAsRead_NotFound() throws Exception {
        var mvcResponse = mvc.perform(patch("/api/notify/{notifyId}", 0L))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public Notification createNotification(String username, String title) {
        User user = (User) userRepository.findByUsername(username);
        Optional<Course> course = courseRepository.findById(1L);

        if (user != null && course.isPresent()) {
            Topic topic = new Topic(user, "title", "description", course.get());
            Topic topicSaved = topicRepository.save(topic);
            Notification notification = new Notification(user, topicSaved, null, title, "Content", Notification.Type.TOPIC, Notification.Subtype.REPLY);
            return notificationRepository.save(notification);
        } else {
            throw new IllegalArgumentException("Usuario o curso no encontrado");
        }
    }
}