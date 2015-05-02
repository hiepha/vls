package com.vlls.jpa.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Hoang Phi on 10/18/2014.
 */
public class FriendshipKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer friendOneId;
    private Integer friendTwoId;

    public FriendshipKey() {

    }

    public FriendshipKey(Integer friendOneId, Integer friendTwoId) {
        this.friendOneId = friendOneId;
        this.friendTwoId = friendTwoId;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return true;
    }

    public int hashCode(){
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.friendOneId)
                .append(this.friendTwoId)
                .toHashCode();
    }

    public Integer getFriendOneId() {
        return friendOneId;
    }

    public void setFriendOneId(Integer friendOneId) {
        this.friendOneId = friendOneId;
    }

    public Integer getFriendTwoId() {
        return friendTwoId;
    }

    public void setFriendTwoId(Integer friendTwoId) {
        this.friendTwoId = friendTwoId;
    }
}
