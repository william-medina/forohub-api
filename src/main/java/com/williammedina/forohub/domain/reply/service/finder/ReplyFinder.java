package com.williammedina.forohub.domain.reply.service.finder;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;

public interface ReplyFinder {

    ReplyEntity findReplyById(Long replyId);

}
