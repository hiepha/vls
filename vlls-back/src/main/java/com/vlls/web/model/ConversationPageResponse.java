package com.vlls.web.model;

import com.vlls.jpa.domain.Conversation;

/**
 * Created by HoangPhi on 2015/01/14.
 */
public class ConversationPageResponse extends AbstractDataPageResponse<Conversation, ConversationResponse> {
    @Override
    public ConversationResponse instantiateResponse() {
        return new ConversationResponse();
    }
}
