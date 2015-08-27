//package com.vlls.jpa.domain;
//
//import org.springframework.data.jpa.domain.AbstractPersistable;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//import java.util.Date;
//
///**
// * Created by HoangPhi on 2015/01/14.
// */
//@Entity
//public class ConversationReply extends AbstractPersistable<Integer> {
//    private static final long serialVersionUID = 1L;
//
//    @Column
//    private String reply;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//    @Column(name = "[time]")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date time;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "conversation_id")
//    private Conversation conversation;
//    @Column(name = "[read]")
//    private Boolean read;
//
//    public String getReply() {
//        return reply;
//    }
//
//    public void setReply(String reply) {
//        this.reply = reply;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Date getTime() {
//        return time;
//    }
//
//    public void setTime(Date time) {
//        this.time = time;
//    }
//
//    public Conversation getConversation() {
//        return conversation;
//    }
//
//    public void setConversation(Conversation conversation) {
//        this.conversation = conversation;
//    }
//
//    public Boolean getRead() {
//        return read;
//    }
//
//    public void setRead(Boolean read) {
//        this.read = read;
//    }
//}
