package com.williammedina.forohub.domain.reply.service;

import com.williammedina.forohub.domain.reply.dto.CreateReplyDTO;
import com.williammedina.forohub.domain.reply.dto.ReplyDTO;
import com.williammedina.forohub.domain.reply.dto.UpdateReplyDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReplyService {

    ReplyDTO createReply(@Valid CreateReplyDTO data) throws MessagingException;
    Page<ReplyDTO> getAllRepliesByUser(Pageable pageable);
    ReplyDTO updateReply(@Valid UpdateReplyDTO data, Long replyId) throws MessagingException;
    void deleteReply(Long replyId) throws MessagingException;
    ReplyDTO getReplyById(Long replyId);
    ReplyDTO setCorrectReply(Long replyId) throws MessagingException;

}
