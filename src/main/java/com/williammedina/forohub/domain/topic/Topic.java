package com.williammedina.forohub.domain.topic;

import com.williammedina.forohub.domain.course.Course;
import com.williammedina.forohub.domain.profile.Profile;
import com.williammedina.forohub.domain.response.Response;
import com.williammedina.forohub.domain.topicfollow.TopicFollow;
import com.williammedina.forohub.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("is_deleted = false")
    private List<Response> responses = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicFollow> followedTopics = new ArrayList<>();


    public Topic(User user, String title, String description, Course course) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.course = course;
    }

    public boolean isTopicClosed() {
        return this.status == Status.CLOSED;
    }

    public enum Status {
        ACTIVE,
        CLOSED,
    }
}
