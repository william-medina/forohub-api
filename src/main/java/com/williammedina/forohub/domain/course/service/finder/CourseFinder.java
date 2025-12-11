package com.williammedina.forohub.domain.course.service.finder;

import com.williammedina.forohub.domain.course.entity.CourseEntity;

public interface CourseFinder {

    CourseEntity findCourseById(Long courseId);

}
