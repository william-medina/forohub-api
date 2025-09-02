package com.williammedina.forohub.controller;

import com.williammedina.forohub.domain.course.entity.Course;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@Transactional
class CourseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @WithUserDetails("William")
    @DisplayName("Debería devolver HTTP 200 cuando la lista de cursos se obtiene exitosamente")
    void getAllCourses_Success() throws Exception {
        createCourse("Course 1", "Description 1");
        createCourse("Course 2", "Description 2");
        var mvcResponse = mvc.perform(get("/api/course"))
                .andReturn().getResponse();
        assertThat(mvcResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        String responseBody = mvcResponse.getContentAsString();
        assertThat(responseBody).contains("Course 1");
        assertThat(responseBody).contains("Course 2");
    }

    private void createCourse(String name, String category) {
        courseRepository.save(new Course(name, category));
    }
}
