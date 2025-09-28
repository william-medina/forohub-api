package com.williammedina.forohub.controller;

import com.williammedina.forohub.config.TestConfig;
import com.williammedina.forohub.config.TestUtil;
import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.domain.reply.dto.CreateReplyDTO;
import com.williammedina.forohub.domain.reply.dto.UpdateReplyDTO;
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
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
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
@Import(TestConfig.class)
class ReplyControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateReplyDTO> createReplyDTOJacksonTester;

    @Autowired
    private JacksonTester<UpdateReplyDTO> updateReplyDTOJacksonTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private TestUtil testUtil;

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 201 cuando se crea una respuesta exitosamente")
    void createReply_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        CreateReplyDTO createReplyDTO = new CreateReplyDTO(topic.getId(), "This is a test reply.");
        var mvcResponse  = mvc.perform(
                testUtil.withAuth(
                                post("/api/reply")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createReplyDTOJacksonTester.write(createReplyDTO).getJson()),
                                user
                        )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de entrada son inválidos")
    void createReply_InvalidData() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        CreateReplyDTO createReplyDTO = new CreateReplyDTO(topic.getId(), "");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                    post("/api/reply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createReplyDTOJacksonTester.write(createReplyDTO).getJson()),
                    user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando se intenta agregar una respuesta a un tópico cerrado")
    void createReply_ClosedTopic() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        topic.setStatus(TopicEntity.Status.CLOSED);
        CreateReplyDTO createReplyDTO = new CreateReplyDTO(topic.getId(), "This is a test reply.");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        post("/api/reply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createReplyDTOJacksonTester.write(createReplyDTO).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el tópico no existe")
    void createReply_TopicNotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        createTopic("William", 1L, "Topic for Reply", "Topic Description");
        CreateReplyDTO createReplyDTO = new CreateReplyDTO(0L, "This is a test reply.");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        post("/api/reply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createReplyDTOJacksonTester.write(createReplyDTO).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 y las respuestas del usuario autenticado")
    void getAllRepliesByUser_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        createReply("William", topic, "First reply");
        createReply("William", topic, "Second reply");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        get("/api/reply/user/replies")
                                .contentType(MediaType.APPLICATION_JSON),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se obtiene una respuesta por ID exitosamente")
    void getReplyById_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        ReplyEntity reply = createReply("William", topic, "Test reply message");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        get("/api/reply/{replyId}", reply.getId())
                                .contentType(MediaType.APPLICATION_JSON),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando no se encuentra la respuesta por ID")
    void getReplyById_NotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        Long nonExistentReplyId = 0L;
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        get("/api/reply/{replyId}", nonExistentReplyId)
                                .contentType(MediaType.APPLICATION_JSON),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se actualiza una respuesta exitosamente")
    void updateReply_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        ReplyEntity reply = createReply("William", topic, "Original reply message");
        UpdateReplyDTO updateReplyDTO = new UpdateReplyDTO("This is a updated reply.");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        put("/api/reply/{replyId}", reply.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateReplyDTOJacksonTester.write(updateReplyDTO).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de entrada son inválidos")
    void updateReply_InvalidData() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        ReplyEntity reply = createReply("William", topic, "Original reply message");
        UpdateReplyDTO updateReplyDTO = new UpdateReplyDTO("");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        put("/api/reply/{replyId}", reply.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateReplyDTOJacksonTester.write(updateReplyDTO).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para modificar la respuesta")
    void updateReply_Forbidden() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply", "Topic Description");
        ReplyEntity reply = createReply("Admin", topic, "Original reply message");
        UpdateReplyDTO updateReplyDTO = new UpdateReplyDTO("Updated reply message");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        put("/api/reply/{replyId}", reply.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateReplyDTOJacksonTester.write(updateReplyDTO).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la respuesta no existe")
    void updateReply_NotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        Long nonExistentReplyId = 0L;
        UpdateReplyDTO updateReplyDTO = new UpdateReplyDTO("Updated reply message");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        put("/api/reply/{replyId}", nonExistentReplyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateReplyDTOJacksonTester.write(updateReplyDTO).getJson()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("Admin")
    @DisplayName("Debería devolver HTTP 200 cuando el estado de solución se actualiza correctamente")
    void setCorrectReply_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("Admin");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply Solution", "Topic Description");
        ReplyEntity reply = createReply("William", topic, "Sample reply");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/reply/{replyId}", reply.getId()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para modificar la respuesta")
    void setCorrectReply_Forbidden() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic for Reply Solution", "Topic Description");
        ReplyEntity reply = createReply("William", topic, "Sample reply");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/reply/{replyId}", reply.getId()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("Admin")
    @DisplayName("Debería devolver HTTP 404 cuando la respuesta no existe")
    void setCorrectReply_NotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("Admin");
        Long nonExistentReplyId = 0L;
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        patch("/api/reply/{replyId}", nonExistentReplyId),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 204 cuando la respuesta se elimina exitosamente")
    void deleteReply_Success() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("William", 1L, "Topic to Reply delete", "Topic Description");
        ReplyEntity reply = createReply("William", topic, "Sample reply");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        delete("/api/reply/{replyId}", reply.getId()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para eliminar la respuesta")
    void deleteReply_Forbidden() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        TopicEntity topic = createTopic("Admin", 1L, "Topic to Reply delete", "Topic Description");
        ReplyEntity reply = createReply("Admin", topic, "Sample reply");
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        delete("/api/reply/{replyId}", reply.getId()),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la respuesta no existe")
    void deleteReply_NotFound() throws Exception {
        UserEntity user = testUtil.getAuthenticatedUser("William");
        Long nonExistentReplyId = 0L;
        var mvcResponse = mvc.perform(
                testUtil.withAuth(
                        delete("/api/reply/{replyId}", nonExistentReplyId),
                        user
                )
        ).andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    public TopicEntity createTopic(String username, Long courseId, String title, String description) {
        UserEntity user = testUtil.getAuthenticatedUser(username);

        Optional<CourseEntity> course = courseRepository.findById(courseId);

        if (user != null && course.isPresent()) {
            TopicEntity topic = new TopicEntity(user, title, description, course.get());
            return topicRepository.save(topic);
        } else {
            throw new IllegalArgumentException("Usuario o curso no encontrado");
        }
    }

    public ReplyEntity createReply(String username, TopicEntity topic, String message) {
        UserEntity user = testUtil.getAuthenticatedUser(username);

        if (user != null) {
            ReplyEntity reply = new ReplyEntity(user, topic, message);
            return replyRepository.save(reply);
        } else {
            throw new IllegalArgumentException("Usuario o curso no encontrado");
        }
    }
}