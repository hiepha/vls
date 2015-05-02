package com.vlls.web.model;

import com.vlls.jpa.domain.Friendship;

/**
 * Created by Hoang Phi on 10/18/2014.
 */
public class FriendshipResponse implements DataResponse<Friendship> {

    private Integer friendOne;
    private Integer friendTwo;
    private Boolean isFriend;

    @Override
    public void setData(Friendship entity) {
        if (entity.getFriendOne() != null) {
            this.friendOne = entity.getFriendOne().getId();
        }
        if (entity.getFriendTwo() != null) {
            this.friendTwo = entity.getFriendTwo().getId();
        }
        if (entity.getIsFriend() != null) {
            this.isFriend = entity.getIsFriend();
        }
    }

    public Integer getFriendOne() {
        return friendOne;
    }

    public Boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Boolean isFriend) {
        this.isFriend = isFriend;
    }

    public void setFriendOne(Integer friendOne) {
        this.friendOne = friendOne;
    }

    public Integer getFriendTwo() {
        return friendTwo;
    }

    public void setFriendTwo(Integer friendTwo) {
        this.friendTwo = friendTwo;
    }
}
