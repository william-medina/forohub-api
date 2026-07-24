package com.williammedina.forohub.domain.reply.service.notifier;

import com.williammedina.forohub.domain.reply.entity.ReplyEntity;
import com.williammedina.forohub.domain.topic.entity.TopicEntity;
import com.williammedina.forohub.domain.user.entity.UserEntity;

public interface ReplyNotifier {

    void notifyNewReply(TopicEntity topic, UserEntity editor);
    void notifyReplyUpdated(ReplyEntity reply, UserEntity editor);
    void notifyReplyDeleted(ReplyEntity reply, UserEntity editor);
    void notifyReplySolution(ReplyEntity reply);

}
