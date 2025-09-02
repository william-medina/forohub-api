package com.williammedina.forohub.domain.course;

import com.williammedina.forohub.domain.course.entity.Course;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debe devolver los cursos ordenados por nombre de manera ascendente")
    void findAllByOrderByNameAsc_ReturnsCoursesSortedByName() {
        // Arrange
        createAndPersistCourse("1 Java Basics");
        createAndPersistCourse("3 Spring Boot Guide");
        createAndPersistCourse("2 Advanced Java");

        // Act
        List<Course> courses = courseRepository.findAllByOrderByNameAsc();

        // Assert
        assertThat(courses).isNotEmpty();
        assertThat(courses.get(0).getName()).isEqualTo("1 Java Basics");
        assertThat(courses.get(1).getName()).isEqualTo("2 Advanced Java");
        assertThat(courses.get(2).getName()).isEqualTo("3 Spring Boot Guide");
    }

    private void createAndPersistCourse(String name) {
        Course course = new Course(name, "Category");
        entityManager.persist(course);
    }
}
