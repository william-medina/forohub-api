package com.williammedina.forohub.domain.topicfollow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.williammedina.forohub.domain.topic.entity.Topic;
import com.williammedina.forohub.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "topic_followers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class TopicFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private Topic topic;

    @Column(name = "followed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime followedAt;

    public TopicFollow(User user, Topic topic) {
        this.user = user;
        this.topic = topic;
    }
}
