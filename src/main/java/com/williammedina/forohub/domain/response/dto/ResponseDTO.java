package com.williammedina.forohub.domain.response.dto;

import com.williammedina.forohub.domain.user.dto.AuthorDTO;

import java.time.LocalDateTime;

public record ResponseDTO(
        Long id,
        String content,
        AuthorDTO author,
        Boolean solution,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
