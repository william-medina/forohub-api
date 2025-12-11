package com.williammedina.forohub.domain.course.service.finder;

import com.williammedina.forohub.domain.course.entity.CourseEntity;
import com.williammedina.forohub.domain.course.repository.CourseRepository;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseFinderImpl implements CourseFinder {

    private final CourseRepository courseRepository;

    @Override
    public CourseEntity findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found with ID: {}", courseId);
                    return new AppException("Curso no encontrado.", HttpStatus.NOT_FOUND);
                });
    }
}
