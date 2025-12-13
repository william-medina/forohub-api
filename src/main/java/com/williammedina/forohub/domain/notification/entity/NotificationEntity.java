package com.williammedina.forohub.domain.notification.entity;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "Notification")
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private TopicEntity topic;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private ReplyEntity reply;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subtype subtype;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public NotificationEntity(UserEntity user, TopicEntity topic, ReplyEntity reply, String title, String message, Type type, Subtype subtype) {
        this.user = user;
        this.topic = topic;
        this.reply = reply;
        this.title = title;
        this.message = message;
        this.type = type;
        this.subtype = subtype;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public enum Type {
        TOPIC,
        REPLY
    }

    public enum Subtype {
        REPLY,
        EDITED,
        SOLVED,
        DELETED
    }
}
