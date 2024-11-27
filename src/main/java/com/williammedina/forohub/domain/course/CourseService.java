package com.williammedina.forohub.domain.course;

import com.williammedina.forohub.domain.course.dto.CourseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByNameAsc();
        return courses.stream().map(course -> new CourseDTO(course.getId(), course.getName(), course.getCategory())).toList();
    }
}
