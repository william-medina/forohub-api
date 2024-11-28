package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.course.CourseRepository;
import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.response.ResponseRepository;
import com.williammedina.forohub.domain.response.dto.CreateResponseDTO;
import com.williammedina.forohub.domain.response.dto.UpdateResponseDTO;
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
class ResponseControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateResponseDTO> createResponseDTOJacksonTester;

    @Autowired
    private JacksonTester<UpdateResponseDTO> updateResponseDTOJacksonTester;


    @Autowired
    UserRepository userRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ResponseRepository responseRepository;


    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 201 cuando se crea una respuesta exitosamente")
    void createResponse_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        CreateResponseDTO createResponseDTO = new CreateResponseDTO(topic.getId(), "This is a test response.");
        var mvcResponse  = mvc.perform(post("/api/response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createResponseDTOJacksonTester.write(createResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de entrada son inválidos")
    void createResponse_InvalidData() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        CreateResponseDTO createResponseDTO = new CreateResponseDTO(topic.getId(), "");
        var mvcResponse = mvc.perform(post("/api/response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createResponseDTOJacksonTester.write(createResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando se intenta agregar una respuesta a un tópico cerrado")
    void createResponse_ClosedTopic() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        topic.setStatus(Topic.Status.CLOSED);
        CreateResponseDTO createResponseDTO = new CreateResponseDTO(topic.getId(), "This is a test response.");
        var mvcResponse = mvc.perform(post("/api/response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createResponseDTOJacksonTester.write(createResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando el tópico no existe")
    void createResponse_TopicNotFound() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        CreateResponseDTO createResponseDTO = new CreateResponseDTO(0L, "This is a test response.");
        var mvcResponse = mvc.perform(post("/api/response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createResponseDTOJacksonTester.write(createResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 y las respuestas del usuario autenticado")
    void getAllResponsesByUser_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        createResponse("William", topic, "First response");
        createResponse("William", topic, "Second response");
        var mvcResponse = mvc.perform(get("/api/response/user/responses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se obtiene una respuesta por ID exitosamente")
    void getResponseById_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        Response response = createResponse("William", topic, "Test response message");
        var mvcResponse = mvc.perform(get("/api/response/{responseId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando no se encuentra la respuesta por ID")
    void getResponseById_NotFound() throws Exception {
        Long nonExistentResponseId = 0L;
        var mvcResponse = mvc.perform(get("/api/response/{responseId}", nonExistentResponseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando se actualiza una respuesta exitosamente")
    void updateResponse_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        Response response = createResponse("William", topic, "Original response message");
        UpdateResponseDTO updateResponseDTO = new UpdateResponseDTO("Updated response message");
        var mvcResponse = mvc.perform(put("/api/response/{responseId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateResponseDTOJacksonTester.write(updateResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 400 cuando los datos de entrada son inválidos")
    void updateResponse_InvalidData() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        Response response = createResponse("William", topic, "Original response message");
        UpdateResponseDTO updateResponseDTO = new UpdateResponseDTO("");
        var mvcResponse = mvc.perform(put("/api/response/{responseId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateResponseDTOJacksonTester.write(updateResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para modificar la respuesta")
    void updateResponse_Forbidden() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Response", "Topic Description");
        Response response = createResponse("Admin", topic, "Original response message");
        UpdateResponseDTO updateResponseDTO = new UpdateResponseDTO("Updated response message");
        var mvcResponse = mvc.perform(put("/api/response/{responseId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateResponseDTOJacksonTester.write(updateResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la respuesta no existe")
    void updateResponse_NotFound() throws Exception {
        Long nonExistentResponseId = 0L;
        UpdateResponseDTO updateResponseDTO = new UpdateResponseDTO("Updated response message");
        var mvcResponse = mvc.perform(put("/api/response/{responseId}", nonExistentResponseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateResponseDTOJacksonTester.write(updateResponseDTO).getJson()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("Admin")
    @DisplayName("Debería devolver HTTP 200 cuando el estado de solución se actualiza correctamente")
    void setCorrectResponse_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Solution", "Topic Description");
        Response response = createResponse("William", topic, "Sample response");
        var mvcResponse = mvc.perform(patch("/api/response/{responseId}", response.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para modificar la respuesta")
    void setCorrectResponse_Forbidden() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic for Solution", "Topic Description");
        Response response = createResponse("William", topic, "Sample response");
        var mvcResponse = mvc.perform(patch("/api/response/{responseId}", response.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("Admin")
    @DisplayName("Debería devolver HTTP 404 cuando la respuesta no existe")
    void setCorrectResponse_NotFound() throws Exception {
        Long nonExistentResponseId = 0L;
        var mvcResponse = mvc.perform(patch("/api/response/{responseId}", nonExistentResponseId))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 204 cuando la respuesta se elimina exitosamente")
    void deleteResponse_Success() throws Exception {
        Topic topic = createTopic("William", 1L, "Topic to delete", "Topic Description");
        Response response = createResponse("William", topic, "Sample response");
        var mvcResponse = mvc.perform(delete("/api/response/{responseId}", response.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 403 cuando el usuario no tiene permiso para eliminar la respuesta")
    void deleteResponse_Forbidden() throws Exception {
        Topic topic = createTopic("Admin", 1L, "Topic to delete", "Topic Description");
        Response response = createResponse("Admin", topic, "Sample response");
        var mvcResponse = mvc.perform(delete("/api/response/{responseId}", response.getId()))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 404 cuando la respuesta no existe")
    void deleteResponse_NotFound() throws Exception {
        Long nonExistentResponseId = 0L;
        var mvcResponse = mvc.perform(delete("/api/response/{responseId}", nonExistentResponseId))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
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

    public Response createResponse(String username, Topic topic, String message) {
        User user = (User) userRepository.findByUsername(username);

        if (user != null) {
            Response response = new Response(user, topic, message);
            return responseRepository.save(response);
        } else {
            throw new IllegalArgumentException("Usuario o curso no encontrado");
        }
    }
}