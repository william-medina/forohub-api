package com.williammedina.forohub.domain.topicfollow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "TopicFollow")
@Table(name = "topic_followers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class TopicFollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private TopicEntity topic;

    @Column(name = "followed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime followedAt;

    public TopicFollowEntity(UserEntity user, TopicEntity topic) {
        this.user = user;
        this.topic = topic;
    }
}
