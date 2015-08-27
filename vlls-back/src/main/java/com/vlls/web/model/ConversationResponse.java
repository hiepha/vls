//package com.vlls.web.model;
//
//import com.vlls.jpa.domain.Conversation;
//
///**
// * Created by HoangPhi on 2015/01/14.
// */
//public class ConversationResponse implements DataResponse<Conversation> {
//
//    private int id;
//    private int userOneId;
//    private String userOneName;
//    private String userOneAvatar;
//    private int userTwoId;
//    private String userTwoName;
//    private String userTwoAvatar;
//    private long lastUpdate;
//    private boolean read;
//
//    @Override
//    public void setData(Conversation entity) {
//        this.id = entity.getId();
//        this.userOneId = entity.getUserOne().getId();
//        this.userTwoId = entity.getUserTwo().getId();
//        this.userOneName = entity.getUserOne().getName();
//        this.userTwoName = entity.getUserTwo().getName();
//        this.userOneAvatar = entity.getUserOne().getAvatar();
//        this.userTwoAvatar = entity.getUserTwo().getAvatar();
//        this.lastUpdate = entity.getLastUpdate().getTime();
//        this.read = entity.getRead() == null ? false : entity.getRead();
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
//    public int getUserOneId() {
//        return userOneId;
//    }
//
//    public void setUserOneId(int userOneId) {
//        this.userOneId = userOneId;
//    }
//
//    public String getUserOneName() {
//        return userOneName;
//    }
//
//    public void setUserOneName(String userOneName) {
//        this.userOneName = userOneName;
//    }
//
//    public int getUserTwoId() {
//        return userTwoId;
//    }
//
//    public void setUserTwoId(int userTwoId) {
//        this.userTwoId = userTwoId;
//    }
//
//    public String getUserTwoName() {
//        return userTwoName;
//    }
//
//    public void setUserTwoName(String userTwoName) {
//        this.userTwoName = userTwoName;
//    }
//
//    public long getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public void setLastUpdate(long lastUpdate) {
//        this.lastUpdate = lastUpdate;
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
//    public String getUserOneAvatar() {
//        return userOneAvatar;
//    }
//
//    public void setUserOneAvatar(String userOneAvatar) {
//        this.userOneAvatar = userOneAvatar;
//    }
//
//    public String getUserTwoAvatar() {
//        return userTwoAvatar;
//    }
//
//    public void setUserTwoAvatar(String userTwoAvatar) {
//        this.userTwoAvatar = userTwoAvatar;
//    }
//}
