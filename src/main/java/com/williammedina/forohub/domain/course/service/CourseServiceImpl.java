package com.williammedina.forohub.domain.course.service;

import com.williammedina.forohub.domain.course.dto.CourseDTO;
import com.williammedina.forohub.domain.course.entity.Course;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAllByOrderByNameAsc();
        return courses.stream().map(CourseDTO::fromEntity).toList();
    }
}
