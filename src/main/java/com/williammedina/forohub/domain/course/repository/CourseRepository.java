package com.williammedina.forohub.domain.course.repository;

import com.williammedina.forohub.domain.course.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findAllByOrderByNameAsc();

}
