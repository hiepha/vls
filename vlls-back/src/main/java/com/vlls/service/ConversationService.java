package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.jpa.domain.Conversation;
import com.vlls.jpa.domain.ConversationReply;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.ConversationRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.ConversationPageResponse;
import com.vlls.web.model.ConversationReplyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HoangPhi on 2015/01/14.
 */
@Service
public class ConversationService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Conversation.class);

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConversationReplyService conversationReplyService;

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ConversationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public ConversationPageResponse getList(int page, int pageSize) throws UnauthenticatedException {
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Conversation> conversations = conversationRepository.findByUserOneIdOrUserTwoIdOrderByLastUpdateDesc(
                userSessionInfo.getId(), userSessionInfo.getId(), pageRequest);
        ConversationPageResponse conversationPageResponse = new ConversationPageResponse();
        conversationPageResponse.from(conversations);
        return conversationPageResponse;
    }

    public List<ConversationReplyResponse> get(Integer otherUserId, long from, int pageSize)
            throws UnauthenticatedException {
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
        Conversation conversation = conversationRepository.findByUserId(userSessionInfo.getId(), otherUserId);
        if (conversation == null) {
            return new ArrayList<>(0);
        } else {
            List<ConversationReply> conversationReplies = conversationReplyService.getList0(conversation.getId(), from, pageSize);
            if (conversationReplyService == null) {
                return new ArrayList<>(0);
            } else {
                return conversationReplies.stream().map(conversationReply -> {
                    ConversationReplyResponse conversationReplyResponse = new ConversationReplyResponse();
                    conversationReplyResponse.setData(conversationReply);
                    return conversationReplyResponse;
                }).collect(Collectors.toList());
            }
        }
    }

    @Transactional
    public ConversationReplyResponse send(Integer otherUserId, String reply)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        User user = securityService.retrieveCurrentUser0();
        Conversation conversation = conversationRepository.findByUserId(user.getId(), otherUserId);
        if (conversation == null) {
            conversation = this.create0(otherUserId);
        }
        if (conversation.getConversationReplies() == null) {
            conversation.setConversationReplies(new ArrayList<>());
        }

        ConversationReply conversationReply = conversationReplyService.create0(conversation, reply, user);
        conversation.getConversationReplies().add(conversationReply);

        ConversationReplyResponse conversationReplyResponse = new ConversationReplyResponse();
        conversationReplyResponse.setData(conversationReply);
        return conversationReplyResponse;
    }

    @Transactional
    Conversation create0(Integer userTwoId)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        User userOne = securityService.retrieveCurrentUser0();
        User userTwo = userService.get0(userTwoId);
        Conversation conversation = new Conversation();
        conversation.setUserOneId(userOne.getId());
        conversation.setUserTwoId(userTwoId);
        conversation.setUserOne(userOne);
        conversation.setUserTwo(userTwo);
        conversation.setLastUpdate(new Date());
        conversationRepository.save(conversation);
        return conversation;
    }

    Conversation get0(Integer conversationId)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        Conversation conversation = conversationRepository.findOne(conversationId);
        if (conversation == null) {
            throw new NoInstanceException("Conversation", conversationId);
        } else {
            return conversation;
        }
    }

    @Transactional
    public void sendWithSocket(Integer conversationId, Integer userId, ConversationReplyResponse conversationReplyResponse)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException, ServerTechnicalException {
        Conversation conversation = this.get0(conversationId);
        /* Validate user */
        if (!Objects.equals(userId, conversation.getUserOneId())
                && !Objects.equals(userId, conversation.getUserTwoId())) {
            throw new UnauthorizedException("You do not have permission to do this action");
        }

        User currentUser = userService.get0(userId);
        ConversationReply conversationReply = conversationReplyService.create0(
                conversation, conversationReplyResponse.getReply(), currentUser);
        conversation.getConversationReplies().add(conversationReply);
        this.touch(conversation);

        conversationReplyResponse.setData(conversationReply);
        this.simpMessagingTemplate.convertAndSend("/topic/conversation-socket/" + conversationId, conversationReplyResponse);
    }

    @Transactional
    void touch(Conversation conversation) {
        conversation.setLastUpdate(new Date());
    }
}
