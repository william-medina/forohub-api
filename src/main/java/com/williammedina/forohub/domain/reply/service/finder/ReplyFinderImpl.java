package com.williammedina.forohub.domain.reply.service.finder;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.reply.repository.ReplyRepository;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyFinderImpl implements ReplyFinder{

    private final ReplyRepository replyRepository;

    @Override
    public ReplyEntity findReplyById(Long replyId) {
        return replyRepository.findByIdAndIsDeletedFalse(replyId)
                .orElseThrow(() ->  {
                    log.error("Reply not found with ID: {}", replyId);
                    return new AppException("Respuesta no encontrada", HttpStatus.NOT_FOUND);
                });
    }
}
