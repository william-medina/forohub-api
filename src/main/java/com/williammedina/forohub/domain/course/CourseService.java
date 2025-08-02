package com.williammedina.forohub.domain.course;

import com.williammedina.forohub.domain.course.dto.CourseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByNameAsc();
        return courses.stream().map(CourseDTO::fromEntity).toList();
    }
}
