package com.vlls.service;

import com.vlls.jpa.domain.Conversation;
import com.vlls.jpa.domain.ConversationReply;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.ConversationReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by HoangPhi on 2015/01/14.
 */
@Service
public class ConversationReplyService extends AbstractService {

    @Autowired
    private ConversationReplyRepository conversationReplyRepository;

    List<ConversationReply> getList0(Integer conversationId, long from, int pageSize) {
        return conversationReplyRepository.findByConversationId(conversationId, new Date(from), pageSize);
    }

    @Transactional
    ConversationReply create0(Conversation conversation, String reply, User currentUser) {
        ConversationReply conversationReply = new ConversationReply();
        conversationReply.setConversation(conversation);
        conversationReply.setReply(reply);
        conversationReply.setUser(currentUser);
        conversationReply.setTime(new Date());
        this.conversationReplyRepository.save(conversationReply);
        return conversationReply;
    }
}
