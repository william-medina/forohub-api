package com.williammedina.forohub.domain.user.entity;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.profile.entity.ProfileEntity;
import com.williammedina.forohub.domain.topicfollow.entity.TopicFollowEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity(name = "User")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private boolean accountConfirmed = false;

    @Column(length = 255)
    private String token;

    @Column(name = "token_expiration", nullable = false)
    private LocalDateTime tokenExpiration;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicEntity> topics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyEntity> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicFollowEntity> followedTopics = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        generateConfirmationToken();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public void generateConfirmationToken() {
        this.token = UUID.randomUUID().toString();
        this.tokenExpiration = LocalDateTime.now().plusMinutes(20);
    }

    public void clearTokenData() {
        this.token = null;
        this.tokenExpiration = LocalDateTime.now();
    }

    public boolean hasElevatedPermissions() {
        return Set.of("MODERATOR", "INSTRUCTOR", "ADMIN").contains(profile.getName());
    }

    @PrePersist
    public void prePersist() {
        if (this.profile == null) {
            this.profile = new ProfileEntity();
            this.profile.setId(4L);
        }
    }

}
