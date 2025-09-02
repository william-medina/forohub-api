package com.williammedina.forohub.domain.response.repository;

import com.williammedina.forohub.domain.response.entity.Response;
import com.williammedina.forohub.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ResponseRepository extends JpaRepository<Response, Long> {

    @Query("SELECT r FROM Response r WHERE r.topic.isDeleted = false AND r.isDeleted = false AND r.topic.id = :topicId")
    List<Response> findByTopicId(@Param("topicId") Long topicId);

    //@Query("SELECT r FROM Response r WHERE r.user = :user ORDER BY r.createdAt DESC")
    @Query("SELECT r FROM Response r WHERE r.user = :user AND r.topic.isDeleted = false AND r.isDeleted = false ORDER BY r.createdAt DESC")
    Page<Response> findByUserSortedByCreationDate(User user, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.user.id = :id AND r.topic.isDeleted = false AND r.isDeleted = false")
    long countByUserId(@Param("id") Long id);

    @Query("SELECT r FROM Response r WHERE r.id = :responseId AND r.topic.isDeleted = false AND r.isDeleted = false")
    Optional<Response> findByIdAndIsDeletedFalse(@Param("responseId") Long responseId);
}
