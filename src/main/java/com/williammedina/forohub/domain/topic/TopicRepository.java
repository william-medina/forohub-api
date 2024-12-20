package com.williammedina.forohub.domain.topic;

import com.williammedina.forohub.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TopicRepository extends JpaRepository<Topic, Long> {

    // @Query("SELECT t FROM Topic t ORDER BY t.createdAt DESC")
    @Query("SELECT t FROM Topic t WHERE t.isDeleted = false ORDER BY t.createdAt DESC")
    Page<Topic> findAllSortedByCreationDate(Pageable pageable);

    // @Query("SELECT t FROM Topic t " + "LEFT JOIN t.course c " + "WHERE (:courseId IS NULL OR c.id = :courseId) " + "AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%') OR c.category LIKE CONCAT('%', :keyword, '%')) " + "AND (:status IS NULL OR t.status = :status) " + "ORDER BY t.createdAt DESC")
    @Query("SELECT t FROM Topic t " +
            "LEFT JOIN t.course c " +
            "WHERE t.isDeleted = false " +
            "AND (:courseId IS NULL OR c.id = :courseId) " +
            "AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%') OR c.category LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "ORDER BY t.createdAt DESC")
    Page<Topic> findByFilters(
            @Param("courseId") Long courseId,
            @Param("keyword") String keyword,
            @Param("status") Topic.Status status,
            Pageable pageable
    );

    // @Query("SELECT t FROM Topic t " + "LEFT JOIN t.course c " + "WHERE (t.user = :user) " + "AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%') OR c.category LIKE CONCAT('%', :keyword, '%')) " + "ORDER BY t.createdAt DESC")
    @Query("SELECT t FROM Topic t " +
            "LEFT JOIN t.course c " +
            "WHERE t.isDeleted = false " +
            "AND t.user = :user " +
            "AND (:keyword IS NULL OR t.title LIKE CONCAT('%', :keyword, '%') OR c.category LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY t.createdAt DESC")
    Page<Topic> findByUserFilters(
            User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //@Query("SELECT t FROM Topic t WHERE t.user = :user ORDER BY t.createdAt DESC")
    @Query("SELECT t FROM Topic t WHERE t.user = :user AND t.isDeleted = false ORDER BY t.createdAt DESC")
    Page<Topic> findByUserSortedByCreationDate(User user, Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE t.title = :title AND t.isDeleted = false")
    boolean existsByTitle(@Param("title") String title);

    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE t.description = :description AND t.isDeleted = false")
    boolean existsByDescription(@Param("description") String description);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.user.id = :userId AND t.isDeleted = false")
    long countByUserId(@Param("userId") Long id);

    @Query("SELECT t FROM Topic t WHERE t.id = :topicId AND t.isDeleted = false")
    Optional<Topic> findByIdAndNotDeleted(@Param("topicId") Long topicId);

}
