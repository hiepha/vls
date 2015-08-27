//package com.vlls.jpa.domain;
//
//import org.springframework.data.jpa.domain.AbstractPersistable;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by HoangPhi on 2015/01/14.
// */
//@Entity
//public class Conversation extends AbstractPersistable<Integer> {
//    private static final long serialVersionUID = 1L;
//
//    @Column(name = "user_one_id")
//    private Integer userOneId;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_one_id", insertable = false, updatable = false)
//    private User userOne;
//    @Column(name = "user_two_id")
//    private Integer userTwoId;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_two_id", insertable = false, updatable = false)
//    private User userTwo;
//    @Column
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastUpdate;
//    @OneToMany(mappedBy = "conversation", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    private List<ConversationReply> conversationReplies;
//    @Column(name = "[read]")
//    private Boolean read;
//
//    public Integer getUserOneId() {
//        return userOneId;
//    }
//
//    public void setUserOneId(Integer userOneId) {
//        this.userOneId = userOneId;
//    }
//
//    public Integer getUserTwoId() {
//        return userTwoId;
//    }
//
//    public void setUserTwoId(Integer userTwoId) {
//        this.userTwoId = userTwoId;
//    }
//
//    public User getUserOne() {
//        return userOne;
//    }
//
//    public void setUserOne(User userOne) {
//        this.userOne = userOne;
//    }
//
//    public User getUserTwo() {
//        return userTwo;
//    }
//
//    public void setUserTwo(User userTwo) {
//        this.userTwo = userTwo;
//    }
//
//    public Date getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public void setLastUpdate(Date lastUpdate) {
//        this.lastUpdate = lastUpdate;
//    }
//
//    public List<ConversationReply> getConversationReplies() {
//        return conversationReplies;
//    }
//
//    public void setConversationReplies(List<ConversationReply> conversationReplies) {
//        this.conversationReplies = conversationReplies;
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
