package com.williammedina.forohub.controller;

import com.williammedina.forohub.config.TestUtil;
import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.course.CourseRepository;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topic.TopicRepository;
import com.williammedina.forohub.domain.topic.dto.InputTopicDTO;
import com.williammedina.forohub.domain.topicfollow.TopicFollowService;
import com.williammedina.forohub.domain.user.User;
import com.williammedina.forohub.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
class TopicControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<InputTopicDTO> inputTopicDTOJacksonTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicFollowService topicFollowService;

    @Autowired
    private TestUtil testUtil;

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 201 cuando se crea un tópico exitosamente")
    void createTopic_Success() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        InputTopicDTO input = new InputTopicDTO("Valid Title", "Valid Description", 1L);
        var mvcResponse  = mvc.perform(post("/api/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 400 cuando los inputs son inválidos")
    @WithUserDetails("William")
    void createTopic_InvalidInput() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        InputTopicDTO input = new InputTopicDTO("", "", 0L);
        var mvcResponse  = mvc.perform(post("/api/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el curso no existe")
    void createTopic_CourseNotFound() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        InputTopicDTO input = new InputTopicDTO("Valid Title", "Valid Description", 0L);
        var mvcResponse  = mvc.perform(post("/api/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Deberia devolver HTTP 409 cuando el título o la descripción ya existen")
    void createTopic_TitleOrDescriptionExist() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        createTopic("William", 1L, "Duplicate Title", "Duplicate Description");
        InputTopicDTO input = new InputTopicDTO("Duplicate Title", "Duplicate Description", 1L);
        var mvcResponse  = mvc.perform(post("/api/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se obtienen todos los tópicos con éxito")
    void getAllTopics_Success() throws Exception {
        createTopic("William", 1L, "Title", "Description");
        var mvcResponse  = mvc.perform(get("/api/topic")
                        .param("page", "0")
                        .param("size", "6")
                        .param("courseId", "1")
                        .param("keyword", "Title")
                        .param("status", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se obtiene un tópico por ID exitosamente")
    void getTopicById_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Title", "Description");
        var mvcResponse  = mvc.perform(get("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el tópico no existe")
    void getTopicById_NotFound() throws Exception {
        Topic topic = createTopic("William", 1L, "Title", "Description");
        topic.setIsDeleted(true);
        var mvcResponse  = mvc.perform(get("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando el tópico se actualiza exitosamente")
    void updateTopic_Success() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic = createTopic("William", 1L, "Initial Title", "Initial Description");
        InputTopicDTO input = new InputTopicDTO("Updated Valid Title", "Updated Valid Description", 1L);
        var mvcResponse  = mvc.perform(put("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de entrada son inválidos")
    void updateTopic_InvalidInput() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        InputTopicDTO input = new InputTopicDTO("", "", 1L);
        var mvcResponse  = mvc.perform(put("/api/topic/{topicId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el tópico no existe")
    void updateTopic_NotFound() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        InputTopicDTO input = new InputTopicDTO("Updated Title", "Updated Description", 1L);
        var mvcResponse  = mvc.perform(put("/api/topic/{topicId}", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 409 cuando el título o la descripción ya existen en otro tópico")
    void updateTopic_Conflict() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        createTopic("William", 1L, "Existing Title", "Existing Description");
        Topic topic = createTopic("William", 1L, "Title to Update", "Description to Update");
        InputTopicDTO input = new InputTopicDTO("Existing Title", "Existing Description", 1L);
        var mvcResponse  = mvc.perform(put("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para modificar el tópico")
    void updateTopic_NoPermission() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic = createTopic("Admin", 1L, "Title", "Description");
        InputTopicDTO input = new InputTopicDTO("Updated Title", "Updated Description", 1L);
        var mvcResponse  = mvc.perform(put("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputTopicDTOJacksonTester.write(input).getJson())
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 204 cuando un tópico se elimina exitosamente")
    void deleteTopic_Success() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic = createTopic("William", 1L, "Title to Delete", "Description to Delete");
        var mvcResponse  = mvc.perform(delete("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para eliminar este tópico")
    void deleteTopic_Forbidden() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic = createTopic("Admin", 1L, "Title", "Description");
        var mvcResponse  = mvc.perform(delete("/api/topic/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el tópico no existe")
    void deleteTopic_NotFound() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        var mvcResponse  = mvc.perform(delete("/api/topic/{topicId}", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando un usuario sigue o deja de seguir un tópico exitosamente")
    void toggleFollowTopic_Success() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic = createTopic("Admin", 1L, "Topic to Follow", "Description");
        var mvcResponse  = mvc.perform(post("/api/topic/follow/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el tópico no existe")
    void toggleFollowTopic_NotFound() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        var mvcResponse  = mvc.perform(post("/api/topic/follow/{topicId}", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 409 cuando un usuario intenta seguir un tópico creado por él mismo")
    void toggleFollowTopic_Conflict() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic = createTopic("William", 1L, "Topic Created by User", "Description");
        var mvcResponse  = mvc.perform(post("/api/topic/follow/{topicId}", topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando los tópicos seguidos por el usuario se recuperan exitosamente")
    void getFollowedTopicsByUser_Success() throws Exception {
        User user = testUtil.getAuthenticatedUser("William");
        Topic topic1 = createTopic("Admin", 1L, "Followed Topic 1", "Description 1");
        Topic topic2 = createTopic("Admin", 1L, "Followed Topic 2", "Description 2");
        topicFollowService.toggleFollowTopic(topic1.getId());
        topicFollowService.toggleFollowTopic(topic2.getId());
        var mvcResponse  = mvc.perform(get("/api/topic/user/followed-topics")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 con paginación y filtrado de palabras clave")
    void getFollowedTopicsByUser_WithPaginationAndKeyword() throws Exception {

        User user = testUtil.getAuthenticatedUser("William");

        Topic topic1 = createTopic("Admin", 1L, "Followed Topic 1", "Description 1");
        Topic topic2 = createTopic("Admin", 1L, "Followed Topic 2", "Description 2");

        topicFollowService.toggleFollowTopic(topic1.getId());
        topicFollowService.toggleFollowTopic(topic2.getId());
        var mvcResponse  = mvc.perform(get("/api/topic/user/followed-topics")
                        .param("page", "0")
                        .param("size", "5")
                        .param("keyword", "Topic 1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(testUtil.createCookie(user, "access_token", "/", 20000)))
                .andReturn().getResponse();

        // Asserting the response status
        assertThat(mvcResponse .getStatus()).isEqualTo(HttpStatus.OK.value());
        // Optionally, check if the response content matches the filtered results
    }

    public Topic createTopic(String username, Long courseId, String title, String description) {
        User user = (User) userRepository.findByUsername(username);
        Optional<Course> course = courseRepository.findById(courseId);

        if (user != null && course.isPresent()) {
            Topic topic = new Topic(user, title, description, course.get());
            return topicRepository.save(topic);
        } else {
            throw new IllegalArgumentException("Usuario o curso no encontrado");
        }
    }
}
