package com.vlls.jpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Hoang Phi on 10/18/2014.
 */
@Entity
@IdClass(FriendshipKey.class)
public class Friendship implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "friend_one_id")
    private Integer friendOneId;

    @Id
    @Column(name = "friend_two_id")
    private Integer friendTwoId;

    @Column
    private Boolean isFriend;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friend_one_id", insertable = false, updatable = false)
    private User friendOne;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friend_two_id", insertable = false, updatable = false)
    private User friendTwo;

    public Boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Boolean isFriend) {
        this.isFriend = isFriend;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    public User getFriendOne() {
        return friendOne;
    }

    public void setFriendOne(User friendOne) {
        this.friendOne = friendOne;
    }

    public User getFriendTwo() {
        return friendTwo;
    }

    public void setFriendTwo(User friendTwo) {
        this.friendTwo = friendTwo;
    }
}
