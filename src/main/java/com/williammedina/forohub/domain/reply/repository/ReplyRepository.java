package com.williammedina.forohub.domain.reply.repository;

import com.williammedina.forohub.domain.reply.entity.Reply;
import com.williammedina.forohub.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT r FROM Reply r WHERE r.topic.isDeleted = false AND r.isDeleted = false AND r.topic.id = :topicId")
    List<Reply> findByTopicId(@Param("topicId") Long topicId);

    //@Query("SELECT r FROM Reply r WHERE r.user = :user ORDER BY r.createdAt DESC")
    @Query("SELECT r FROM Reply r WHERE r.user = :user AND r.topic.isDeleted = false AND r.isDeleted = false ORDER BY r.createdAt DESC")
    Page<Reply> findByUserSortedByCreationDate(User user, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reply r WHERE r.user.id = :id AND r.topic.isDeleted = false AND r.isDeleted = false")
    long countByUserId(@Param("id") Long id);

    @Query("SELECT r FROM Reply r WHERE r.id = :replyId AND r.topic.isDeleted = false AND r.isDeleted = false")
    Optional<Reply> findByIdAndIsDeletedFalse(@Param("replyId") Long replyId);
}
