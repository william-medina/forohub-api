package com.williammedina.forohub.domain.course.repository;

import com.williammedina.forohub.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByOrderByNameAsc();

}
