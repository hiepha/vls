package com.vlls.jpa.domain;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by HoangPhi on 10/13/2014.
 */
@Entity
@IdClass(LikedPictureKey.class)
public class LikedPicture implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "picture_id")
    private Integer pictureId;
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "picture_id", insertable = false, updatable = false)
    private Picture picture;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
