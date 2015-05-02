package com.vlls.web;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.service.ConversationService;
import com.vlls.web.model.ConversationPageResponse;
import com.vlls.web.model.ConversationReplyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by HoangPhi on 2015/01/14.
 */
@Controller
@RequestMapping("/conversation")
//@MessageMapping("/conversation")
public class ConversationController implements ControllerConstant {

    @Autowired
    private ConversationService conversationService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ConversationPageResponse getList(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
            throws UnauthenticatedException {
        return conversationService.getList(page, pageSize);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ConversationReplyResponse> get(
            @PathVariable("userId") Integer userId,
            @RequestParam long from,
            @RequestParam(defaultValue = MESSAGE_DEFAULT_PAGE_SIZE) int pagesize)
            throws UnauthenticatedException {
        return conversationService.get(userId, from, pagesize);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ConversationReplyResponse send(
            @PathVariable("userId") Integer userId,
            @RequestParam String reply)
            throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        return conversationService.send(userId, reply);
    }

    @MessageMapping("/conversation-socket/{conversationId}/{userId}")
    public void sock(
            @DestinationVariable Integer conversationId,
            @DestinationVariable Integer userId,
            ConversationReplyResponse message) throws Exception {
        conversationService.sendWithSocket(conversationId, userId, message);
    }
}
