package com.williammedina.forohub.domain.topic.dto;

import com.williammedina.forohub.domain.course.dto.CourseDTO;
import com.williammedina.forohub.domain.response.dto.ResponseDTO;
import com.williammedina.forohub.domain.topic.Topic;
import com.williammedina.forohub.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.forohub.domain.user.dto.AuthorDTO;

import java.time.LocalDateTime;
import java.util.List;

public record TopicDetailsDTO(
        Long id,
        String title,
        String description,
        CourseDTO course,
        AuthorDTO author,
        List<ResponseDTO> responses,
        Topic.Status status,
        LocalDateTime createdAt,
        LocalDateTime updateAt,
        List<TopicFollowerDTO> followers
) {
}
