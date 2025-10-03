package com.williammedina.forohub.domain.user.repository;

import com.williammedina.forohub.domain.user.entity.RefreshTokenEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findAllByUser(UserEntity user);
}
