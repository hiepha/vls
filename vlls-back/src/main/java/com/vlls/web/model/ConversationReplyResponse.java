//package com.vlls.web.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.vlls.jpa.domain.ConversationReply;
//
///**
// * Created by HoangPhi on 2015/01/14.
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ConversationReplyResponse implements DataResponse<ConversationReply> {
//
//    private int id;
//    private int userId;
//    private String userName;
//    private String reply;
//    private long time;
//    private boolean read;
//    private int conversationId;
//
//    @Override
//    public void setData(ConversationReply entity) {
//        this.id = entity.getId();
//        this.reply = entity.getReply();
//        this.time = entity.getTime().getTime();
//        this.userName = entity.getUser().getName();
//        this.userId = entity.getUser().getId();
//        this.read = entity.getRead() == null ? false : entity.getRead();
//        this.conversationId = entity.getConversation().getId();
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getReply() {
//        return reply;
//    }
//
//    public void setReply(String reply) {
//        this.reply = reply;
//    }
//
//    public long getTime() {
//        return time;
//    }
//
//    public void setTime(long time) {
//        this.time = time;
//    }
//
//    public boolean isRead() {
//        return read;
//    }
//
//    public void setRead(boolean read) {
//        this.read = read;
//    }
//
//    public int getConversationId() {
//        return conversationId;
//    }
//
//    public void setConversationId(int conversationId) {
//        this.conversationId = conversationId;
//    }
//}
