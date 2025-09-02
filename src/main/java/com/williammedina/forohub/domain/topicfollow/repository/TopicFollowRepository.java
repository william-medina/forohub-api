package com.williammedina.forohub.domain.topicfollow.repository;

import com.williammedina.forohub.domain.topicfollow.entity.TopicFollow;
import com.williammedina.forohub.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicFollowRepository extends JpaRepository<TopicFollow, Long> {

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TopicFollow t " +
            "WHERE t.user.id = :userId AND t.topic.id = :topicId " +
            "AND t.topic.isDeleted = false")
    boolean existsByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);

    void deleteByUserIdAndTopicId(Long userId, Long topicId);

    @Query("SELECT COUNT(t) FROM TopicFollow t " +
            "WHERE t.user.id = :id AND t.topic.isDeleted = false")
    long countByUserId(@Param("id") Long id);

    // @Query("SELECT t FROM TopicFollow t " + "LEFT JOIN t.topic.course c " + "WHERE t.user = :user " + "AND (:keyword IS NULL OR (t.topic.title LIKE CONCAT('%', :keyword, '%') OR c.category LIKE CONCAT('%', :keyword, '%'))) " + "ORDER BY t.followedAt DESC")
    @Query("SELECT t FROM TopicFollow t " +
            "LEFT JOIN t.topic.course c " +
            "WHERE t.user = :user " +
            "AND (:keyword IS NULL OR (t.topic.title LIKE CONCAT('%', :keyword, '%') OR c.category LIKE CONCAT('%', :keyword, '%'))) " +
            "AND t.topic.isDeleted = false " +
            "ORDER BY t.followedAt DESC")
    Page<TopicFollow> findByUserFilters(
            User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //@Query("SELECT t FROM TopicFollow t WHERE t.user = :user ORDER BY t.followedAt DESC")
    @Query("SELECT t FROM TopicFollow t " + "WHERE t.user = :user " + "AND t.topic.isDeleted = false " + "ORDER BY t.followedAt DESC")
    Page<TopicFollow> findByUserSortedByCreationDate(User user, Pageable pageable);

}
