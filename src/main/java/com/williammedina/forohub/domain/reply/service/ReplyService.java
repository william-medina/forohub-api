package com.williammedina.forohub.domain.reply.service;

import com.williammedina.forohub.domain.reply.dto.CreateReplyDTO;
import com.williammedina.forohub.domain.reply.dto.ReplyDTO;
import com.williammedina.forohub.domain.reply.dto.UpdateReplyDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReplyService {

    ReplyDTO createReply(@Valid CreateReplyDTO replyRequest);
    Page<ReplyDTO> getAllRepliesByUser(Pageable pageable);
    ReplyDTO updateReply(@Valid UpdateReplyDTO replyRequest, Long replyId);
    void deleteReply(Long replyId);
    ReplyDTO getReplyById(Long replyId);
    ReplyDTO setCorrectReply(Long replyId);

}
